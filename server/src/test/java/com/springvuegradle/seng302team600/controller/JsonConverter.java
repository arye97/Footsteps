/**
 * Unit tests in the controller class require a lot of JSON string literals, which are not represented well.
 * This class builds JSON strings from lists of arguments, arrays, and Maps in a way that is easier to read.
 */
package com.springvuegradle.seng302team600.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

class JsonConverter {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Converts argument pairs into a json string.  Every even argument is the key, every odd is the value.
     * Supports arrays as values and nested json strings.  Takes an optional boolean as the 1st argument.
     * If true it returns a json string with formatting.
     * @param args Key value pairs.  Keys should be strings, values should be strings, Maps or String arrays.
     *             1st argument can be a boolean.
     * @return a json string
     */
    static String toJson(Object ... args) {
        String jsonString;
        // This try-catch isn't great.  But can throw it because you can't catch an exception in a final variable initialization
        try {
            // Checks if there is a boolean as the 1st argument
            if ((args[0].getClass().equals(boolean.class) || args[0].getClass().equals(Boolean.class)) && (boolean)args[0]) {
                jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(toMap(args));
            } else {
                jsonString = mapper.writeValueAsString(toMap(args));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            jsonString = "";
        }

        return jsonString;
    }

    /**
     * Converts argument pairs into a Map<Object, Object>.  Every even argument is the key, every odd is the value.
     * This method should be used when writing nested Json.  Supports arrays as values and nested json strings.
     * @param args Key value pairs.  Keys should be strings, values should be strings, Maps or String arrays.
     * @return a Map<Object, Object>
     */
    static Map toMap(Object ... args) {
        Map<Object, Object> jsonMap = new HashMap<>(args.length/2);

        int i = 0;
        if (args[0].getClass().equals(boolean.class) || args[0].getClass().equals(Boolean.class)) i++;

        for(; i < args.length; i+=2) {
            jsonMap.put(args[i], args[i+1]);
        }
        return jsonMap;
    }
}
