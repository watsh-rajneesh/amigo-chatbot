/*
 * Copyright (c) 2017 San Jose State University.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package edu.sjsu.amigo.cp.kafka;

import edu.sjsu.amigo.cp.jobs.JobManager;
import edu.sjsu.amigo.cp.jobs.MessageProcessorJob;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;

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
                 *
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
                    processMessageAsync(value);
                    System.out.println(this.id + ": " + data);
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
        }
    }

    private void processMessageAsync(String value) {
        if (value != null && !value.trim().isEmpty()) {
            try {
                // Some unique job name
                String jobName = "MESG-JOB-" + UUID.randomUUID().toString();
                String groupName = "CHATBOT-GRP";
                JobDataMap params = new JobDataMap();
                params.put("message", value);
                JobManager.getInstance().scheduleJob(MessageProcessorJob.class, jobName, groupName, params);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void shutdown() {
        consumer.wakeup();
    }

    /**
     * Run the client program.
     *
     * @param args
     */
    public static void main(String[] args) throws SchedulerException {
        //Start the job scheduler
        JobManager.getInstance().startScheduler();

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
