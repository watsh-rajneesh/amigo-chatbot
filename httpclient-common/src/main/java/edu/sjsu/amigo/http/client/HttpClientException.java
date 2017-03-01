package edu.sjsu.amigo.http.client;

/**
 * A friendly http client exception.
 *
 * @author rwatsh on 2/27/17.
 */
public class HttpClientException extends Exception {
    public HttpClientException(String msg) {
        super(msg);
    }
    public HttpClientException(Throwable throwable) {
        super(throwable);
    }
}
