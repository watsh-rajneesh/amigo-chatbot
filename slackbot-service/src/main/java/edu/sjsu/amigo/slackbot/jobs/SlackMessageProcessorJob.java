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

package edu.sjsu.amigo.slackbot.jobs;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import edu.sjsu.amigo.json.util.JsonUtils;
import edu.sjsu.amigo.mp.model.SlackMessage;
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
 * Sends message to chatbot service asynchronously.
 *
 * @author rwatsh on 4/23/17.
 */
@Log
public class SlackMessageProcessorJob implements Job {

    private String parsedMessage;
    private SlackUser messageSender;
    private SlackSession session;
    private SlackChannel channel;
    private String botToken;
    // TODO change it
    protected static final String BASE_URI = "http://localhost:8080";
    public static final String RESOURCE_URI = "/api/v1.0/chat";


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            //message = jobdata params.
            JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
            parsedMessage = jobDataMap.getString(JobConstants.JOB_PARAM_MESSAGE);
            messageSender = (SlackUser)jobDataMap.get(JobConstants.JOB_PARAM_MSG_SENDER);
            session = (SlackSession)jobDataMap.get(JobConstants.JOB_PARAM_SLACK_SESSION);
            channel = (SlackChannel) jobDataMap.get(JobConstants.JOB_PARAM_SLACK_CHANNEL);
            botToken = jobDataMap.getString(JobConstants.JOB_PARAM_BOT_TOK);

            // Respond to user that we are working on it...
            String reply = "Working on it...";
            if (channel != null) {
                session.sendMessage(channel, reply);
            } else {
                session.sendMessageToUser(messageSender, reply, null);
            }

            // Send message to chatbot service's /chat endpoint.
            String curTime = new Date().toString();
            SlackMessage msg = new SlackMessage(curTime,messageSender.getUserMail(),
                    messageSender.getUserName(),
                    parsedMessage,
                    channel.getName(),
                    botToken);
            String jsonStr = null;
            try {
                jsonStr = JsonUtils.convertObjectToJson(msg);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error converting chat object to JSON string", e);
                return;
            }
            if (jsonStr != null) {
                Client client = ClientBuilder.newClient();
                WebTarget webTarget = client.target(BASE_URI);
                try {
                    Response response = webTarget.path(RESOURCE_URI)
                            .request()
                            .accept(MediaType.APPLICATION_JSON_TYPE)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .post(Entity.json(jsonStr));
                    if (response.getStatus() != Response.Status.ACCEPTED.getStatusCode()) {
                        log.severe(MessageFormat.format("Failed to send the message [{0}] to chatbot service", parsedMessage));
                    }
                } finally {
                    if (client != null) {
                        client.close();
                    }
                }
            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
