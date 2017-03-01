package edu.sjsu.amigo.cp.aws;

import edu.sjsu.amigo.cp.api.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rwatsh on 2/15/17.
 */

public class AWSCommandExecutorTest {
    @Test
    public void executeCommandTest() throws CommandExecutionException {

        List<String> envList = new ArrayList<>();
        envList.add("AWS_DEFAULT_REGION="+ System.getenv("AWS_DEFAULT_REGION"));
        envList.add("AWS_ACCESS_KEY_ID="+ System.getenv("AWS_ACCESS_KEY_ID"));
        envList.add("AWS_SECRET_ACCESS_KEY="+ System.getenv("AWS_SECRET_ACCESS_KEY"));
        List<String> cmdList = new ArrayList<>();
        cmdList.add("s3");
        cmdList.add("ls");
        String dockerImage = "sjsucohort6/docker_awscli:latest";
        String entryPoint = "aws";
        Command cmd = new Command.Builder(dockerImage, cmdList)
                .env(envList)
                .entryPoint(entryPoint)
                .build();
        CommandExecutor executor = CloudProviderFactory.getCloudProviderCmdExecutor(CloudProviderFactory.AWS);
        Response response = executor.executeCommand(cmd);
        Assert.assertFalse(response == null, "Response from comamnd execution should not be null");
    }
}
