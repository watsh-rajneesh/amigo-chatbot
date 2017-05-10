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

package edu.sjsu.amigo.command.db.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.sjsu.amigo.db.common.model.IModel;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.Date;

/**
 * Audit log of all requests executed maintained here.
 *
 * @author rwatsh on 4/16/17.
 */
@Entity(value = "requests" , noClassnameStored = true, concern = "SAFE")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request implements IModel {
    @Id
    private String requestId;
    private Date startTime;
    private Date respRecvdTime;
    private String commandExecuted;
    private String resp;
    private Status status;

    @JsonProperty
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @JsonProperty
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonProperty
    public Date getRespRecvdTime() {
        return respRecvdTime;
    }

    public void setRespRecvdTime(Date respRecvdTime) {
        this.respRecvdTime = respRecvdTime;
    }

    @JsonProperty
    public String getCommandExecuted() {
        return commandExecuted;
    }

    public void setCommandExecuted(String commandExecuted) {
        this.commandExecuted = commandExecuted;
    }

    @JsonProperty
    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    @JsonProperty
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
