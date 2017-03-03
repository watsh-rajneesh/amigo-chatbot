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
