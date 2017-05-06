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

package edu.sjsu.amigo.chatbot.rest.test;

import edu.sjsu.amigo.json.util.EndpointUtils;
import edu.sjsu.amigo.json.util.JsonUtils;
import edu.sjsu.amigo.mp.model.Message;
import edu.sjsu.amigo.mp.model.SlackMessage;
import lombok.extern.java.Log;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;

/**
 * @author rwatsh on 4/22/17.
 */
@Log
public class ChatResourceTest {
    protected static final String BASE_URI = "http://localhost:8080";
    public static final String RESOURCE_URI = EndpointUtils.ENDPOINT_ROOT + "/chat";
    protected Client client;
    protected WebTarget webTarget;

    @BeforeClass
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
        webTarget = client.target(BASE_URI);
    }

    @AfterClass
    public void tearDown() throws Exception {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void sendMessageTest() throws IOException {
        Message msg = new SlackMessage(new Date().toString(),
                "watsh.rajneesh@sjsu.edu",
                "rwatsh",
                "aws help",
                "UA574",
                "x703j");

        String jsonStr = JsonUtils.convertObjectToJson(msg);
        log.info(jsonStr);
        Response response = webTarget.path(RESOURCE_URI)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonStr));
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        log.info(response.getEntity().toString());
    }
}
