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

import java.util.List;

/**
 * Message payload that will be sent to the backend to process and execute command.
 * This is client agnostic message so all clients - slack messenger or web UI will send the same message to the
 * backend.
 *
 * An example message received from slack messenger.
 *
 * Feb 26, 2017 4:17:02 AM edu.sjsu.amigo.slackbot.MessageListener$1 onEvent
 INFO: Received message from SlackUserImpl{
     id='USER_ID',
     userName='watsh',
     realName='Watsh Rajneesh',
     userMail='watsh.rajneesh@sjsu.edu',
     userSkype='rwatsh',
     userPhone='',
     userTitle='',
     isDeleted=false',
     isAdmin=true',
     isOwner=true',
     isPrimaryOwner=true',
     isRestricted=false',
     isUltraRestricted=false,
     timeZone=America/Los_Angeles,
     timeZoneLabel=Pacific Standard Time, timeZoneOffset=-28800}
     content [<@BOT_ID> aws iam list-users]
 }
 *
 * @author rwatsh on 2/26/17.
 */
@Data
public class Message {
    @JsonProperty
    private String botType;
    // Message creation time
    @JsonProperty
    private String msgReceivedTime;
    // Email id of user who sent this message
    @JsonProperty
    private String userEmail;
    // User name of user who sent this message
    @JsonProperty
    private String userName;
    // Content of this message
    @JsonProperty
    private String content;
    /*
     * Intent is fetched from wit.ai service at chatbot service.
     */
    @JsonProperty
    private List<String> intent;
    /*
     * Request ID is generated at command processor service.
     */
    @JsonProperty
    private String requestId;


    public Message() {}

    public Message(String botType, String time, String userEmail, String userName, String content) {
        this.botType = botType;
        this.msgReceivedTime = time;
        this.userEmail = userEmail;
        this.userName = userName;
        this.content = content;
        this.intent = intent;
    }

}
