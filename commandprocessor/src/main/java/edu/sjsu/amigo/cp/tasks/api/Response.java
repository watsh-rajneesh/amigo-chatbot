package edu.sjsu.amigo.cp.tasks.api;

/**
 * This class represents the response returned upon execution of command.
 *
 * @author rwatsh on 2/15/17.
 */
public class Response {
    private String msg;

    public Response(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Response{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
