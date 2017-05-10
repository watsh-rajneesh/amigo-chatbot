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

package edu.sjsu.amigo.http.client;

import lombok.Data;

import java.util.Map;

/**
 * Http response wrapper.
 *
 * @author rwatsh on 3/1/17.
 */
@Data
public class Response<T> {
    private int statusCode;
    private String statusText;
    private Map<String, String> headers;
    private String rawBody;
    private T parsedObject;
}
