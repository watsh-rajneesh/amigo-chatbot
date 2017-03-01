package edu.sjsu.amigo.cp.jobs;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import edu.sjsu.amigo.cp.api.*;
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
                String userEmail = slackMessage.getUserEmail();

                String ackMessage = "Message received in the backend";
                sendMessageToUser(userEmail, session, channelId, ackMessage);

                // Lookup intent in the DB and get the following:
                // - dockerImage, cmd and entry point.
                // Make a REST call to user service to get the user's (by userEmail) AWS creds.

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
            e.printStackTrace();
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
