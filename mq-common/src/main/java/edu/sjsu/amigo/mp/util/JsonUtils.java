package edu.sjsu.amigo.mp.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods to convert between JSON and Java objects.
 *
 * @author rwatsh on 2/26/17.
 */
public class JsonUtils {
    /**
     * Converts object to JSON string representation.
     * It could even be a list of objects which will be converted to JSON array on objects.
     *
     * @param object
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> String convertObjectToJson(T object) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(object);
    }

    /**
     * Convert JSON string to object.
     *
     * @param jsonStr
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T convertJsonToObject(String jsonStr, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //jsonStr = removeIdField(jsonStr);
        return mapper.readValue(jsonStr, clazz);
    }

    /**
     * Converts a JSON array of objects to List of Java Object types.
     * For example, convert a JSON array of students to List<Student>.
     *
     * @param jsonArrayStr
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> List<T> convertJsonArrayToList(String jsonArrayStr, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //jsonArrayStr = removeIdField(jsonArrayStr);
        return mapper.readValue(jsonArrayStr,
                TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
    }

    public static String convertListToJson(List<?> list) throws IOException {
        if (list == null) {
            // return empty list
            list = new ArrayList<Object>();
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(out, list);

        final byte[] data = out.toByteArray();
        return new String(data);
    }
}
