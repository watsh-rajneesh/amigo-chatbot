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

package edu.sjsu.amigo.cp.rest;

import edu.sjsu.amigo.command.db.model.Request;
import edu.sjsu.amigo.db.common.DBClient;
import edu.sjsu.amigo.json.util.InternalErrorException;
import io.dropwizard.servlets.assets.ResourceNotFoundException;
import lombok.extern.java.Log;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author rwatsh on 4/23/17.
 */
@Path("/requests")
@Log
public class RequestResource extends BaseResource<Request>{
    public RequestResource(DBClient dbClient) {
        super(dbClient);
    }

    /**
     * Create the resource.
     *
     * @param modelJson
     * @return
     */
    @Override
    public Response create(@Valid String modelJson) {
        return null;
    }

    @Override
    public List<Request> list(@QueryParam("filter") String filter) throws InternalErrorException {
        return null;
    }

    @Override
    @GET
    @Path("/{requestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Request retrieve(@PathParam("requestId") String requestId) throws ResourceNotFoundException, InternalErrorException {
        try {
            List<Request> requests = requestDAO.fetchById(new ArrayList<String>() {{
                add(requestId);
            }});
            if (requests != null && !requests.isEmpty()) {
                return requests.get(0);
            } else {
                return null;
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in getting user ID [" + requestId +  "]", e);
            throw new BadRequestException(e);
        }
    }

    @Override
    public Request update(@PathParam("id") String id, @Valid String entity) throws ResourceNotFoundException, InternalErrorException, IOException {
        return null;
    }

    @Override
    public Response delete(@PathParam("id") String id) throws ResourceNotFoundException, InternalErrorException {
        return null;
    }
}
