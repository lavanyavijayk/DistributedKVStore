package com.distributedkvstore.kvstore;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonDeserialize(as = KeyValueStoreManager.class)
@JsonSerialize(as = KeyValueStoreManager.class)
public class KeyValueStoreManager {
    private static KeyValueStoreManager keyValueStoreManagerInstance = null;

    @JsonProperty
    private final KeyValueStore<String> userInfo;
    @JsonProperty
    private final KeyValueStore<List<String>> userTableInfo;
    @JsonProperty
    private final KeyValueStore<String> globalKeyValueStore;

    // Private constructor to prevent instantiation from outside
    private KeyValueStoreManager() {
        userInfo = new KeyValueStore<String>();
        userTableInfo = new KeyValueStore<List<String>>();
        globalKeyValueStore = new KeyValueStore<String>();
    }

    private KeyValueStoreManager(KeyValueStore<String> userInfo,
                                KeyValueStore<List<String>> userTableInfo,
                                KeyValueStore<String> globalKeyValueStore) {
        this.userInfo = userInfo;
        this.userTableInfo = userTableInfo;
        this.globalKeyValueStore = globalKeyValueStore;
    }

    //Method to set the keyValueStoreManagerInstance to null
    public void clear(){
        keyValueStoreManagerInstance = null;
    }

    // Public method to provide access to the single instance
    public static KeyValueStoreManager getInstance() {
        if (keyValueStoreManagerInstance == null) {
            synchronized (KeyValueStoreManager.class) {
                if (keyValueStoreManagerInstance == null) {
                    keyValueStoreManagerInstance = new KeyValueStoreManager();
                }
            }
        }
        return keyValueStoreManagerInstance;
    }


    // Method to serialize LogEntry object to JSON string
    public static String serializer() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(KeyValueStoreManager.getInstance());
    }

    // Method to deserialize JSON string to LogEntry object
    public static void deserializer(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        keyValueStoreManagerInstance = objectMapper.readValue(json, KeyValueStoreManager.class);
    }


    // Getter methods for the User INFO KeyValueStore instances
    public KeyValueStore getUserInfo() {
        return userInfo;
    }

    // Getter methods for the User Table INFO KeyValueStore instances
    public KeyValueStore getUserTableInfo() {
        return userTableInfo;
    }

    // Getter methods for the Global Key Value KeyValueStore instances
    public KeyValueStore getGlobalKeyValueStore() {
        return globalKeyValueStore;
    }
}
