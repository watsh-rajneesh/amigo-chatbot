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

package edu.sjsu.amigo.chatbot.nlp;

import com.fasterxml.jackson.databind.JsonNode;
import edu.sjsu.amigo.chatbot.api.AIClient;
import edu.sjsu.amigo.http.client.HttpClient;
import edu.sjsu.amigo.http.client.Response;
import edu.sjsu.amigo.mp.kafka.IntentElem;
import edu.sjsu.amigo.mp.kafka.InvalidMessageException;
import edu.sjsu.amigo.json.util.JsonUtils;

import java.io.IOException;
import java.util.*;

/**
 * This class provides methods to access wit.api REST endpoints.
 *
 * @author rwatsh on 3/7/17.
 */
public class WitDotAIClient implements AIClient {

    public static final String WIT_AI_SERVER_ACCESS_TOKEN = System.getenv("WIT_AI_SERVER_ACCESS_TOKEN");
    public static final String WIT_AI_URL_PREFIX = "https://api.wit.ai";
    public static final String WIT_AI_MESSAGE_URI = "/message";
    public static final String WIT_AI_INTENTS_URI = "/intents";
    public static final Map<String, String> queryStringMap = new HashMap<String, String>() {
        {
            put("v", "20160526");
        }
    };
    public static final Map<String, String> headersMap = new HashMap<String, String>() {
        {
            put("authorization", "Bearer " + WIT_AI_SERVER_ACCESS_TOKEN); //OAuth 2.0 Bearer Token
        }
    };

    public WitDotAIClient() {}

    /**
     * Given a message find the intents.
     *
     * A sample message from wit.ai:
     * {
         "msg_id" : "5e89f3d9-93d1-4dfa-b128-73fda21974b3",
         "_text" : "list my ec2 instances on aws and azure",
         "entities" : {
             "intent" : [ {
                     "confidence" : 0.9771069792658154,
                     "type" : "value",
                     "value" : "list"
                 }, {
                     "confidence" : 0.9721842327959846,
                     "type" : "value",
                     "value" : "ec2"
                 }, {
                     "confidence" : 0.873736763412216,
                     "type" : "value",
                     "value" : "aws"
                 }, {
                     "confidence" : 0.7501236802989762,
                     "type" : "value",
                     "value" : "azure",
                     "suggested" : true
            } ]
         }
     }
     *
     * @param message   the message to parse and to find intents from.
     * @return  an array of sorted intent tokens.
     */
    @Override
    public List<String> getIntent(String message) {
        try(HttpClient httpClient = new HttpClient()) {
            String url = WIT_AI_URL_PREFIX + WIT_AI_MESSAGE_URI;
            queryStringMap.put("q", message);
            Response<String> resp = httpClient.get(url, null, queryStringMap, headersMap, String.class);
            JsonNode jsonNode = JsonUtils.parseJson(resp.getRawBody());
            JsonNode intentNode = jsonNode.path("entities").path("intent");
            List<IntentElem> intents = new ArrayList<>();
            if(intentNode.isArray()) {
                for (JsonNode node: intentNode) {
                    IntentElem intentElem = new IntentElem();
                    intentElem.setConfidence(node.path("confidence").asText());
                    intentElem.setType(node.path("type").asText());
                    intentElem.setValue(node.path("value").asText());
                    intentElem.setSuggested(node.path("suggested").asBoolean());
                    intents.add(intentElem);
                }
            }
            //return JsonUtils.prettyPrint(resp.getRawBody());
            //System.out.println(JsonUtils.convertObjectToJson(intents));
            //return intents;
            List<String> toks = new ArrayList<>();
            for (IntentElem tok: intents) {
                //System.out.println(tok.getValue());
                toks.add(tok.getValue());
            }
            Collections.sort(toks);
            return toks;
        } catch (Exception e) {
            throw new InvalidMessageException(e);
        }
    }

    /**
     * Get all intents.
     *
     * @return
     */
    public static String getAllIntents() {
        try(HttpClient httpClient = new HttpClient()) {
            String url = WIT_AI_URL_PREFIX + WIT_AI_INTENTS_URI;
            Response<String> resp = httpClient.get(url, null, null, headersMap, String.class);
            return JsonUtils.prettyPrint(resp.getRawBody());
        } catch (Exception e) {
            throw new InvalidMessageException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        String message = "list ec2 instances of aws please";
        AIClient aiClient = new WitDotAIClient();
        List<String> resp = aiClient.getIntent(message);
        System.out.println("intent: " + resp);

    }
}
