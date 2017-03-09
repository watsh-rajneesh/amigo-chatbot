package edu.sjsu.amigo.slackbot;

/**
 * @author rwatsh on 3/7/17.
 */
public class InvalidMessageException extends RuntimeException {
    public InvalidMessageException(Throwable t) {
        super(t);
    }

    public InvalidMessageException(String msg) {
        super(msg);
    }
}
