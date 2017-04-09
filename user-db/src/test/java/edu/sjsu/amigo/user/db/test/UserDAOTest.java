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

package edu.sjsu.amigo.user.db.test;

import edu.sjsu.amigo.db.common.DBException;
import edu.sjsu.amigo.user.db.dao.UserDAO;
import edu.sjsu.amigo.user.db.model.AWSCredentials;
import edu.sjsu.amigo.user.db.model.User;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Test for user entity in user_db.
 *
 * @author rwatsh on 3/27/17.
 */
public class UserDAOTest extends DBTest<UserDAO, User> {
    private static final Logger log = Logger.getLogger(UserDAOTest.class.getName());

    @Override
    public void testAdd() throws Exception {
        testCreateUser();
    }

    private User createUser() {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setName("testUser");
        user.setPassword("password");
        user.setRiaId("101");
        user.setSlackUser("watsh.rajneesh@sjsu.edu");
        AWSCredentials awsCredentials = new AWSCredentials();
        awsCredentials.setAwsAccessKeyId("abc");
        awsCredentials.setAwsSecretAccessKey("def");
        awsCredentials.setRegion("us-west-2");
        user.setAwsCredentials(awsCredentials);
        return user;
    }

    private List<String> testCreateUser() throws DBException {
        User user = createUser();

        UserDAO userDAO = (UserDAO) client.getDAO(UserDAO.class);
        List<String> insertedIds = userDAO.add(new ArrayList<User>() {{
            add(user);
        }});
        List<User> users = userDAO.fetchById(insertedIds);
        Assert.assertNotNull(users);
        log.info("User created: " + users);
        return insertedIds;
    }

    @Override
    public void testRemove() throws Exception {
        List<String> insertedIds = testCreateUser();
        Assert.assertNotNull(insertedIds);
        long countRemovedEntries = dao.remove(insertedIds);
        Assert.assertTrue(countRemovedEntries > 0, "Failed to delete any service");

    }

    @Override
    public void testUpdate() throws Exception {

    }

    @Override
    public void testFetch() throws Exception {
        List<String> insertedIds = testCreateUser();
        Assert.assertNotNull(insertedIds);
        List<User> users = dao.fetchById(insertedIds);
        Assert.assertNotNull(users);
    }
}
