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

package edu.sjsu.amigo.scheduler.jobs;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.EverythingMatcher.allJobs;
import static org.quartz.impl.matchers.GroupMatcher.groupEquals;
import static org.quartz.impl.matchers.KeyMatcher.keyEquals;

/**
 * Wrapper for job scheduler instance implemented as a singleton such that
 * there is just one scheduler for the application.
 *
 * @author rwatsh on 2/27/17.
 */
public class JobManager implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(JobManager.class.getName());

    private final Scheduler scheduler;
    private static JobManager instance = null;

    private JobManager() throws SchedulerException {
        // the 'default' scheduler is defined in "quartz.properties" found
        // in the current working directory, in the classpath, or
        // resorts to a fall-back default that is in the quartz.jar
        SchedulerFactory sf = new StdSchedulerFactory();
        scheduler = sf.getScheduler();
    }


    public static JobManager getInstance() throws SchedulerException {
        if (instance == null) {
            synchronized (JobManager.class) {
                if (instance == null) {
                    instance = new JobManager();
                }
            }
        }
        return instance;
    }

    /**
     * Register a job listener.
     *
     * @param listener
     * @throws SchedulerException
     */
    public void registerJobListener(JobListener listener) throws SchedulerException {
        scheduler.getListenerManager().addJobListener(listener,
                allJobs());
    }

    /**
     * Listener for a specific job.
     *
     * @param listener
     * @param jobName
     * @param groupName
     * @throws SchedulerException
     */
    public void registerJobListener(JobListener listener, String jobName, String groupName) throws SchedulerException {
        scheduler.getListenerManager().addJobListener(listener,
                keyEquals(jobKey(jobName, groupName)));
    }

    /**
     * Start scheduler.
     *
     * @throws SchedulerException
     */
    public void startScheduler() throws SchedulerException {
        // Scheduler will not execute jobs until it has been started (though they
        // can be scheduled before start())
        scheduler.start();
    }

    /**
     * Schedule a job.
     *
     * @param jobClazz
     * @param jobName
     * @param tenantName
     * @param jobDataMap
     * @throws SchedulerException
     */
    public JobDetail scheduleJob(Class<? extends Job> jobClazz,
                                 String jobName,
                                 String tenantName,
                                 JobDataMap jobDataMap) throws SchedulerException {
        JobDetail job = newJob(jobClazz)
                .withIdentity(jobName, tenantName)
                .usingJobData(jobDataMap)
                .build();


        // Define a Trigger that will fire "now", and not repeat
        Trigger trigger = newTrigger()
                //.withIdentity(TRIGGER_NAME + "-" + jobClazz.getTypeName(), tenantName)
                .startNow()
                .build();

        // Schedule the job with the trigger
        scheduler.scheduleJob(job, trigger);
        return job;
    }

    public JobDetail scheduleJob(Class<? extends Job> jobClazz,
                                 String jobName,
                                 String tenantName,
                                 JobDataMap jobDataMap, int intervalInMins) throws SchedulerException {
        JobDetail job = newJob(jobClazz)
                .withIdentity(jobName, tenantName)
                .usingJobData(jobDataMap)
                .build();


        // Define a Trigger that will fire "now", and not repeat
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInMinutes(intervalInMins)
                        .repeatForever())
                .build();

        // Schedule the job with the trigger
        scheduler.scheduleJob(job, trigger);
        return job;
    }

    /**
     * Get a list of jobs.
     *
     * @param tenantName
     * @return
     * @throws SchedulerException
     */
    public List<JobDetail> getJobsList(String tenantName) throws SchedulerException {
        List<JobDetail> jobDetails = new ArrayList<>();
        // enumerate each job group
        for(String group: scheduler.getJobGroupNames()) {
            // enumerate each job in group
            for(JobKey jobKey : scheduler.getJobKeys(groupEquals(group))) {
                LOGGER.info("Found job identified by: " + jobKey);
                jobDetails.add(scheduler.getJobDetail(jobKey));
            }
        }
        return jobDetails;
    }


    /**
     * Scheduler will allow currently executing jobs to complete and gracefully shutdown.
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        //shutdown() does not return until executing Jobs complete execution
        scheduler.shutdown(true);
    }

    public boolean findJob(String jobName, String tenant) throws SchedulerException {
        // enumerate each job in group
        for(JobKey jobKey : scheduler.getJobKeys(groupEquals(tenant))) {
            LOGGER.info("Found job identified by: " + jobKey);
            if (jobKey.getName().equalsIgnoreCase(jobName)) {
                return true;
            }
        }
        return false;
    }
}