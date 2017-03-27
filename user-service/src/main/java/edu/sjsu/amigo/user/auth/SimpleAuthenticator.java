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

package edu.sjsu.amigo.user.auth;

import edu.sjsu.amigo.db.common.DBClient;
import edu.sjsu.amigo.db.common.DBException;
import edu.sjsu.amigo.db.common.Utilities;
import edu.sjsu.amigo.user.db.dao.UserDAO;
import edu.sjsu.amigo.user.db.model.User;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rwatsh on 3/27/17.
 */
public class SimpleAuthenticator implements Authenticator<BasicCredentials, PrincipalUser> {
    private DBClient dbClient;
    private static final Logger log = Logger.getLogger(SimpleAuthenticator.class.getName());


    public SimpleAuthenticator(DBClient dbClient) {
        this.dbClient = dbClient;
    }

    @Override
    public Optional<PrincipalUser> authenticate(BasicCredentials credentials) throws AuthenticationException {
        return getUserByCredentials(credentials.getUsername(), credentials.getPassword());

    }

    private Optional<PrincipalUser> getUserByCredentials(String username, String password) {
        UserDAO userDAO = (UserDAO) dbClient.getDAO(UserDAO.class);
        PrincipalUser principalUser = null;

        try {
            List<User> users = userDAO.fetchById(new ArrayList<String>() {{
                add(username);
            }});
            if (users != null && !users.isEmpty()) {
                User user = users.get(0);
                if (user.getPassword().equals(Utilities.generateMD5Hash(password))) {
                    principalUser = new PrincipalUser(username);
                    principalUser.setPassword(password);
                    return Optional.of(principalUser);
                }
            }
        } catch (DBException | NoSuchAlgorithmException e) {
            log.log(Level.SEVERE, MessageFormat.format("Authentication failed for user {0}", username), e);
        }
        return Optional.ofNullable(principalUser);
    }
}
