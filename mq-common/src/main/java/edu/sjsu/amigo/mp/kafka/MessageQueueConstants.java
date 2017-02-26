package edu.sjsu.amigo.mp.kafka;

/**
 * Constants to be used by both message producers and consumers.
 *
 * @author rwatsh on 2/26/17.
 */
public interface MessageQueueConstants {
    // Topics
    /**
     * user message topic on which incoming client message is received.
     */
    String USER_MSG_TOPIC = "user_msg";

    // Client groups
    /**
     * chatbot group is the group of client consumers receiving message from user message topic.
     * Only one consumer from the group of consumers will receive a message from the topic.
     * 
     */
    String AMIGO_CHATBOT_GROUP = "amigo-chatbot-group";
}
