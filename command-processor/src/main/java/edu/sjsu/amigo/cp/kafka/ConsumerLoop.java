package edu.sjsu.amigo.cp.kafka;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import edu.sjsu.amigo.mp.kafka.SlackMessage;
import edu.sjsu.amigo.mp.util.JsonUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static edu.sjsu.amigo.mp.kafka.MessageQueueConstants.AMIGO_CHATBOT_GROUP;
import static edu.sjsu.amigo.mp.kafka.MessageQueueConstants.USER_MSG_TOPIC;

/**
 * A Kafka message consumer. As soon as it receives a message it will spawn a job to process it.
 *
 * Based on https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-9-consumer-client/
 *
 * @author rwatsh on 2/25/17.
 */
public class ConsumerLoop implements Runnable {
    private final KafkaConsumer<String, String> consumer;
    private final List<String> topics;
    private final int id;

    public ConsumerLoop(int id,
                        String groupId,
                        List<String> topics) {
        this.id = id;
        this.topics = topics;
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", groupId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {
        try {
            consumer.subscribe(topics);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
                /**
                 * A typical slack message look like below:
                 * 14:08:16.407 [pool-1-thread-1] DEBUG org.apache.kafka.clients.consumer.internals.Fetcher
                 * - Sending fetch for partitions [user_msg-0] to broker 192.168.86.89:9092 (id: 1001 rack: null)
                 0:
                 {
                     partition=0, offset=5,
                         value={
                             "msgReceivedTime" : "1488146896328",
                             "userEmail" : "watsh.rajneesh@sjsu.edu",
                             "userName" : "watsh",
                             "content" : " aws iam list-users",
                             "intent" : " aws iam list-users",
                             "channelId" : "C2A710WGG",
                             "slackBotToken" : "xoxb-78235458209-TzSYTrgAK9SOdYClKgh1YJKQ"
                         }
                 }
                 */
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    String value = record.value();
                    data.put("value", value);
                    if (value != null && !value.trim().isEmpty()) {
                        try {
                            SlackMessage slackMessage = JsonUtils.convertJsonToObject(value, SlackMessage.class);
                            if (slackMessage != null) {
                                String slackBotToken = slackMessage.getSlackBotToken();

                                SlackSession session = SlackSessionFactory.createWebSocketSlackSession(slackBotToken);
                                session.connect();
                                String channelId = slackMessage.getChannelId();
                                String ackMessage = "Message received in the backend";
                                if (channelId != null && !channelId.trim().isEmpty()) {
                                    SlackChannel channel = session.findChannelById(channelId);
                                    session.sendMessage(channel, ackMessage);
                                } else {
                                    String userEmail = slackMessage.getUserEmail();
                                    if (userEmail != null && !userEmail.trim().isEmpty()) {
                                        SlackUser slackUser = session.findUserByEmail(userEmail);
                                        session.sendMessageToUser(slackUser, ackMessage, null);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(this.id + ": " + data);
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }

    /**
     * Test client.
     *
     * @param args
     */
    public static void main(String[] args) {
        int numConsumers = 3;
        String groupId = AMIGO_CHATBOT_GROUP;
        List<String> topics = Arrays.asList(USER_MSG_TOPIC);
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers);

        final List<ConsumerLoop> consumers = new ArrayList<>();
        for (int i = 0; i < numConsumers; i++) {
            ConsumerLoop consumer = new ConsumerLoop(i, groupId, topics);
            consumers.add(consumer);
            executor.submit(consumer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (ConsumerLoop consumer : consumers) {
                    consumer.shutdown();
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
