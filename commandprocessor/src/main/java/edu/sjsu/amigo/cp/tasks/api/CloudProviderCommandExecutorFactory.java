package edu.sjsu.amigo.cp.tasks.api;

import edu.sjsu.amigo.cp.tasks.aws.AWSCommandExecutor;

/**
 * An abstract factory class for cloud providers.
 *
 * @author rwatsh on 2/15/17.
 */
public abstract class CloudProviderCommandExecutorFactory {

    /**
     * Get the cloud provider specific executor class based on provider's name.
     *
     * @param providerName
     * @return
     */
    public static final CloudProviderCommandExecutorFactory getCloudProviderExecutor(String providerName) {
        if (providerName == null) return null;

        switch (providerName.toLowerCase()) {
            case "aws": return new AWSCommandExecutor();
            default: return null;
        }
    }

    public abstract void executeCommand(Command cmd) throws CommandExecutionException;
}
