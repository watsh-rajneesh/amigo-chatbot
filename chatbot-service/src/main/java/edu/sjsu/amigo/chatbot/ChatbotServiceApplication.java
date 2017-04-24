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

package edu.sjsu.amigo.chatbot;

import edu.sjsu.amigo.chatbot.rest.ChatResource;
import edu.sjsu.amigo.json.util.EndpointUtils;
import edu.sjsu.amigo.scheduler.jobs.JobManager;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import lombok.extern.java.Log;

/**
 * @author rwatsh on 4/21/17.
 */
@Log
public class ChatbotServiceApplication extends Application<ChatbotConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ChatbotServiceApplication().run(args);
    }

    @Override
    public void run(ChatbotConfiguration chatbotConfiguration, Environment environment) throws Exception {
        //Start the job scheduler
        JobManager.getInstance().startScheduler();
        /*
         * Register resources with jersey.
         */
        final ChatResource chatResource = new ChatResource();

        /*
         * Setup jersey environment.
         */
        environment.jersey().setUrlPattern(EndpointUtils.ENDPOINT_ROOT + "/*");
        environment.jersey().register(chatResource);
        log.info("Done with all initializations for user service");
    }
}
