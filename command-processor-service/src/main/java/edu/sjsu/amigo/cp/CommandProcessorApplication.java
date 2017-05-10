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

import edu.sjsu.amigo.cp.health.DBHealthCheck;
import edu.sjsu.amigo.cp.kafka.ConsumerLoop;
import edu.sjsu.amigo.cp.rest.RequestResource;
import edu.sjsu.amigo.db.common.DBClient;
import edu.sjsu.amigo.json.util.EndpointUtils;
import edu.sjsu.amigo.scheduler.jobs.JobManager;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static edu.sjsu.amigo.mp.kafka.MessageQueueConstants.AMIGO_CHATBOT_GROUP;
import static edu.sjsu.amigo.mp.kafka.MessageQueueConstants.USER_MSG_TOPIC;

/**
 * @author rwatsh on 2/26/17.
 */
@Log
public class CommandProcessorApplication  extends Application<CommandProcessorConfiguration> {
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

    private void initKafkaMessageConsumerLoop() {
        int numConsumers = 3;
        String groupId = AMIGO_CHATBOT_GROUP;
        List<String> topics = Arrays.asList(USER_MSG_TOPIC);
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);

        final List<ConsumerLoop> consumers = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
            ConsumerLoop consumer = new ConsumerLoop(i, groupId, topics);
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
