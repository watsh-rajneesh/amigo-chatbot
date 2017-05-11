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

/**
 * Job scheduler constants.
 *
 * @author rwatsh on 4/23/17.
 */
public interface JobConstants {
    String JOB_PARAM_MESSAGE = "message";
    String JOB_GRP_CHATBOT = "CHATBOT_GRP";
    String JOB_GRP_SLACKBOT = "SLACKBOT_GRP";
    String JOB_GRP_RIABOT = "RIABOT_GRP";
    String JOB_GRP_CP = "CP_GRP";
    String JOB_PARAM_MSG_SENDER = "slack_msg_sender";
    String JOB_PARAM_SLACK_SESSION = "slack_session";
    String JOB_PARAM_SLACK_CHANNEL = "slack_channel";
    String JOB_PARAM_BOT_TOK = "slack_bot_token";
    String JOB_PARAM_DBCLIENT = "dbclient";
}
