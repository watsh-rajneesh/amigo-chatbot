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
package edu.sjsu.amigo.cp.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.RegistryAuth;
import edu.sjsu.amigo.cp.api.CommandExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.spotify.docker.client.DockerClient.LogsParam.stderr;
import static com.spotify.docker.client.DockerClient.LogsParam.stdout;

/**
 * Docker command executor task.
 *
 * @author rwatsh on 2/14/17.
 */
public class DockerTask {
    private static final Logger logger = Logger.getLogger(DockerTask.class.getName());

    /**
     * Executes a containerized command on AWS.
     */
    public String execute(String dockerImage, List<String> envList, List<String> commandList, String entryPoint) throws CommandExecutionException {
        try( DockerClient dockerClient = new DefaultDockerClient("unix:///var/run/docker.sock")) {
            String response = null;
            pullImage(dockerClient, dockerImage);
            //dockerClient.pull(dockerImage);

            if (commandList != null && !commandList.isEmpty()) {
                if (commandList.get(0).equalsIgnoreCase(entryPoint)) {
                    commandList = commandList.subList(1, commandList.size());
                }
            }

            final ContainerConfig containerConfig = ContainerConfig.builder()
                    .image(dockerImage)
                    .env(envList)
                    .entrypoint(entryPoint)
                    .cmd(commandList)
                    .build();

            final ContainerCreation container = dockerClient.createContainer(containerConfig);
            final String containerId = container.id();
            dockerClient.startContainer(containerId);

            // Wait for the container to exit.
            // If we don't wait, docker.logs() might return an epmty string because the container
            // cmd hasn't run yet.
            dockerClient.waitContainer(containerId);

            final String log;
            try (LogStream logs = dockerClient.logs(containerId, stdout(), stderr())) {
                log = logs.readFully();
            }
            logger.info(log);
            response = log;
            dockerClient.removeContainer(containerId);

            return response;
        } catch (DockerException | InterruptedException e) {
            throw new CommandExecutionException(e);
        }
    }

    private void pullImage(DockerClient docker, String imageRepoName) throws DockerException, InterruptedException {
        final RegistryAuth registryAuth = RegistryAuth.builder()
                .email(System.getenv("DOCKER_EMAIL"))
                .username(System.getenv("DOCKER_USERNAME"))
                .password(System.getenv("DOCKER_PASSWORD"))
                .build();

        final int statusCode = docker.auth(registryAuth);
        logger.info("Docker registry auth status: " + statusCode);
        docker.pull(imageRepoName, registryAuth);
    }

    public static void main(String[] args) throws CommandExecutionException {
        DockerTask t = new DockerTask();
        List<String> envList = new ArrayList<>();
        envList.add("AWS_DEFAULT_REGION="+ System.getenv("AWS_DEFAULT_REGION"));
        envList.add("AWS_ACCESS_KEY_ID="+ System.getenv("AWS_ACCESS_KEY_ID"));
        envList.add("AWS_SECRET_ACCESS_KEY="+ System.getenv("AWS_SECRET_ACCESS_KEY"));
        List<String> cmdList = new ArrayList<>();
        cmdList.add("iam");
        cmdList.add("list-users");
        t.execute("sjsucohort6/docker_awscli:latest", envList, cmdList, "aws");
    }
}
