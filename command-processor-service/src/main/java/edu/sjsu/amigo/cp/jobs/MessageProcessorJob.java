package edu.sjsu.amigo.cp.jobs;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import edu.sjsu.amigo.cp.api.*;
import edu.sjsu.amigo.mp.kafka.IntentElem;
import edu.sjsu.amigo.mp.kafka.SlackMessage;
import edu.sjsu.amigo.mp.util.JsonUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Job to process the incoming message asynchronously.
 *
 * @author rwatsh on 2/27/17.
 */
public class MessageProcessorJob implements Job {
    private String message;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            SlackMessage slackMessage = JsonUtils.convertJsonToObject(message, SlackMessage.class);
            if (slackMessage != null) {
                String slackBotToken = slackMessage.getSlackBotToken();

                SlackSession session = SlackSessionFactory.createWebSocketSlackSession(slackBotToken);
                session.connect();
                String channelId = slackMessage.getChannelId();
                String intent = slackMessage.getIntent();
                List<IntentElem> intentsList = null;

                /**
                 * Only if the intent string is a JSON array means wit.ai returned some intents for the message.
                 */
                if (JsonUtils.parseJson(intent).isArray()) {
                    intentsList = JsonUtils.convertJsonArrayToList(intent, IntentElem.class);
                }

                String userEmail = slackMessage.getUserEmail();

                String ackMessage = "Message received in the backend";
                sendMessageToUser(userEmail, session, channelId, ackMessage);

                // TODO
                // 1. Lookup intentsList in the DB and get the following:
                // - dockerImage, cmdList and entry point.

                /*
                 * For simplicity sake, we will lookup with intentsList such that,
                 * - each intent element in the intentsList should be found in a single NoSQL document
                 * - then we get the cloud provider name, dockerImage, cmdList and entry point from the DB.
                 *
                 * If intent string is not an array, then we try getting the cloud provider from the message as first
                 * word in the intent. If
                 */

                // 2. Make a REST call to user service to get the user's (by userEmail) AWS creds.

                // Execute Command
                List<String> envList = new ArrayList<>();
                envList.add("AWS_DEFAULT_REGION="+ System.getenv("AWS_DEFAULT_REGION"));
                envList.add("AWS_ACCESS_KEY_ID="+ System.getenv("AWS_ACCESS_KEY_ID"));
                envList.add("AWS_SECRET_ACCESS_KEY="+ System.getenv("AWS_SECRET_ACCESS_KEY"));
                String[] cmdArray = intent.trim().split(" ");
                String providerName = cmdArray[0];
                List<String> cmdList = new ArrayList<>();
                for (int i = 1; i < cmdArray.length; i++) {
                    cmdList.add(cmdArray[i]);
                }
                String dockerImage = "sjsucohort6/docker_awscli:latest";
                String entryPoint = "aws";
                Command cmd = new Command.Builder(dockerImage, cmdList)
                        .env(envList)
                        .entryPoint(entryPoint)
                        .build();
                CommandExecutor executor = CloudProviderFactory.getCloudProviderCmdExecutor(providerName);
                Response response = executor.executeCommand(cmd);
                sendMessageToUser(userEmail, session, channelId, response.getMsg());
            }
        } catch (IOException | CommandExecutionException e) {
            throw new JobExecutionException(e);
        }
    }

    private void sendMessageToUser(String userEmail, SlackSession session, String channelId, String message) {
        if (channelId != null && !channelId.trim().isEmpty()) {
            SlackChannel channel = session.findChannelById(channelId);
            session.sendMessage(channel, message);
        } else {

            if (userEmail != null && !userEmail.trim().isEmpty()) {
                SlackUser slackUser = session.findUserByEmail(userEmail);
                session.sendMessageToUser(slackUser, message, null);
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
