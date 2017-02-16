package edu.sjsu.amigo.cp.tasks.aws;

import edu.sjsu.amigo.cp.tasks.api.CloudProviderCommandExecutorFactory;
import edu.sjsu.amigo.cp.tasks.api.Command;
import edu.sjsu.amigo.cp.tasks.api.CommandExecutionException;
import edu.sjsu.amigo.cp.tasks.docker.DockerTask;

/**
 * Executor for AWS commands.
 *
 * @author rwatsh on 2/15/17.
 */
public class AWSCommandExecutor extends CloudProviderCommandExecutorFactory {

    @Override
    public void executeCommand(Command cmd) throws CommandExecutionException {
        DockerTask t = new DockerTask();
        t.execute(cmd.getDockerImage(),
                cmd.getEnvList(),
                cmd.getCommandList(),
                cmd.getEntryPoint());
    }
}
