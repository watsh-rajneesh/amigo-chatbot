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

package edu.sjsu.amigo.chatbot.msg;

import edu.sjsu.amigo.chatbot.api.AIClient;
import edu.sjsu.amigo.chatbot.nlp.WitDotAIClient;
import edu.sjsu.amigo.json.util.JsonUtils;
import edu.sjsu.amigo.mp.kafka.MessageProducer;
import edu.sjsu.amigo.mp.model.Message;
import lombok.extern.java.Log;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author rwatsh on 3/26/17.
 */
@Log
public class MessageProcessor {

    public static void processMessage(Message msg) throws Exception {
        // Get the intent from wit.ai and attach it to the message.
        /*
            Intent will be a sorted list of tokens.
            intent: [aws, ec2, list]
         */
        log.info("Processing message: " + msg);
        setIntent(msg);
        setRequestId(msg);


        if (msg != null) {
            String msgJson = null;

            try {
                msgJson = JsonUtils.convertObjectToJson(msg);
                log.info("Converted msg to json: " + msgJson);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error in converting message to JSON", e);
                throw e;
            }

            if (msgJson != null) {
                // Send message to topic on kafka MQ
                log.info("Sending message as JSON: " + msgJson);
                String kafkaHostName = System.getenv("KAFKA_HOST_NAME");
                log.info("Using kafka host: " + kafkaHostName);
                try (MessageProducer producer = new MessageProducer(kafkaHostName)) {
                    log.info("Publishing message json to kafka MQ: " + msgJson);
                    producer.sendUserMessage(msgJson);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Error in publishing message to queue", e);
                    throw e;
                }
            }
        }
    }

    private static void setIntent(Message msg) {
        List<String> intent = null;
        //intent = HttpClient.getIntentFromWitAI(parsedMessage);
        AIClient aiClient = new WitDotAIClient();
        intent = aiClient.getIntent(msg.getContent());
        log.info(MessageFormat.format("Got intent [{0}] for message [{1}]", intent, msg));
        msg.setIntent(intent);
    }

    private static void setRequestId(Message msg) {
        // Generate a request ID and attach it to message
        String requestId = UUID.randomUUID().toString();
        log.info(MessageFormat.format("Generated request ID is [{0}]", requestId));
        msg.setRequestId(requestId);
    }
}
