package edu.sjsu.amigo.http.client.test;

import edu.sjsu.amigo.http.client.HttpClient;
import edu.sjsu.amigo.http.client.Response;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rwatsh on 3/1/17.
 */
public class HttpClientTest {

    @Test
    public void testGetRequest() throws Exception {
        try(HttpClient c = new HttpClient()) {
            Response<String> resp = c.get("http://httpbin.org/get", String.class);
            System.out.println(resp);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testHeadRequest() throws Exception {
        try (HttpClient c = new HttpClient()) {
            Response<String> resp = c.head("http://httpbin.org/get", String.class);
            System.out.println(resp);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testPostRequest() throws Exception {
        try (HttpClient c = new HttpClient()) {
            String data = "test";
            Response<Object> resp = c.post("http://httpbin.org/post", data);
            System.out.println(resp);
        } catch (Exception e) {
            throw e;
        }
    }

    //@Test
    // TODO - broken for now!
    public void testPutRequest() throws Exception {
        try (HttpClient c = new HttpClient()) {
            Map<String, String> data = new HashMap<>();
            String value = "[\n" +
                    "        {\"firstName\":\"John\", \"lastName\":\"Doe\"},\n" +
                    "        {\"firstName\":\"Anna\", \"lastName\":\"Smith\"},\n" +
                    "        {\"firstName\":\"Peter\", \"lastName\":\"Jones\"}\n" +
                    "    ]";
            data.put("data", value);
            Response<Object> resp = c.put("http://httpbin.org/put", value);
            System.out.println(resp);
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void testDeleteRequest() throws Exception {
        try (HttpClient c = new HttpClient()) {
            String data = "test";
            Response<Object> resp = c.delete("http://httpbin.org/delete", data);
            System.out.println(resp);
        } catch (Exception e) {
            throw e;
        }
    }
}