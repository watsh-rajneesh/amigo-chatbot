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

import com.fasterxml.jackson.databind.JsonNode;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import edu.sjsu.amigo.command.db.dao.ProviderDAO;
import edu.sjsu.amigo.command.db.dao.RequestDAO;
import edu.sjsu.amigo.command.db.model.Provider;
import edu.sjsu.amigo.command.db.model.Request;
import edu.sjsu.amigo.command.db.model.Status;
import edu.sjsu.amigo.cp.api.CloudProviderFactory;
import edu.sjsu.amigo.cp.api.Command;
import edu.sjsu.amigo.cp.api.CommandExecutionException;
import edu.sjsu.amigo.cp.api.CommandExecutor;
import edu.sjsu.amigo.cp.api.Response;
import edu.sjsu.amigo.db.common.DBClient;
import edu.sjsu.amigo.db.common.DBException;
import edu.sjsu.amigo.http.client.HttpClient;
import edu.sjsu.amigo.json.util.JsonUtils;
import edu.sjsu.amigo.mp.model.BotType;
import edu.sjsu.amigo.mp.model.RiaMessage;
import edu.sjsu.amigo.mp.model.SlackMessage;
import edu.sjsu.amigo.scheduler.jobs.JobConstants;
import edu.sjsu.amigo.user.db.model.User;
import lombok.extern.java.Log;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * Job to process the incoming message asynchronously.
 *
 * @author rwatsh on 2/27/17.
 */
@Log
public class MessageProcessorJob implements Job {
    private String message;
    private static final String BASE_URI = "http://localhost:8080";
    private static final String RESOURCE_URI = "/api/v1.0/users";
    private DBClient dbClient;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            //message = jobdata params.
            JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
            String message = jobDataMap.getString(JobConstants.JOB_PARAM_MESSAGE);
            dbClient = (DBClient) jobDataMap.get(JobConstants.JOB_PARAM_DBCLIENT);
            RequestDAO requestDAO = (RequestDAO) dbClient.getDAO(RequestDAO.class);
            ProviderDAO providerDAO = (ProviderDAO) dbClient.getDAO(ProviderDAO.class);


            JsonNode jsonNode = JsonUtils.parseJson(message);
            String botType = jsonNode.path("botType").asText();

            // shared variables between either of the bot types.
            String messageIntent = null;
            String commandStr = null;
            String dockerImage = "sjsucohort6/docker_awscli";
            String providerName = "aws";
            String content = null;
            List<String> envList = null;
            String requestId = null;

            // only for slack
            String userEmail = null;
            String channelId = null;
            SlackSession session = null;


            if (botType.equalsIgnoreCase(BotType.SLACK.name())) {
                SlackMessage slackMessage = JsonUtils.convertJsonToObject(message, SlackMessage.class);
                List<Request> requestEntity = requestDAO.fetchById(new ArrayList<String>() {{
                    add(slackMessage.getRequestId());
                }});
                // Only process the message if the requestID in the message is unique and seen first time
                // This is done to prevent from duplicate message processing.
                if (slackMessage != null && (requestEntity == null || requestEntity.isEmpty())) {
                    String slackBotToken = slackMessage.getSlackBotToken();
                    // set request in progress
                    Request request = requestInProgress(slackMessage.getRequestId());
                    Request finalRequest = request;
                    requestDAO.add(new ArrayList<Request>() {{
                        add(finalRequest);
                    }});

                    session = SlackSessionFactory.createWebSocketSlackSession(slackBotToken);
                    session.connect();
                    channelId = slackMessage.getChannelId();
                    userEmail = slackMessage.getUserEmail();

                    // send ack to slack user that message is being processed.
                    String ackMessage = "Message received in the backend";
                    sendMessageToUser(userEmail, session, channelId, ackMessage);

                    // Make a REST call to user service to get the user's (by userEmail) AWS creds.
                    envList = getCloudProviderCredsByEmail(userEmail);

                    content = slackMessage.getContent();
                    requestId = slackMessage.getRequestId();
                }
            } else if (botType.equalsIgnoreCase(BotType.RIA.name())) {
                RiaMessage riaMessage = JsonUtils.convertJsonToObject(message, RiaMessage.class);
                List<Request> requestEntity = requestDAO.fetchById(new ArrayList<String>() {{
                    add(riaMessage.getRequestId());
                }});
                if (riaMessage != null && (requestEntity == null || requestEntity.isEmpty())) {
                    String riaId = riaMessage.getRiaId();

                    // set request instance in DB with in progress status
                    Request request = requestInProgress(riaMessage.getRequestId());
                    Request finalRequest = request;
                    requestDAO.add(new ArrayList<Request>() {{
                        add(finalRequest);
                    }});

                    envList = getCloudProviderCredsByRiaId(riaMessage.getRiaId());

                    content = riaMessage.getContent();
                    requestId = riaMessage.getRequestId();
                }
            }
            // Common logic for all bot types to find intent for a given provider.
            IntentFinder intentFinder = new IntentFinder(providerDAO, content, messageIntent, commandStr, dockerImage, providerName).invoke();
            messageIntent = intentFinder.getMessageIntent();
            commandStr = intentFinder.getCommandStr();
            dockerImage = intentFinder.getDockerImage();
            providerName = intentFinder.getProviderName();
            List<String> cmdList = null;

