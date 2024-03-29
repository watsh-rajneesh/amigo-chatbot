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

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.sjsu.amigo.db.common.model.Validable;
import edu.sjsu.amigo.db.common.model.ValidationException;
import org.mongodb.morphia.annotations.Embedded;

import java.util.List;

/**
 * Represents a mapping between intent and the command to execute for the provider.
 *
 * @author rwatsh on 4/16/17.
 */
@Embedded
public class Command extends Validable {
    private List<String> intents;
    private String command;

    @JsonProperty
    public List<String> getIntents() {
        return intents;
    }

    public void setIntents(List<String> intents) {
        this.intents = intents;
    }

    @JsonProperty
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public boolean isValid() throws ValidationException {
        return isReqd(intents) && isReqd(command);
    }

    @Override
    public String toString() {
        return "Command{" +
                "intents='" + intents + '\'' +
                ", command=" + command +
                '}';
    }
}
