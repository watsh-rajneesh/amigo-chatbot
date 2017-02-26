package edu.sjsu.amigo.mp.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Message from slack messenger. It has properties specific to slack.
 *
 * @author rwatsh on 2/26/17.
 */
public class SlackMessage extends Message {
    @JsonProperty
    private String channelId;
    // TODO make it encrypted - should not be visible in clear text.
    @JsonProperty
    private String slackBotToken;

    public SlackMessage(String time, String userEmail, String userName, String content, String intent,
                         String channelName, String slackBotToken) {
        super(time, userEmail, userName, content, intent);
        this.channelId = channelName;
        this.slackBotToken = slackBotToken;
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getSlackBotToken() {
        return slackBotToken;
    }

    public void setSlackBotToken(String slackBotToken) {
        this.slackBotToken = slackBotToken;
    }

    @Override
    public String toString() {
        return "SlackMessage{" +
                ", channelId='" + channelId + '\'' +
                ", slackBotToken='" + slackBotToken + '\'' +
                '}';
    }
}
