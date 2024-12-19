package com.distributedkvstore.data;

import org.codehaus.jackson.annotate.JsonCreator;

public enum Operation {
    POST,
    PUT,
    GET,
    DELETE;

    // Method to return corresponding enum object for the given string.
    @JsonCreator
    public static Operation fromString(String operation) {
        return switch (operation.toLowerCase()) {
            case "POST" -> POST;
            case "PUT" -> PUT;
            case "DELETE" -> DELETE;
            case "GET" -> GET;
            default -> throw new IllegalArgumentException("Unknown operation: " + operation);
        };
    }
}
