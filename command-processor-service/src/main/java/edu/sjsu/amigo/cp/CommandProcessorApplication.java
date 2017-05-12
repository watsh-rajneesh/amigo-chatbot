/*
 * Copyright (c) 2017 San Jose State University.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package edu.sjsu.amigo.cp;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sjsu.amigo.command.db.dao.ProviderDAO;
import edu.sjsu.amigo.command.db.model.Provider;
import edu.sjsu.amigo.cp.health.DBHealthCheck;
import edu.sjsu.amigo.cp.kafka.ConsumerLoop;
import edu.sjsu.amigo.cp.rest.RequestResource;
import edu.sjsu.amigo.db.common.DBClient;
import edu.sjsu.amigo.db.common.DBException;
import edu.sjsu.amigo.db.common.model.ValidationException;
import edu.sjsu.amigo.json.util.EndpointUtils;
import edu.sjsu.amigo.scheduler.jobs.JobManager;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static edu.sjsu.amigo.mp.kafka.MessageQueueConstants.AMIGO_CHATBOT_GROUP;
import static edu.sjsu.amigo.mp.kafka.MessageQueueConstants.USER_MSG_TOPIC;

/**
 * Command processor application. This module's job is to get the message from kafka MQ and lookup the command to
 * execute on the provider.
 *
 * @author rwatsh on 2/26/17.
 */
@Log
public class CommandProcessorApplication extends Application<CommandProcessorConfiguration> {
    // AWS provider intents to command mapping file.
    public static final String AWS_INTENT_COMMAND_MAPPINGS_JSON = "aws_intent_command_mappings.json";
    private DBClient dbClient;

    public static void main(String[] args) throws Exception {
        new CommandProcessorApplication().run(args);
    }

    @Override
    public void initialize(final Bootstrap<CommandProcessorConfiguration> bootstrap) {
        /*
         * Register the static html contents to be served from /assets directory and accessible from browser from
         * http://<host>:<port>/openstack
         */
        //bootstrap.addBundle(new AssetsBundle("/assets", "/openstack", "index.html"));

    }

    @Override
    public void run(CommandProcessorConfiguration commandProcessorConfiguration, Environment environment) throws Exception {
        log.info("Initializing db client");
        dbClient = commandProcessorConfiguration.getDbConfig().build(environment);
        log.info("db connect string: " + dbClient.getConnectString());
        log.info("Connected to db: " + dbClient.getConnectString());

        initCommandDB(dbClient);

        initKafkaMessageConsumerLoop();

        environment.healthChecks().register("database", new DBHealthCheck(dbClient));

        /*
         * Register resources with jersey.
         */
        final RequestResource requestResource = new RequestResource(dbClient);

        /*
         * Setup jersey environment.
         */
        environment.jersey().setUrlPattern(EndpointUtils.ENDPOINT_ROOT + "/*");
        environment.jersey().register(requestResource);
        log.info("Done with all initializations for user service");


        // Start the job scheduler
        JobManager.getInstance().startScheduler();
    }

    /**
     * Read the provider intents to command mappings files and populate the provider collection in the DB.
     *
     * TODO currently it is only reading one provider "AWS" specific json but the same approach can be used to
     * load any other provider specific intents to command mappings data into DB.
     *
     * @param dbClient
     * @throws IOException
     * @throws ValidationException
     * @throws DBException
     */
    private void initCommandDB(DBClient dbClient) throws IOException, ValidationException, DBException {
        ObjectMapper mapper = new ObjectMapper();
        File f = new File(AWS_INTENT_COMMAND_MAPPINGS_JSON);
        if (f.exists()) {
            Provider provider = mapper.readValue(f, Provider.class);
            if (provider.isValid()) {
                ProviderDAO providerDAO = (ProviderDAO) dbClient.getDAO(ProviderDAO.class);
                providerDAO.addOrUpdate(provider, provider.getCloudProvider());
            } else {
                throw new RuntimeException("aws_intent_command_mappings.json invalid data");
            }
        } else {
            throw new RuntimeException("aws_intent_command_mappings.json file not found");
        }
    }

    /**
     * Initialize message consumer executors for Kafka message queue.
     *
     */
    private void initKafkaMessageConsumerLoop() {
        int numConsumers = 3;
        String groupId = AMIGO_CHATBOT_GROUP;
        List<String> topics = Arrays.asList(USER_MSG_TOPIC);
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);

        final List<ConsumerLoop> consumers = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
            ConsumerLoop consumer = new ConsumerLoop(i, groupId, topics, dbClient);
            consumers.add(consumer);
            executor.submit(consumer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (ConsumerLoop consumer : consumers) {
                    consumer.shutdown();
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
