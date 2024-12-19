package com.distributedkvstore.helper;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ListOfStringObject {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Serialize a list of strings into a JSON string
    public static String serialize(List<String> strings) throws JsonProcessingException {
        return objectMapper.writeValueAsString(strings);
    }

    // Deserialize a JSON string back into a list of strings
    public static List<String> deserialize(String serializedString) throws JsonProcessingException {
        return objectMapper.readValue(serializedString, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        }

}
