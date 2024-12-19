package com.distributedkvstore.kvstore;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;


public class KeyValueStore<T> {
    private final Map<String, T> store;
    // Constructor initializes the HashMap
    public KeyValueStore() {
        this.store = new HashMap<>();
    }

    public KeyValueStore(final Map<String, T> store) {
        this.store = store;
    }

    // Method to get the store.
    @JsonAnyGetter
    public Map<String, T> getStore() {
        return store;
    }

    // Method to create a key value pair
    @JsonAnySetter
    public void set(String key, T value) {
        store.put(key, value);
    }

    // Method to update a key value pair
    public void put(final String key, final T value) {
        store.put(key, value);
    }

    // Method to get value for the given key
    public Object get(final String key) {
        return store.get(key);
    }

    // Method to print the key value pairs
    public void printStore() {
        for (Map.Entry<String, T> entry : store.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    // Method to delete a key value pairs
    public void delete(String key) {
        store.remove(key);
        return;
    }

    // Method to get all the key value pairs
    public Set<Map.Entry<String, T>> getAll() {
        return store.entrySet().stream().collect(Collectors.toSet());
    }

    // Method to get all the keys with given prefix
    public List<String> getListOfKeys(String queryKey){
        List<String> allKeys = new ArrayList<>();
        final Set<Map.Entry<String, T>> entrySet = getAll();
        for (Map.Entry<String, T> entry  : entrySet) {
            // Check if the key starts with the given queryKey
            String key = entry.getKey();
            if (key.startsWith(queryKey)) {
                // Remove the queryKey prefix and add the remaining part to the result
                allKeys.add(key.substring(queryKey.length()));
            }
        }
        return allKeys;
    }
}
