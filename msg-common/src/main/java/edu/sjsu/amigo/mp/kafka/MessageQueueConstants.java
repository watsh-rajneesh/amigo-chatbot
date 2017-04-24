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

/**
 * Constants to be used by both message producers and consumers.
 *
 * @author rwatsh on 2/26/17.
 */
public interface MessageQueueConstants {
    // Topics
    /**
     * user message topic on which incoming client message is received.
     */
    String USER_MSG_TOPIC = "user.msg";

    // Client groups
    /**
     * chatbot group is the group of client consumers receiving message from user message topic.
     * Only one consumer from the group of consumers will receive a message from the topic.
     * 
     */
    String AMIGO_CHATBOT_GROUP = "amigo-chatbot-group";
}
