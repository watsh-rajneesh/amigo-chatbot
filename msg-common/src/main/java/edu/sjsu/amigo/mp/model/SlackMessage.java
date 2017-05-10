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

package edu.sjsu.amigo.mp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Message from slack messenger. It has properties specific to slack.
 *
 * @author rwatsh on 2/26/17.
 */
@Data
public class SlackMessage extends Message {
    /*
     * Channel ID of the channel where message was sent. This will be blank if the message was sent directly to bot.
     */
    @JsonProperty
    private String channelId;
    /*
     * Bot token to be used to create a session with the bot and send any message to it or receive any message from it.
     */
    @JsonProperty
    private String slackBotToken;

    public SlackMessage() {}

    public SlackMessage(String time, String userEmail, String userName, String content,
                         String channelName, String slackBotToken) {
        super(BotType.SLACK.name(), time, userEmail, userName, content);
        this.channelId = channelName;
        this.slackBotToken = slackBotToken;
    }
}
