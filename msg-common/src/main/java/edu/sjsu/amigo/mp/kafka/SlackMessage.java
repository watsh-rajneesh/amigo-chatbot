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

package edu.sjsu.amigo.mp.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Message from slack messenger. It has properties specific to slack.
 *
 * @author rwatsh on 2/26/17.
 */
public class SlackMessage extends Message {
    @JsonProperty
    private String channelId;
    // TODO make it encrypted - should not be visible in clear text.
    @JsonProperty
    private String slackBotToken;


    public SlackMessage(String time, String userEmail, String userName, String content, List<String> intent,
                         String channelName, String slackBotToken) {
        super(time, userEmail, userName, content, intent);
        this.channelId = channelName;
        this.slackBotToken = slackBotToken;
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getSlackBotToken() {
        return slackBotToken;
    }

    public void setSlackBotToken(String slackBotToken) {
        this.slackBotToken = slackBotToken;
    }

    @Override
    public String toString() {
        return "SlackMessage{" +
                ", channelId='" + channelId + '\'' +
                ", slackBotToken='" + slackBotToken + '\'' +
                '}';
    }
}
