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
import edu.sjsu.amigo.db.common.model.Validable;
import edu.sjsu.amigo.db.common.model.ValidationException;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cloud provider and the commands that can be executed on the provider for each inferred intent.
 *
 * @author rwatsh on 4/16/17.
 */
@Entity(value = "providers" , noClassnameStored = true, concern = "SAFE")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Provider extends Validable implements IModel {
    @Id
    private String cloudProvider;
    private String dockerImage;

    @Embedded
    private List<Command> commands = new ArrayList<>();

    @JsonProperty
    public String getCloudProvider() {
        return cloudProvider;
    }

    public void setCloudProvider(String cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    @JsonProperty
    public String getDockerImage() {
        return dockerImage;
    }

    public void setDockerImage(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    @JsonProperty
    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return "Provider{" +
                "cloudProvider='" + cloudProvider + '\'' +
                ", dockerImage='" + dockerImage + '\'' +
                ", commands=" + commands +
                '}';
    }

    @Override
    public boolean isValid() throws ValidationException {
        boolean isValid = isReqd(cloudProvider);
        if (isValid) {
            for (Command command: commands) {
                if (!command.isValid()) {
                    return false;
                }
            }
        }
        return isValid;
    }
}
