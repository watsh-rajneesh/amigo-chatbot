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

package edu.sjsu.amigo.user.rest.test;

import edu.sjsu.amigo.json.util.EndpointUtils;
import edu.sjsu.amigo.json.util.JsonUtils;
import edu.sjsu.amigo.user.db.model.AWSCredentials;
import edu.sjsu.amigo.user.db.model.User;
import lombok.extern.java.Log;
import org.testng.Assert;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author rwatsh on 4/8/17.
 */
@Log
public class UserResourceTest extends BaseResourceTest {
    public static final String RESOURCE_URI = EndpointUtils.ENDPOINT_ROOT + "/users";

    private User addUser(User user) throws Exception {
        // Convert to string
        String jsonStr = JsonUtils.convertObjectToJson(user);
        log.info(jsonStr);
        Response response = webTarget.path(RESOURCE_URI)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonStr));
        String us = response.readEntity(String.class);
        User u = JsonUtils.convertJsonToObject(us, User.class);
        log.info(u.toString());
        Assert.assertTrue(response.getStatus() == Response.Status.CREATED.getStatusCode());
        return user;
    }

    private User createTestUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("testUser");
        user.setPassword("pass");
        user.setRiaId("101");
        user.setSlackUser("watsh.rajneesh@sjsu.edu");
        AWSCredentials awsCredentials = new AWSCredentials();
        awsCredentials.setAwsAccessKeyId("abc");
        awsCredentials.setAwsSecretAccessKey("def");
        awsCredentials.setRegion("us-west-2");
        user.setAwsCredentials(awsCredentials);
        return user;
    }

    @Override
    public void testAdd() throws Exception {


        User user = addUser(createTestUser("watsh.rajneesh@sjsu.edu"));
        Assert.assertNotNull(user);
    }

    @Override
    public void testRemove() throws Exception {
        User user = addUser(createTestUser("test_remove@tests.com"));
        log.info("Created user: " + user);
        Response response = webTarget.path(RESOURCE_URI)
                .path("/" + user.getEmail())
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .delete();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        log.info("User deleted: " + user);
    }

    @Override
    public void testUpdate() throws Exception {

        User user = addUser(createTestUser("test_update@tests.com"));
        log.info("Created user: " + user);
        user.setName("test_modified_name");
        String jsonStr = JsonUtils.convertObjectToJson(user);
        log.info(jsonStr);
        Response response = webTarget.path(RESOURCE_URI)
                .path("/" + user.getEmail())
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .put(Entity.json(jsonStr));
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        log.info("User updated: " + user);
        log.info(response.toString());
        String respStr = response.readEntity(String.class);
        User updatedUser = JsonUtils.convertJsonToObject(respStr, User.class);
        log.info("Updated user: " + updatedUser);
    }

    @Override
    public void testFetch() throws Exception {
        List<User> userList = getUsers();
        if (userList != null && !userList.isEmpty()) {
            String id = userList.get(0).getEmail();

            Response response = webTarget.path(RESOURCE_URI)
                    .queryParam("id", id)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            log.info(response.toString());
            String respStr = response.readEntity(String.class);
            userList = JsonUtils.convertJsonArrayToList(respStr, User.class);
            log.info(userList.toString());
            Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        }
    }

    private List<User> getUsers() throws Exception {
        Response response = webTarget.path(RESOURCE_URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        log.info(response.toString());
        String respStr = response.readEntity(String.class);
        List<User> userList = JsonUtils.convertJsonArrayToList(respStr, User.class);
        log.info(userList.toString());
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        return userList;
    }

    @Override
    public void testFetchAll() throws Exception {
        List<User> userList  = getUsers();
        Assert.assertNotNull(userList);
    }
}
