package com.distributedkvstore.data;

import org.codehaus.jackson.annotate.JsonCreator;


public enum StoreType {

    USER_INFO("USER_INFO"),
    USER_TABLE_INFO("USER_TABLE_INFO"),
    GLOBAL_STORE("GLOBAL_STORE");

    private final String value;

    StoreType(String value) {
        this.value = value;
    }

    // Method to get the Value
    public String getValue() {
        return value;
    }

    // Method that returns the string value for the object
    @Override
    public String toString() {
        return value;
    }

    // Method to return corresponding enum object for the given string.
    @JsonCreator
    public static StoreType fromString(String storeType) {
        switch (storeType.toLowerCase()) {
            case "USER_INFO":
                return USER_INFO;
            case "USER_TABLE_INFO":
                return USER_TABLE_INFO;
            case "GLOBAL_STORE":
                return GLOBAL_STORE;
            default:
                throw new IllegalArgumentException("Unknown store type: " + storeType);
        }
    }
}