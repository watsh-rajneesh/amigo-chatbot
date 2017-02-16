package edu.sjsu.amigo.cp.tasks.aws;

import edu.sjsu.amigo.cp.tasks.api.*;
import edu.sjsu.amigo.cp.tasks.docker.DockerTask;

/**
 * Executor for AWS commands.
 *
 * @author rwatsh on 2/15/17.
 */
public class AWSCommandExecutor implements CommandExecutor {

    @Override
    public Response executeCommand(Command cmd) throws CommandExecutionException {
        DockerTask t = new DockerTask();
        String msg = t.execute(cmd.getDockerImage(),
                cmd.getEnvList(),
                cmd.getCommandList(),
                cmd.getEntryPoint());
        return new Response(msg);
    }
}
