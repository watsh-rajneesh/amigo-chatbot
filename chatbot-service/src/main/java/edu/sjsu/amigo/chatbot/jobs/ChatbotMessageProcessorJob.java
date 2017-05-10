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

package edu.sjsu.amigo.chatbot.jobs;

import edu.sjsu.amigo.chatbot.msg.MessageProcessor;
import edu.sjsu.amigo.mp.model.Message;
import edu.sjsu.amigo.scheduler.jobs.JobConstants;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author rwatsh on 4/23/17.
 */
public class ChatbotMessageProcessorJob  implements Job {
    private Message message;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
            message = (Message)jobDataMap.get(JobConstants.JOB_PARAM_MESSAGE);
            MessageProcessor.processMessage(message);
        } catch( Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
