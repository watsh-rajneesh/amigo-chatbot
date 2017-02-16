package edu.sjsu.amigo.cp.tasks.api;

import edu.sjsu.amigo.cp.tasks.aws.AWSCommandExecutor;
import edu.sjsu.amigo.cp.tasks.azure.AzureCommandExecutor;

/**
 * An abstract factory class for cloud providers.
 *
 * @author rwatsh on 2/15/17.
 */
public abstract class CloudProviderFactory {

    public static final String AWS = "aws";

    /**
     * Get the cloud provider specific executor class based on provider's name.
     *
     * @param providerName
     * @return
     */
    public static final CommandExecutor getCloudProviderCmdExecutor(String providerName) {
        if (providerName == null) return null;

        switch (providerName.toLowerCase()) {
            case "aws": return new AWSCommandExecutor();
            case "azure": return new AzureCommandExecutor();
            default: return null;
        }
    }
}
