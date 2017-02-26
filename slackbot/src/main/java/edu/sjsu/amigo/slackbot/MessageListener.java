package edu.sjsu.amigo.slackbot;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;
import edu.sjsu.amigo.mp.kafka.Message;
import edu.sjsu.amigo.mp.kafka.MessageProducer;
import edu.sjsu.amigo.mp.util.JsonUtils;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Listens to messages sent on channels amigo chatbot joined on slack or messges sent directly to it.
 *
 * @author rwatsh on 2/26/17.
 */
public class MessageListener {
    private final static Logger logger = Logger.getLogger(MessageListener.class.getName());
    /**
     * This method shows how to register a listener on a SlackSession
     */
    public void registerListener(SlackSession session)
    {
        // first define the listener
        SlackMessagePostedListener messagePostedListener = new SlackMessagePostedListener()
        {
            public void onEvent(SlackMessagePosted event, SlackSession session)
            {
                SlackChannel channelOnWhichMessageWasPosted = event.getChannel();
                String messageContent = event.getMessageContent();
                SlackUser messageSender = event.getSender();
                logger.info("Received message from " + messageSender + " content [" + messageContent + "]");

                if (isNotNullOrEmpty(messageContent)) {
                    // Parse the message content
                    String parsedMessage = parseMessage(messageContent);

                    if (parsedMessage != null) {
                        // TODO Get the intent from wit.ai here...
                        String intent = parsedMessage;

                        // Construct the message in JSON
                        String currentTime = "" + (new Date()).getTime();

                        Message msg = new Message(currentTime, messageSender.getUserMail(), messageSender.getUserName(),
                                parsedMessage, intent);

                        String msgJson = null;

                        try {
                            msgJson = JsonUtils.convertObjectToJson(msg);
                        } catch (IOException e) {

                            logger.log(Level.SEVERE, "Error in converting message to JSON", e);
                        }

                        if (msgJson != null) {
                            // Send message to topic on kafka MQ
                            try (MessageProducer producer = new MessageProducer()) {
                                producer.sendUserMessage(msgJson);
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "Error in publishing message to queue", e);
                            }
                        }
                    }
                }
            }
        };
        //add it to the session
        session.addMessagePostedListener(messagePostedListener);

        //that's it, the listener will get every message post events the bot can get notified on
        //(IE: the messages sent on channels it joined or sent directly to it)
    }

    private boolean isNotNullOrEmpty(String messageContent) {
        return messageContent != null && !messageContent.trim().isEmpty();
    }

    /**
     * Parse the message to see if content includes <@BOT_ID> in the message.
     * If it does, the message was meant for our chatbot hence get the message after the @BOT_ID.
     *
     * @param messageContent
     * @return
     */
    private String parseMessage(String messageContent) {
        String botId = "<@" + System.getenv("BOT_ID") + ">";
        String parsedMessage = null;
        if (messageContent.contains(botId)) {
            // Get the text after <@BOT_ID> for example, from "@amigo aws iam list-users" get "aws iam list-users".
            parsedMessage = messageContent.split(botId)[1];
        }
        return parsedMessage;
    }

    public static void main(String[] args) throws IOException
    {
        SlackSession session = SlackSessionFactory.createWebSocketSlackSession(System.getenv("SLACK_BOT_TOKEN"));
        session.connect();
        MessageListener listener = new MessageListener();
        listener.registerListener(session);
    }

}
