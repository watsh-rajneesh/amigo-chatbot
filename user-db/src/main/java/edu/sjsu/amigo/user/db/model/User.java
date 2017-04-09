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

package edu.sjsu.amigo.user.db.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.sjsu.amigo.db.common.model.IModel;
import edu.sjsu.amigo.db.common.model.Validable;
import edu.sjsu.amigo.db.common.model.ValidationException;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * DB model for users.
 *
 * @author rwatsh on 3/26/17.
 */
@Entity(value = "users" , noClassnameStored = true, concern = "SAFE")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends Validable implements IModel {
    /*
     * User email ID
     */
    @Id
    private String email;
    /*
     * User name.
     */
    private String name;
    /*
     * User password.
     */
    private String password;
    /*
     * Slack ID for this user.
     */
    private String slackUser;
    /*
     * Raspberry Pi Intelligent Assistant UUID.
     */
    private String riaId;
    /*
     * AWS Credentials for the user.
     *
     * TODO make this externalized so other cloud provider specific
     * credentials can be added.
     */
    @Embedded
    private AWSCredentials awsCredentials;

    @JsonProperty
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty
    public String getSlackUser() {
        return slackUser;
    }

    public void setSlackUser(String slackUser) {
        this.slackUser = slackUser;
    }

    @JsonProperty
    public String getRiaId() {
        return riaId;
    }

    public void setRiaId(String riaId) {
        this.riaId = riaId;
    }

    @JsonProperty
    public AWSCredentials getAwsCredentials() {
        return awsCredentials;
    }

    public void setAwsCredentials(AWSCredentials awsCredentials) {
        this.awsCredentials = awsCredentials;
    }

    @Override
    @JsonIgnore
    public boolean isValid() throws ValidationException {
        return isReqd(email) &&
                isReqd(name) &&
                isReqd(password) &&
                isReqd(awsCredentials);
    }
}
