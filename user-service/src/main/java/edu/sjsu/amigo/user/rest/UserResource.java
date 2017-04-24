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
import edu.sjsu.amigo.db.common.Utilities;
import edu.sjsu.amigo.json.util.InternalErrorException;
import edu.sjsu.amigo.json.util.JsonUtils;
import edu.sjsu.amigo.user.auth.PrincipalUser;
import edu.sjsu.amigo.user.db.model.User;
import io.dropwizard.auth.Auth;
import io.dropwizard.servlets.assets.ResourceNotFoundException;
import lombok.extern.java.Log;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author rwatsh on 3/27/17.
 */
@Log
@Path("/users")
/*
@Api(value = "/users", description = "Operations about users")
*/
public class UserResource extends BaseResource<User> {
    public UserResource(DBClient dbClient) {
        super(dbClient);
    }

    /**
     * Used to validate the user's creds. If the user is valid then framework will let this method
     * be invoked where we simply return 200.
     * <p>
     * Every subsequent user request will also need to have the same auth creds as server wont maintain any session.
     *
     * REST is stateless so we don't do any session tracking for user and this method lets the web UI perform a login
     * operation. Alternatively web UI can also do a get on the user ID specified during login.
     *
     * @param info
     * @return
     */
    @HEAD
    @Produces(MediaType.APPLICATION_JSON)
    /*@ApiOperation(httpMethod = "HEAD",
            value = "Authenticates the user",
            response = Response.class,
            nickname="login")*/
    public User validateApiKey(@Context UriInfo info, @Context HttpHeaders headers) throws DBException {
        //return Response.ok().build();
        try {
            String apiKey = null;
            List<String> vals = headers.getRequestHeader("AMIGO-API-KEY");
            if (vals != null && !vals.isEmpty()) {
                apiKey = vals.get(0);
            }
            if (apiKey != null) {
                List<User> users = userDAO.fetch("{apiKey: \"" + apiKey + "\"}", User.class);
                if (users != null && !users.isEmpty()) {
                    return users.get(0);
                }
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in validating API Key", e);
        }
        return null;
    }

    /**
     * Create the resource.
     *
     * @param modelJson
     * @return
     */
    @Override
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    /*@ApiOperation(httpMethod = "POST",
            value = "Resource to create a user",
            response = Response.class,
            nickname="create")*/
    public Response create(@Valid String modelJson) {
        try {
            User user = JsonUtils.convertJsonToObject(modelJson, User.class);
            user.isValid();
            String encryptedPasswd = Utilities.generateMD5Hash(user.getPassword());
            user.setPassword(encryptedPasswd);
            // generate API Key
            user.setApiKey(UUID.randomUUID().toString());
            List<String> insertedIds = userDAO.add(new ArrayList<User>() {{
                add(user);
            }});
            if(insertedIds != null && !insertedIds.isEmpty()) {
                URI uri = UriBuilder.fromResource(UserResource.class).build(insertedIds.get(0));
                return Response.created(uri)
                        .entity(Entity.json(insertedIds.get(0)))
                        .build();
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in adding service", e);
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(Entity.json(modelJson)).build();
    }

    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /*@ApiOperation(httpMethod = "GET",
            value = "list all users",
            response = User.class,
            responseContainer = "List",
            nickname="list")*/
    public List<User> list(@Auth PrincipalUser user, @QueryParam("filter") String filter) throws InternalErrorException {
        try {
            if(filter == null) {
                return userDAO.fetchById(null);
            } else {
                return userDAO.fetch(filter, User.class);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in getting users", e);
            throw new BadRequestException(e);
        }
    }

    @Override
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    /*@ApiOperation(httpMethod = "GET",
            value = "get a user by id",
            response = User.class,
            nickname="retrieve")*/
    public User retrieve(@Auth PrincipalUser user, @PathParam("id") String id) throws ResourceNotFoundException, InternalErrorException {
        try {
            List<User> users = userDAO.fetchById(new ArrayList<String>() {{
                add(id);
            }});
            return users != null && !users.isEmpty() ? users.get(0) : null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in getting user ID [" + id +  "]", e);
            throw new BadRequestException(e);
        }
    }

    @Override
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    /*@ApiOperation(httpMethod = "PUT",
            value = "update a user",
            response = User.class,
            nickname="update")*/
    public User update(@Auth PrincipalUser user, @PathParam("id") String id, @Valid String entity) throws ResourceNotFoundException, InternalErrorException, IOException {
        try {
            User u = JsonUtils.convertJsonToObject(entity, User.class);
            u.isValid();
            userDAO.update(new ArrayList<User>() {{
                add(u);
            }});
            List<User> users = userDAO.fetchById(new ArrayList<String>() {{
                add(id);
            }});
            if (users != null && !users.isEmpty()) {
                return users.get(0);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in getting user ID [" + id +  "]", e);
            throw new BadRequestException(e);
        }
        return null;
    }

    @Override
    @DELETE
    @Path("/{id}")
    /*@ApiOperation(httpMethod = "DELETE",
            value = "delete a user",
            response = Response.class,
            nickname="delete")*/
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@Auth PrincipalUser user, @PathParam("id") String id) throws ResourceNotFoundException, InternalErrorException {
        try {
            userDAO.deleteById(id);
            return Response.ok()
                    .entity(Entity.json(id))
                    .build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in getting users", e);
            throw new BadRequestException(e);
        }
    }
}
