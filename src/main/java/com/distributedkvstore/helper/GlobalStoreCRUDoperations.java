package com.distributedkvstore.helper;

import java.util.List;

import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftClientReply;

import com.distributedkvstore.client.KeyValueClient;
import com.distributedkvstore.data.LogEntry;
import com.distributedkvstore.data.Operation;
import com.distributedkvstore.data.StoreType;
import com.fasterxml.jackson.core.JsonProcessingException;

public class GlobalStoreCRUDoperations {
    static StoreType store = StoreType.GLOBAL_STORE;
    static KeyValueClient keyValueClient = KeyValueClient.getInstance();

    // Method to get a key prefix
    private static String getKeyPrefix() {
        return keyValueClient.getInstance().getUserSession().getUsername() + "_"
                + keyValueClient.getInstance().getUserSession().getUserId();
    }

    // Method to post a key to store
    public static void postOperation(String key, String value) throws JsonProcessingException {
        key = getKeyPrefix() + "_" + key;
        LogEntry newLog = new LogEntry(store, Operation.PUT, key, value);
        keyValueClient.writeOperation(Message.valueOf(LogEntry.serializer(newLog)));
        return ;
    }

    // Method to get a key from store
    public static String getOperation(String key) throws JsonProcessingException {
        key = getKeyPrefix() + "_" + key;
        LogEntry newLog = new LogEntry(store, Operation.GET, key, "");
        RaftClientReply response= KeyValueClient.getInstance().readOperation(
                Message.valueOf(LogEntry.serializer(newLog)));
        return response.getMessage().getContent().toStringUtf8();
    }

    // Method to put a key to the store
    public static void putOperation(String key, String value) throws JsonProcessingException {
        key = getKeyPrefix() + "_" + key;
        LogEntry newLog = new LogEntry(store, Operation.PUT, key, value);
        keyValueClient.writeOperation(Message.valueOf(LogEntry.serializer(newLog)));
        return ;
    }

    // Method to delete a key from store
    public static void deleteOperation(String key) throws JsonProcessingException {
        key = getKeyPrefix() + "_" + key;
        LogEntry newLog = new LogEntry(store, Operation.DELETE, key, "");
        keyValueClient.writeOperation(Message.valueOf(LogEntry.serializer(newLog)));
        return ;
    }

    // Method to get the list of all keys with the given prefix
    public static List<String> listKeysOperation(String keyPrefix) throws JsonProcessingException {
        keyPrefix = getKeyPrefix() + "_" + keyPrefix;
        LogEntry newLog = new LogEntry(StoreType.GLOBAL_STORE,
                Operation.GET,
                keyPrefix,
                "LIST_KEYS");
        RaftClientReply response = KeyValueClient.getInstance().readOperation(
                Message.valueOf(LogEntry.serializer(newLog)));
        return ListOfStringObject.deserialize(response.getMessage().getContent().toStringUtf8());
    }
}
