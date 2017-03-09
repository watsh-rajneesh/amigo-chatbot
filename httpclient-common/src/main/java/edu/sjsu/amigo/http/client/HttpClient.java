package edu.sjsu.amigo.http.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

import java.io.*;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Wrapper over http methods using unirest-java library.
 *
 * Below methods are synchronous.
 *
 * For asynch methods use the unirest-java library directly.
 *
 * @author rwatsh on 2/27/17.
 * @see "http://unirest.io/java.html"
 */
public class HttpClient implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(HttpClient.class.getName());
    private String user;
    private String password;

    /**
     * Create a http client with no auth (user anonymous).
     */
    public HttpClient() {
        init();
    }

    /**
     * Create a http client with basic auth.
     *
     * @param user
     * @param passwd
     */
    public HttpClient(String user, String passwd) {
        init();
        this.user = user;
        this.password = passwd;
    }

    /**
     * Initialize the instance.
     */
    private void init() {
        // setup to serialize Json from\to Object using jackson object mapper
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public <T> Response<T> get(String url, Class<T> clazz) throws HttpClientException {
        return get(url, null, null, null, clazz);
    }

    public <T> Response<T> get(String url,
                      Map<String, String> routeParamsMap,
                      Map<String, String> queryStringMap,
                      Map<String, String> headersMap,
                      Class<T> clazz) throws HttpClientException {
        GetRequest request = Unirest.get(url);
        return executeGetRequest(routeParamsMap, queryStringMap, headersMap, clazz, request);
    }

    public <T> Response<T> head(String url, Class<T> clazz) throws HttpClientException {
        return head(url, null, null, null, clazz);
    }

    public <T> Response<T> head(String url,
                     Map<String, String> routeParamsMap,
                     Map<String, String> queryStringMap,
                     Map<String, String> headersMap,
                     Class<T> clazz) throws HttpClientException {
        GetRequest request = Unirest.head(url);
        return executeGetRequest(routeParamsMap, queryStringMap, headersMap, clazz, request);
    }

    public Response<Object> post(String url, Object bodyObject) throws HttpClientException {
        return post(url, null, null, null, bodyObject);
    }

    public Response<Object> post(String url,
                     Map<String, String> routeParamsMap,
                     Map<String, String> queryStringMap,
                     Map<String, String> headersMap,
                     Object bodyObject) throws HttpClientException {
        HttpRequestWithBody request = Unirest.post(url);
        return executeHttpRequestWithBody(routeParamsMap, queryStringMap, headersMap, bodyObject, request);
    }

    public Response<Object> put(String url, Object bodyObject) throws HttpClientException {
        return put(url, null, null, null, bodyObject);
    }

    public Response<Object> put(String url,
                      Map<String, String> routeParamsMap,
                      Map<String, String> queryStringMap,
                      Map<String, String> headersMap,
                      Object bodyObject) throws HttpClientException {
        HttpRequestWithBody request = Unirest.post(url);
        return executeHttpRequestWithBody(routeParamsMap, queryStringMap, headersMap, bodyObject, request);
    }

    public Response<Object> delete(String url, Object bodyObject) throws HttpClientException {
        return delete(url, null, null, null, bodyObject);
    }

    public Response<Object> delete(String url,
                         Map<String, String> routeParamsMap,
                         Map<String, String> queryStringMap,
                         Map<String, String> headersMap,
                         Object bodyObject) throws HttpClientException {
        HttpRequestWithBody request = Unirest.delete(url);
        return executeHttpRequestWithBody(routeParamsMap, queryStringMap, headersMap, bodyObject, request);

    }


    private <T> Response executeGetRequest(Map<String, String> routeParamsMap, Map<String, String> queryStringMap, Map<String, String> headersMap, Class<T> clazz, GetRequest request) throws HttpClientException {
        if (user != null && password != null) {
            request.basicAuth(user, password);
        }

        if(routeParamsMap != null) {
            for (Map.Entry<String, String> entry : routeParamsMap.entrySet()) {
                request.routeParam(entry.getKey(), entry.getValue());
            }
        }
        if (queryStringMap != null) {
            for (Map.Entry<String, String> entry : queryStringMap.entrySet()) {
                request.queryString(entry.getKey(), entry.getValue());
            }
        }
        if (headersMap != null) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
        }
        try {
            HttpResponse<T> response = request.asObject(clazz);
            Response<T> r = new Response();
            r.setStatusCode(response.getStatus());
            r.setStatusText(response.getStatusText());
            r.setRawBody(getStringFromInputStream(response.getRawBody()));
            r.setParsedObject(response.getBody());
            return r;
        } catch (UnirestException e) {
            throw new HttpClientException(e);
        }
    }


    private Response executeHttpRequestWithBody(Map<String, String> routeParamsMap, Map<String, String> queryStringMap, Map<String, String> headersMap, Object bodyObject, HttpRequestWithBody request) throws HttpClientException {
        if (user != null && password != null) {
            request.basicAuth(user, password);
        }
        if(routeParamsMap != null) {
            for (Map.Entry<String, String> entry : routeParamsMap.entrySet()) {
                request.routeParam(entry.getKey(), entry.getValue());
            }
        }
        if (queryStringMap != null) {
            for (Map.Entry<String, String> entry : queryStringMap.entrySet()) {
                request.queryString(entry.getKey(), entry.getValue());
            }
        }
        if (headersMap != null) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                request.header(entry.getKey(), entry.getValue());
            }
        }
        if(bodyObject != null) {
            request.body(bodyObject);
        }
        try {
            HttpResponse<JsonNode> response = request.asJson();
            Response<Object> r = new Response();
            r.setStatusCode(response.getStatus());
            r.setStatusText(response.getStatusText());
            r.setRawBody(getStringFromInputStream(response.getRawBody()));
            r.setParsedObject(response.getBody().isArray() ?
                    response.getBody().getArray() :
                    response.getBody().getObject());

            return r;
        } catch (UnirestException e) {
            throw new HttpClientException(e);
        }
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        if (is == null) return "";

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    public static String getIntentFromWitAI(String message) throws UnirestException, UnsupportedEncodingException {
        String url = "https://api.wit.ai/message?v=20160526&q=" + URLEncoder.encode(message, "UTF-8");
        HttpResponse<String> response = Unirest.get(url)
                .header("authorization", "Bearer " + System.getenv("WIT_AI_SERVER_ACCESS_TOKEN"))
                .header("cache-control", "no-cache")
                .asString();
        return response.getBody();
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * <p>
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     * <p>
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     * <p>
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     * <p>
     * <p>Note that unlike the {@link Closeable#close close}
     * method of {@link Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        Unirest.shutdown();
    }

    public static void main(String[] args) throws UnsupportedEncodingException, UnirestException {
        String intent = getIntentFromWitAI("aws iam list-users");
        System.out.println(intent);
    }
}
