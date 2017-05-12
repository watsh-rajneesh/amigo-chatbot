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

package edu.sjsu.amigo.riabot.jobs;

import edu.sjsu.amigo.json.util.JsonUtils;
import edu.sjsu.amigo.mp.model.RiaMessage;
import edu.sjsu.amigo.scheduler.jobs.JobConstants;
import lombok.extern.java.Log;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * @author rwatsh on 4/23/17.
 */
@Log
public class RiabotMessageProcessorJob implements Job {
    private RiaMessage message;
    private static final String PROXY_HOST_NAME = System.getenv("PROXY_HOST_NAME");
    protected static final String BASE_URI = "http://" + PROXY_HOST_NAME;
    public static final String RESOURCE_URI = "/api/v1.0/chat";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
            message = (RiaMessage)jobDataMap.get(JobConstants.JOB_PARAM_MESSAGE);

            // send to /chat endpoint
            // Send message to chatbot service's /chat endpoint.
            String curTime = new Date().toString();

            String jsonStr = null;
            try {
                jsonStr = JsonUtils.convertObjectToJson(message);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error converting chat object to JSON string", e);
                return;
            }
            if (jsonStr != null) {
                log.info(MessageFormat.format("Sending message to chatbot [{0}] at [{1}]", jsonStr, BASE_URI + RESOURCE_URI));
                Client client = ClientBuilder.newClient();
                WebTarget webTarget = client.target(BASE_URI);
                Response response = null;
                try {
                    response = webTarget.path(RESOURCE_URI)
                            .request()
                            .accept(MediaType.APPLICATION_JSON_TYPE)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .post(Entity.json(jsonStr));

                    log.info("Received response from chatbot: " + response);
                    if (response.getStatus() != Response.Status.ACCEPTED.getStatusCode()) {
                        log.severe(MessageFormat.format("Failed to send the message [{0}] to chatbot service", message.toString()));
                    }
                } finally {
                    if (response != null) {
                        response.close();
                    }
                    if (client != null) {
                        client.close();
                    }
                }
            }
        } catch(Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
