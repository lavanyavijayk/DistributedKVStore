package com.distributedkvstore.helper;

import com.distributedkvstore.client.KeyValueClient;
import com.distributedkvstore.data.LogEntry;
import com.distributedkvstore.data.Operation;
import com.distributedkvstore.data.StoreType;
import com.distributedkvstore.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftClientReply;


public class UserInfoCRUDoperations {
    static StoreType store = StoreType.USER_INFO;
    static KeyValueClient keyValueClient = KeyValueClient.getInstance();

    // Method to do post operation
    public static void postOperation(String key, String value) throws JsonProcessingException {
        LogEntry newLog = new LogEntry(store, Operation.POST, key, value);
        keyValueClient.writeOperation(Message.valueOf(LogEntry.serializer(newLog)));
        return ;
    }

    // Method to do get operation
    public static User getOperation(String key) throws JsonProcessingException {
        LogEntry newLog = new LogEntry(store, Operation.GET, key, "");
        RaftClientReply response= KeyValueClient.getInstance().readOperation(
                Message.valueOf(LogEntry.serializer(newLog)));
        if (response.getMessage().getContent().toStringUtf8().isEmpty()){
            return null;
        }
        return (User) User.deserialize(response.getMessage().getContent().toStringUtf8());
    }

    // Method to do put operation
    public static void putOperation(String key, String value) throws JsonProcessingException {
        LogEntry newLog = new LogEntry(store, Operation.PUT, key, value);
        keyValueClient.writeOperation(Message.valueOf(LogEntry.serializer(newLog)));
        return ;
    }

    // Method to do delete operation
    public static void deleteOperation(String key, String value) throws JsonProcessingException {
        LogEntry newLog = new LogEntry(store, Operation.DELETE, key, value);
        keyValueClient.writeOperation(Message.valueOf(LogEntry.serializer(newLog)));
        return ;

    }

}
