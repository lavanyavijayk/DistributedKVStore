package com.distributedkvstore.data;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@AllArgsConstructor
public class LogEntry {
    @JsonProperty("storeType")
    private StoreType storeType;

    @JsonProperty("operation")
    private Operation operation;

    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private Object value; // Can hold any type of value, such as String, Map, etc.

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public LogEntry() {}

    // Method to serialize LogEntry object to JSON string
    public static String serializer(LogEntry logEntry) throws JsonProcessingException {
        return objectMapper.writeValueAsString(logEntry);
    }

    // Method to deserialize JSON string to LogEntry object
    public static LogEntry deserializer(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, LogEntry.class);
    }

}

