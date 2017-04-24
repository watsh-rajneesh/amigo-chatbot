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

package edu.sjsu.amigo.slackbot;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import edu.sjsu.amigo.scheduler.jobs.JobManager;
import edu.sjsu.amigo.slackbot.jobs.SlackMessageProcessorJob;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.sjsu.amigo.scheduler.jobs.JobConstants.*;


/**
 * Listens to messages sent on channels amigo chatbot joined on slack or messges sent directly to it.
 *
 * @author rwatsh on 2/26/17.
 */
public class MessageListener {
    private final static Logger logger = Logger.getLogger(MessageListener.class.getName());

    /**
     * This method shows how to register a listener on a SlackSession
     */
    public void registerListener(SlackSession session)
    {
        // first define the listener
        SlackMessagePostedListener messagePostedListener = new SlackMessagePostedListener()
        {
            public void onEvent(SlackMessagePosted event, SlackSession session)
            {
                SlackChannel channelOnWhichMessageWasPosted = event.getChannel();
                String messageContent = event.getMessageContent();
                SlackUser messageSender = event.getSender();
                SlackChannel channel = event.getChannel();
                logger.info("Received message from " + messageSender + " content [" + messageContent + "]");

                if (isNotNullOrEmpty(messageContent)) {

                    processUserMessage(messageContent, messageSender, session, channel);

                }
            }
        };
        //add it to the session
        session.addMessagePostedListener(messagePostedListener);

        //that's it, the listener will get every message post events the bot can get notified on
        //(IE: the messages sent on channels it joined or sent directly to it)
    }

    private void processUserMessage(String messageContent, SlackUser messageSender, SlackSession session, SlackChannel channel) {
        // TODO change it to be processed asynchronously
        // Parse the message content
        String parsedMessage = parseMessage(messageContent);

        if (parsedMessage != null) {
            try {
                // Some unique job name
                String jobName = "SLACK-MESG-JOB-" + UUID.randomUUID().toString();
                String groupName = JOB_GRP_SLACKBOT;
                JobDataMap params = new JobDataMap();
                params.put(JOB_PARAM_MESSAGE, parsedMessage);
                params.put(JOB_PARAM_MSG_SENDER, messageSender);
                params.put(JOB_PARAM_SLACK_SESSION, session);
                params.put(JOB_PARAM_SLACK_CHANNEL, channel);
                params.put(JOB_PARAM_BOT_TOK, System.getenv("SLACK_BOT_TOKEN"));
                JobManager.getInstance().scheduleJob(SlackMessageProcessorJob.class, jobName, groupName, params);

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in processing message", e);
            }
        }
    }

    private boolean isNotNullOrEmpty(String messageContent) {
        return messageContent != null && !messageContent.trim().isEmpty();
    }

    /**
     * Parse the message to see if content includes <@BOT_ID> in the message.
     * If it does, the message was meant for our chatbot hence get the message after the @BOT_ID.
     *
     * @param messageContent
     * @return
     */
    private String parseMessage(String messageContent) {
        String botId = "<@" + System.getenv("BOT_ID") + ">";
        String parsedMessage = null;
        if (messageContent.contains(botId)) {
            // Get the text after <@BOT_ID> for example, from "@amigo aws iam list-users" get "aws iam list-users".
            parsedMessage = messageContent.split(botId)[1];
        }
        return parsedMessage;
    }

    public static void main(String[] args) throws IOException, SchedulerException {
        //Start the job scheduler
        JobManager.getInstance().startScheduler();

        SlackSession session = SlackSessionFactory.createWebSocketSlackSession(System.getenv("SLACK_BOT_TOKEN"));
        session.connect();
        MessageListener listener = new MessageListener();
        listener.registerListener(session);
    }

}
