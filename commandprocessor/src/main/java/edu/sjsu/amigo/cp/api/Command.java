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
package edu.sjsu.amigo.cp.api;

import java.util.List;

/**
 * This class represents a command. It is generic and can be specialized for each cloud provider specific command as needed.
 *
 * @author rwatsh on 2/14/17.
 */
public class Command {

    private String dockerImage; // mandatory property
    private List<String> envList; // optional
    private List<String> commandList; // mandatory property
    private String entryPoint; // optional

    public static class Builder {
        private String dockerImage;
        private List<String> envList;
        private List<String> cmdList;
        private String entryPoint;

        public Builder(String dockerImage, List<String> cmdList) {
            this.dockerImage = dockerImage;
            this.cmdList = cmdList;
        }

        public Builder env(List<String> envList) {
            this.envList = envList;
            return this;
        }

        public Builder entryPoint(String entryPoint) {
            this.entryPoint = entryPoint;
            return this;
        }

        public Command build() {
            return new Command(this);
        }
    }

    private Command(Builder builder) {
        this.dockerImage = builder.dockerImage;
        this.commandList = builder.cmdList;
        this.entryPoint = builder.entryPoint;
        this.envList = builder.envList;
    }

    public String getDockerImage() {
        return dockerImage;
    }

    public void setDockerImage(String dockerImage) {
        this.dockerImage = dockerImage;
    }

    public List<String> getEnvList() {
        return envList;
    }

    public void setEnvList(List<String> envList) {
        this.envList = envList;
    }

    public List<String> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<String> commandList) {
        this.commandList = commandList;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Override
    public String toString() {
        return "Command{" +
                "dockerImage='" + dockerImage + '\'' +
                ", envList=" + envList +
                ", commandList=" + commandList +
                ", entryPoint='" + entryPoint + '\'' +
                '}';
    }
}
