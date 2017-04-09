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

package edu.sjsu.amigo.user.db.dao;

import com.mongodb.MongoClient;
import edu.sjsu.amigo.db.common.DBException;
import edu.sjsu.amigo.db.common.mongodb.BaseDAOImpl;
import edu.sjsu.amigo.user.db.model.User;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for User.
 *
 * @author rwatsh on 3/26/17.
 */
public class UserDAO extends BaseDAOImpl<User> {

    public UserDAO(MongoClient mongoClient, Morphia morphia, String dbName) {
        super(mongoClient, morphia, dbName);
    }

    @Override
    public synchronized void update(List<User> entityList) throws DBException {
        for (User user: entityList) {
            List<User> users = fetchById(new ArrayList<String>() {{add(user.getEmail());}});
            User existingUser = null;
            if (users != null && !users.isEmpty()) {
                existingUser = users.get(0);
            }
            if (existingUser != null) {
                UpdateOperations<User> ops = this.createUpdateOperations();
                if (user.getAwsCredentials() != null) {
                    ops.set("awsCredentials", user.getAwsCredentials());
                }
                if (user.getName() != null) {
                    ops.set("name", user.getName());
                }
                if (user.getPassword() != null) {
                    ops.set("password", user.getPassword());
                }
                if (user.getRiaId() != null) {
                    ops.set("riaId", user.getRiaId());
                }
                if (user.getSlackUser() != null) {
                    ops.set("slackUser", user.getSlackUser());
                }

                Query<User> updateQuery = this.createQuery().field(Mapper.ID_KEY).equal(user.getEmail());
                UpdateResults results = this.update(updateQuery, ops);
            }
        }
    }


}
