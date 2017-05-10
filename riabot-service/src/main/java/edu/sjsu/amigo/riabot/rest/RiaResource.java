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

package edu.sjsu.amigo.riabot.rest;

import edu.sjsu.amigo.json.util.JsonUtils;
import edu.sjsu.amigo.mp.model.RiaMessage;
import edu.sjsu.amigo.riabot.jobs.RiabotMessageProcessorJob;
import edu.sjsu.amigo.scheduler.jobs.JobConstants;
import edu.sjsu.amigo.scheduler.jobs.JobManager;
import lombok.extern.java.Log;
import org.quartz.JobDataMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import static edu.sjsu.amigo.scheduler.jobs.JobConstants.JOB_PARAM_MESSAGE;

/**
 * @author rwatsh on 4/23/17.
 */
@Log
@Path("/ria")
public class RiaResource {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(String messageJson) {
        try {
            RiaMessage chatMessage = JsonUtils.convertJsonToObject(messageJson, RiaMessage.class);
            try {
                // Some unique job name
                String jobName = "RIABOT-MESG-JOB-" + UUID.randomUUID().toString();
                String groupName = JobConstants.JOB_GRP_RIABOT;
                JobDataMap params = new JobDataMap();
                params.put(JOB_PARAM_MESSAGE, chatMessage);

                JobManager.getInstance().scheduleJob(RiabotMessageProcessorJob.class, jobName, groupName, params);

            } catch (Exception e) {
                log.log(Level.SEVERE, "Error in processing message", e);
                return Response.status(Response.Status.BAD_REQUEST).entity(Entity.text("Error in processing message: " + e.getMessage())).build();
            }
            return Response.accepted()
                    .entity(Entity.text("Received: " + chatMessage.getContent()))
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
