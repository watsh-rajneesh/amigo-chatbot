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

package edu.sjsu.amigo.chatbot.api;

import java.util.List;

/**
 * Interface for AI Client.
 *
 * @author rwatsh on 3/25/17.
 */
public interface AIClient {
    /**
     * Gets the intent of the message.
     *
     * @param message   user message.
     * @return  returns a sorted list of intent tokens found in the message.
     */
    List<String> getIntent(String message);
}
