package edu.sjsu.amigo.mp.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Message payload that will be sent to the backend to process and execute command.
 * This is client agnostic message so all clients - slack messenger or web UI will send the same message to the
 * backend.
 *
 * An example message received from slack messenger.
 *
 * Feb 26, 2017 4:17:02 AM edu.sjsu.amigo.slackbot.MessageListener$1 onEvent
 INFO: Received message from SlackUserImpl{
     id='USER_ID',
     userName='watsh',
     realName='Watsh Rajneesh',
     userMail='watsh.rajneesh@sjsu.edu',
     userSkype='rwatsh',
     userPhone='',
     userTitle='',
     isDeleted=false',
     isAdmin=true',
     isOwner=true',
     isPrimaryOwner=true',
     isRestricted=false',
     isUltraRestricted=false,
     timeZone=America/Los_Angeles,
     timeZoneLabel=Pacific Standard Time, timeZoneOffset=-28800}
     content [<@BOT_ID> aws iam list-users]
 }
 *
 * @author rwatsh on 2/26/17.
 */
public class Message {
    @JsonProperty
    private String msgReceivedTime;
    @JsonProperty
    private String userEmail;
    @JsonProperty
    private String userName;
    @JsonProperty
    private String userId;
    @JsonProperty
    private String content;
    @JsonProperty
    private String intent;

    public Message() {}

    public Message(String time, String userEmail, String userName, String content, String intent) {
        this.msgReceivedTime = time;
        this.userEmail = userEmail;
        this.userName = userName;
        this.content = content;
        this.intent = intent;
    }

    public String getMsgReceivedTime() {
        return msgReceivedTime;
    }

    public void setMsgReceivedTime(String msgReceivedTime) {
        this.msgReceivedTime = msgReceivedTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msgReceivedTime='" + msgReceivedTime + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                ", intent='" + intent + '\'' +
                '}';
    }
}
