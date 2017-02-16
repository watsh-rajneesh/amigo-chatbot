package edu.sjsu.amigo.cp.tasks.api;

import java.util.List;

/**
 * This class represents a command. It is generic and can be specialized for each cloud provider specific command as needed.
 *
 * @author rwatsh on 2/14/17.
 */
public class Command {
    /**
     * Execute a command with docker.
     *
     * @param dockerImage   the docker image to use to execute the command on.
     * @param envList       the list of environment vars to be passed to the command to be executed in docker container
     * @param commandList   commmand as list for eg. ["s3", "ls"] is list of parts of command - s3 ls.
     * @param entryPoint    provider name for eg. "aws"
     * @throws CommandExecutionException    raised when any error occurs in executing the command.
     */
    //void execute(String dockerImage, List<String> envList, List<String> commandList, String entryPoint) throws CommandExecutionException;

    protected String dockerImage; // mandatory property
    protected List<String> envList; // optional
    protected List<String> commandList; // mandatory property
    protected String entryPoint; // optional

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
