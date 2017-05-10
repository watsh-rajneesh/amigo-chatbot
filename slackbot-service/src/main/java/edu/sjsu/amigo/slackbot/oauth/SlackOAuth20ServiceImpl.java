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

package edu.sjsu.amigo.slackbot.oauth;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuth20ServiceImpl;


public class SlackOAuth20ServiceImpl extends OAuth20ServiceImpl {

    public SlackOAuth20ServiceImpl (DefaultApi20 api, OAuthConfig config) {
        super(api, config);
    }

    public String getAuthorizationUrl(Token requestToken, String team) {
        SlackApi slackApi = (SlackApi)this.getApi();
        slackApi.setTeam(team);
        return super.getAuthorizationUrl(requestToken);
    }
}