            if (messageIntent != null && commandStr != null) {
                String ackMessage = MessageFormat.format("Found intent [{0}] and command [{1}]",
                        messageIntent, commandStr);
                sendMessageToUser(userEmail, session, channelId, ackMessage);
                cmdList = convertMsgToWordsList(commandStr);
            } else {
                String ackMessage = "No intent found for message. Attempting to execute the message as is on aws provider.";
                sendMessageToUser(userEmail, session, channelId, ackMessage);
                cmdList = convertMsgToWordsList(content);
            }

            // Execute Command
            Command cmd = new Command.Builder(dockerImage, cmdList)
                    .env(envList)
                    .entryPoint(providerName)
                    .build();

            CommandExecutor executor = CloudProviderFactory.getCloudProviderCmdExecutor(providerName);
            Response response = executor.executeCommand(cmd);
            sendMessageToUser(userEmail, session, channelId, response.getMsg());

            // Persist the response in DB
            Request request = requestWithResponse(requestId, cmd.toString(), response.getMsg(), Status.SUCCESS);
            Request finalRequest1 = request;
            requestDAO.update(new ArrayList<Request>() {{
                add(finalRequest1);
            }});

        } catch (IOException | CommandExecutionException | DBException e) {
            if (dbClient != null && message != null) {
                RequestDAO requestDAO = (RequestDAO) dbClient.getDAO(RequestDAO.class);
                JsonNode jsonNode = null;
                try {
                    jsonNode = JsonUtils.parseJson(message);
                } catch (IOException e1) {
                    log.log(Level.SEVERE, "Failed to parse JSON message", e1);
                }
                if (jsonNode != null) {
                    String requestId = jsonNode.path("requestId").asText();
                    String content = jsonNode.path("content").asText();
                    Request request = requestWithResponse(requestId, content, e.getMessage(), Status.FAILURE);
                    try {
                        requestDAO.update(new ArrayList<Request>() {{
                            add(request);
                        }});
                    } catch (DBException e1) {
                        log.log(Level.SEVERE, "Failed to update DB with failure status", e1);
                    }
                }
            }
            throw new JobExecutionException(e);
        }
    }

    private List<String> convertMsgToWordsList(String content) {
        String[] words = content.trim().split("\\s+");
        return Arrays.asList(words);
    }

    private static boolean checkMessage(List<String> intent, List<String> userMessage) {
        if (userMessage.containsAll(intent)) {
            return true;
        }
        return false;
    }

    private Request requestInProgress(String requestId) {
        Request request = new Request();
        request.setRequestId(requestId);
        request.setStartTime(new Date());
        request.setStatus(Status.IN_PROGRESS);
        return request;
    }

    private Request requestWithResponse(String requestId, String cmd, String responseMsg, Status status) {
        Request request = new Request();
        request.setCommandExecuted(cmd.toString());
        request.setRequestId(requestId);
        request.setResp(responseMsg);
        request.setRespRecvdTime(new Date());
        request.setStatus(status);
        return request;
    }

    private String[] getCmdArray(String content) {
        // \s = any whitespace character or a combination of them, will be treated as delimiter
        return content.trim().split("\\s+");
    }

    private List<String> getCloudProviderCredsByEmail(String userEmail) {
        try (HttpClient c = new HttpClient()) {
            edu.sjsu.amigo.http.client.Response<User> userResponse = c.get(BASE_URI + RESOURCE_URI + "/" + userEmail, User.class);
            User user = userResponse.getParsedObject();
            List<String> envList = new ArrayList<>();

            envList.add("AWS_DEFAULT_REGION=" + user.getAwsCredentials().getRegion());
            envList.add("AWS_ACCESS_KEY_ID=" + user.getAwsCredentials().getAwsAccessKeyId());
            envList.add("AWS_SECRET_ACCESS_KEY=" + user.getAwsCredentials().getAwsSecretAccessKey());
            return envList;
        } catch (Exception e) {

        }
        return null;
    }

    private List<String> getCloudProviderCredsByRiaId(String riaId) {
        try (HttpClient c = new HttpClient()) {
            edu.sjsu.amigo.http.client.Response<User> userResponse = c.get(BASE_URI + RESOURCE_URI + "/ria/" + riaId, User.class);
            User user = userResponse.getParsedObject();
            List<String> envList = new ArrayList<>();

            envList.add("AWS_DEFAULT_REGION=" + user.getAwsCredentials().getRegion());
            envList.add("AWS_ACCESS_KEY_ID=" + user.getAwsCredentials().getAwsAccessKeyId());
            envList.add("AWS_SECRET_ACCESS_KEY=" + user.getAwsCredentials().getAwsSecretAccessKey());
            return envList;
        } catch (Exception e) {

        }
        return null;
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

    /**
     * This is a method object of common code for finding intent of the message for either type of bots - slack or RIA.
     */
    private class IntentFinder {
        private ProviderDAO providerDAO;
        private String content;
        private String messageIntent;
        private String commandStr;
        private String dockerImage;
        private String providerName;

        public IntentFinder(ProviderDAO providerDAO, String content, String messageIntent, String commandStr, String dockerImage, String providerName) {
            this.providerDAO = providerDAO;
            this.content = content;
            this.messageIntent = messageIntent;
            this.commandStr = commandStr;
            this.dockerImage = dockerImage;
            this.providerName = providerName;
        }

        public String getMessageIntent() {
            return messageIntent;
        }

        public String getCommandStr() {
            return commandStr;
        }

        public String getDockerImage() {
            return dockerImage;
        }

        public String getProviderName() {
            return providerName;
        }

        public IntentFinder invoke() throws DBException {
            List<String> contentWordsList = convertMsgToWordsList(content);
            // Looking up by hardcoded provider aws.
            // TODO remove hardcoding and fetch all providers (not by ID).
            List<Provider> providers = providerDAO.fetchById(new ArrayList<String>() {{
                add("aws");
            }});

            if (providers != null && !providers.isEmpty()) {
                Provider provider = providers.get(0);
                List<edu.sjsu.amigo.command.db.model.Command> commands = provider.getCommands();
                for (edu.sjsu.amigo.command.db.model.Command command : commands) {
                    List<String> intents = command.getIntents();
                    for (String intent : intents) {
                        List<String> intentWordsList = convertMsgToWordsList(intent);
                        if (checkMessage(intentWordsList, contentWordsList)) {
                            messageIntent = intent;
                            commandStr = command.getCommand();
                            providerName = provider.getCloudProvider();
                            dockerImage = provider.getDockerImage();
                            break;
                        }
                    }
                }
            }
            return this;
        }
    }
}
