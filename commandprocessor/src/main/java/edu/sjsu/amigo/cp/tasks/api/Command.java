package edu.sjsu.amigo.cp.tasks.api;

import java.util.List;

/**
 * @author rwatsh on 2/14/17.
 */
public interface Command {
    /**
     * Execute a command with docker.
     *
     * @param dockerImage   the docker image to use to execute the command on.
     * @param envList       the list of environment vars to be passed to the command to be executed in docker container
     * @param commandList   commmand as list for eg. ["s3", "ls"] is list of parts of command - s3 ls.
     * @param entryPoint    provider name for eg. "aws"
     * @throws CommandExecutionException    raised when any error occurs in executing the command.
     */
    void execute(String dockerImage, List<String> envList, List<String> commandList, String entryPoint) throws CommandExecutionException;

}
