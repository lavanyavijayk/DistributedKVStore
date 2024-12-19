package com.distributedkvstore.helper;

import java.util.List;

import com.distributedkvstore.client.KeyValueClient;
import com.distributedkvstore.data.LogEntry;
import com.distributedkvstore.data.Operation;
import com.distributedkvstore.data.StoreType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftClientReply;


public class UserTableCRUDoperations {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static StoreType store = StoreType.USER_TABLE_INFO;
    static KeyValueClient keyValueClient = KeyValueClient.getInstance();

    // Method to get the value for the key
    private static String getKey() {
        return keyValueClient.getInstance().getUserSession().getUsername() + "_"
                + keyValueClient.getInstance().getUserSession().getUserId();
    }

    // Method to do post operation
    public static void postOperation(String value) throws JsonProcessingException {
        String key = getKey();
        LogEntry newLog = new LogEntry(store, Operation.PUT, key, value);
        keyValueClient.writeOperation(Message.valueOf(LogEntry.serializer(newLog)));
        return ;
    }

    // Method to do get operation
    public static List<String> getOperation() throws JsonProcessingException {
        String key = getKey();
        LogEntry newLog = new LogEntry(store, Operation.GET, key, "");
        RaftClientReply response= KeyValueClient.getInstance().readOperation(
                Message.valueOf(LogEntry.serializer(newLog)));
        return ListOfStringObject.deserialize(response.getMessage().getContent().toStringUtf8());
    }

    // Method to check if a table exists.
    public static Boolean tableExists(String tableName) throws JsonProcessingException {
        List<String> tableNamesLists = getOperation();
        if (tableNamesLists==null || tableNamesLists.size()==0) {
            return false;
        }
        return tableNamesLists.contains(tableName);
    }

    // Method to do put operation
    public static void putOperation(String value) throws JsonProcessingException {
        String key = getKey();
        LogEntry newLog = new LogEntry(store, Operation.PUT, key, value);
        keyValueClient.writeOperation(Message.valueOf(LogEntry.serializer(newLog)));
        return ;
    }

    // Method to do delete operation
    public static void deleteOperation(String value) throws JsonProcessingException {
        String key = getKey();
        LogEntry newLog = new LogEntry(store, Operation.DELETE, key, value);
        keyValueClient.writeOperation(Message.valueOf(LogEntry.serializer(newLog)));
        return ;

    }

}
