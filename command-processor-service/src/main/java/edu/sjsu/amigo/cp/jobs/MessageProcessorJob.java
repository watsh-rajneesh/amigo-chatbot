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

package edu.sjsu.amigo.cp.jobs;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import edu.sjsu.amigo.cp.api.CloudProviderFactory;
import edu.sjsu.amigo.cp.api.Command;
import edu.sjsu.amigo.cp.api.CommandExecutionException;
import edu.sjsu.amigo.cp.api.CommandExecutor;
import edu.sjsu.amigo.cp.api.Response;
import edu.sjsu.amigo.mp.kafka.SlackMessage;
import edu.sjsu.amigo.json.util.JsonUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Job to process the incoming message asynchronously.
 *
 * @author rwatsh on 2/27/17.
 */
public class MessageProcessorJob implements Job {
    private String message;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            //message = jobdata params.
            SlackMessage slackMessage = JsonUtils.convertJsonToObject(message, SlackMessage.class);
            if (slackMessage != null) {
                String slackBotToken = slackMessage.getSlackBotToken();

                SlackSession session = SlackSessionFactory.createWebSocketSlackSession(slackBotToken);
                session.connect();
                String channelId = slackMessage.getChannelId();
                List<String> intent = slackMessage.getIntent();
                //List<IntentElem> intentsList = null;

/*                *//**
                 * Only if the intent string is a JSON array means wit.ai returned some intents for the message.
                 *//*
                if (JsonUtils.parseJson(intent).isArray()) {
                    intentsList = JsonUtils.convertJsonArrayToList(intent, IntentElem.class);
                }*/

                String userEmail = slackMessage.getUserEmail();

                String ackMessage = "Message received in the backend";
                sendMessageToUser(userEmail, session, channelId, ackMessage);

                // 1. Get command to execute from intent
                String[] cmdArray = getCmdArray(intent);
                String providerName = cmdArray[0];

                // 2. Make a REST call to user service to get the user's (by userEmail) AWS creds.
                List<String> envList = getCloudProviderCreds(providerName);

                // Execute Command


                List<String> cmdList = new ArrayList<>();
                for (int i = 1; i < cmdArray.length; i++) {
                    cmdList.add(cmdArray[i]);
                }
                String dockerImage = "sjsucohort6/docker_awscli:latest";
                String entryPoint = "aws";
                Command cmd = new Command.Builder(dockerImage, cmdList)
                        .env(envList)
                        .entryPoint(entryPoint)
                        .build();
                CommandExecutor executor = CloudProviderFactory.getCloudProviderCmdExecutor(providerName);
                Response response = executor.executeCommand(cmd);
                sendMessageToUser(userEmail, session, channelId, response.getMsg());
            }
        } catch (IOException | CommandExecutionException e) {
            throw new JobExecutionException(e);
        }
    }

    private List<String> getCloudProviderCreds(String providerName) {
        // TODO Get this from /users/{userId} endpoint.
        List<String> envList = new ArrayList<>();
        envList.add("AWS_DEFAULT_REGION="+ System.getenv("AWS_DEFAULT_REGION"));
        envList.add("AWS_ACCESS_KEY_ID="+ System.getenv("AWS_ACCESS_KEY_ID"));
        envList.add("AWS_SECRET_ACCESS_KEY="+ System.getenv("AWS_SECRET_ACCESS_KEY"));
        return envList;
    }

    private String[] getCmdArray(List<String> intent) {
        //TODO Change to use DB to get the command array from intent
        // 1. Lookup intentsList in the DB and get the following:
        // - dockerImage, cmdList and entry point.

        /*
         * For simplicity sake, we will lookup with intentsList such that,
         * - each intent element in the intentsList should be found in a single NoSQL document
         * - then we get the cloud provider name, dockerImage, cmdList and entry point from the DB.
         *
         * If intent string is not an array, then we try getting the cloud provider from the message as first
         * word in the intent.
         */
        String[] cmdArray = new String[intent.size()];
        cmdArray = intent.toArray(cmdArray);
        return cmdArray;
    }

    private void sendMessageToUser(String userEmail, SlackSession session, String channelId, String message) {
        if (channelId != null && !channelId.trim().isEmpty()) {
            SlackChannel channel = session.findChannelById(channelId);
            session.sendMessage(channel, message);
        } else {

            if (userEmail != null && !userEmail.trim().isEmpty()) {
                SlackUser slackUser = session.findUserByEmail(userEmail);
                session.sendMessageToUser(slackUser, message, null);
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
