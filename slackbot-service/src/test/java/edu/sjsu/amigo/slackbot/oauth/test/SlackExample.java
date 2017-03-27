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

package edu.sjsu.amigo.slackbot.oauth.test;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;
import edu.sjsu.amigo.slackbot.oauth.SlackApi;
import edu.sjsu.amigo.slackbot.oauth.SlackOAuth20ServiceImpl;

import java.util.Random;
import java.util.Scanner;

/**
 * Example for using oauth with Slack App.
 *
 * @author rwatsh
 */

public class SlackExample {

    private static final String NETWORK_NAME = "Slack";
    private static final Token EMPTY_TOKEN = null;

    // Replace these with your client id and secret
    private static final String CLIENT_ID = "71247616086.160494289335";
    private static final String CLIENT_SECRET = "e07b8ffa7c8a96a35e62d2922a195bef";
    private static final String SLACK_TEAM = ""; // optional
    //TODO change to use proxy host in AWS
    private static final String SLACKBOT_HOST = "localhost";
    public static final String PORT = "8443";
    public static final String PROTOCOL = "https";

    public static void main(String[] args) {

        final String secretState = "secret" + new Random().nextInt(999_999);
        final OAuthService service = new ServiceBuilder()
                .provider(SlackApi.class)
                .apiKey(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .state(secretState)
                .scope("channels:read chat:write:bot")
                .callback(PROTOCOL + "://" + SLACKBOT_HOST + ":" + PORT + "/oauth2/callback")
                .build();

        final Scanner in = new Scanner(System.in, "UTF-8");

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Authorization URL
        System.out.println("Fetching the Authorization URL...");

        String authorizationUrl;
        if(!SLACK_TEAM.isEmpty()){
            authorizationUrl = ((SlackOAuth20ServiceImpl) service).getAuthorizationUrl(EMPTY_TOKEN, SLACK_TEAM);
        } else {
            authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        }

        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize ScribeJava here:");
        System.out.println(authorizationUrl);
        System.out.println("And paste the authorization code here");
        System.out.print(">>");
        final Verifier verifier = new Verifier(in.nextLine());
        System.out.println();

        System.out.println("And paste the state from server here. We have set 'secretState'='" + secretState + "'.");
        System.out.print(">>");
        final String value = in.nextLine();
        if (secretState.equals(value)) {
            System.out.println("State value does match!");
        } else {
            System.out.println("Ooops, state value does not match!");
            System.out.println("Expected = " + secretState);
            System.out.println("Got      = " + value);
            System.out.println();
        }

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        final Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken.getToken() + " )");
        System.out.println();

        // All we need is the token, the rest is done by https://github.com/OvertimeNZ/java-slack-api as it is a lot nicer to use.
    }
}
