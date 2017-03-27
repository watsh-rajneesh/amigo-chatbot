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

package edu.sjsu.amigo.user.rest;

import edu.sjsu.amigo.db.common.DBClient;
import edu.sjsu.amigo.db.common.DBException;
import edu.sjsu.amigo.mp.util.JsonUtils;
import edu.sjsu.amigo.user.auth.PrincipalUser;
import edu.sjsu.amigo.user.db.model.User;
import io.dropwizard.auth.Auth;
import io.dropwizard.servlets.assets.ResourceNotFoundException;

import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.HEAD;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rwatsh on 3/27/17.
 */
public class UserResource extends BaseResource<User>  {
    private static final Logger log = Logger.getLogger(UserResource.class.getName());

    public UserResource(DBClient dbClient) {
        super(dbClient);
    }

    /**
     * Used to validate the user's creds. If the user is valid then framework will let this method
     * be invoked where we simply return 200.
     * <p>
     * Every subsequent user request will also need to have the same auth creds as server wont maintain any session.
     *
     * @param user
     * @param info
     * @return
     */
    @HEAD
    public Response login(@Auth User user, @Context UriInfo info) throws DBException {
        return Response.ok().build();
    }

    /**
     * Create the resource.
     *
     * @param principalUser
     * @param modelJson
     * @param info
     * @return
     */
    @Override
    public Response create(@Auth PrincipalUser principalUser, @Valid String modelJson, @Context UriInfo info) {
        try {
            User user = JsonUtils.convertJsonToObject(modelJson, User.class);
            user.isValid();
            // TO BE CONTINUED....
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in adding service", e);
            throw new BadRequestException(e);
        }
        return null;
    }

    @Override
    public List<User> list(@Auth PrincipalUser user, @QueryParam("filter") String filter) throws InternalErrorException {
        return null;
    }

    @Override
    public User retrieve(@Auth PrincipalUser user, @PathParam("id") String id) throws ResourceNotFoundException, InternalErrorException {
        return null;
    }

    @Override
    public User update(@Auth PrincipalUser user, @PathParam("id") String id, @Valid String entity) throws ResourceNotFoundException, InternalErrorException, IOException {
        return null;
    }

    @Override
    public Response delete(@Auth PrincipalUser user, @PathParam("id") String id) throws ResourceNotFoundException, InternalErrorException {
        return null;
    }
}
