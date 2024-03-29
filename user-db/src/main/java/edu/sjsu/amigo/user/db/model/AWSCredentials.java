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

/**
 * Amazon web services credentials for the user.
 *
 * @author rwatsh on 3/26/17.
 */
@Embedded
@JsonIgnoreProperties(ignoreUnknown = true)
public class AWSCredentials extends Validable implements IModel {
    private String region;
    private String awsAccessKeyId;
    private String awsSecretAccessKey;

    @JsonProperty
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @JsonProperty
    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    @JsonProperty
    public String getAwsSecretAccessKey() {
        return awsSecretAccessKey;
    }

    public void setAwsSecretAccessKey(String awsSecretAccessKey) {
        this.awsSecretAccessKey = awsSecretAccessKey;
    }

    @Override
    @JsonIgnore
    public boolean isValid() throws ValidationException {
        return true;
    }

    @Override
    public String toString() {
        return "AWSCredentials{" +
                "region='" + region + '\'' +
                ", awsAccessKeyId='" + awsAccessKeyId + '\'' +
                ", awsSecretAccessKey='" + awsSecretAccessKey + '\'' +
                '}';
    }
}
