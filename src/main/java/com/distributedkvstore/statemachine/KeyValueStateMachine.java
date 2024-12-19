package com.distributedkvstore.statemachine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.distributedkvstore.data.LogEntry;
import com.distributedkvstore.data.StoreType;
import com.distributedkvstore.helper.ListOfStringObject;
import com.distributedkvstore.kvstore.KeyValueStore;
import com.distributedkvstore.kvstore.KeyValueStoreManager;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;
import org.apache.ratis.io.MD5Hash;
import org.apache.ratis.protocol.Message;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.protocol.TermIndex;
import org.apache.ratis.server.storage.FileInfo;
import org.apache.ratis.server.storage.RaftStorage;
import org.apache.ratis.statemachine.StateMachineStorage;
import org.apache.ratis.statemachine.TransactionContext;
import org.apache.ratis.statemachine.impl.BaseStateMachine;
import org.apache.ratis.statemachine.impl.SimpleStateMachineStorage;
import org.apache.ratis.statemachine.impl.SingleFileSnapshotInfo;
import org.apache.ratis.util.FileUtils;
import org.apache.ratis.util.MD5FileUtil;

@Slf4j
public class KeyValueStateMachine extends BaseStateMachine {

    private final KeyValueStoreManager keyValueStoreManager;

    private final SimpleStateMachineStorage storage = new SimpleStateMachineStorage();

    public KeyValueStateMachine(final KeyValueStoreManager keyValueStoreManager) {
        this.keyValueStoreManager = KeyValueStoreManager.getInstance();
    }

    @Override
    public void initialize(RaftServer server,
                           RaftGroupId groupId,
                           RaftStorage raftStorage) throws IOException {
        // Method to initialize data restoration from the data snapshot.
        super.initialize(server, groupId, raftStorage);
        storage.init(raftStorage);
        reinitialize();
    }

    @Override
    public StateMachineStorage getStateMachineStorage() {
        return storage;
    }

    // Method that gets invoked for write operations.
    @Override
    public CompletableFuture<Message> applyTransaction(final TransactionContext transaction) {
        final CompletableFuture<Message> future = new CompletableFuture<>();
        System.out.println("Got a new message!");
        log.info("Got a new message!");

        try {
            // Deserialize the JSON log
            String commandBytes = transaction.getStateMachineLogEntry().getLogData().toStringUtf8();
            final LogEntry logEntry = LogEntry.deserializer(commandBytes);
            log.info("Received log entry: Table - {}, Operation Type - {}, Key - {}",
                     logEntry.getStoreType(), logEntry.getOperation(), logEntry.getKey());

            // Handle the operation based on the store type and operation
            final StoreType storeType = logEntry.getStoreType();
            switch (storeType) {
                case USER_INFO:
                    handleOperation(storeType,
                                    keyValueStoreManager.getInstance().getUserInfo(),
                                    logEntry);
                    break;
                case USER_TABLE_INFO:
                    handleOperation(storeType,
                                    keyValueStoreManager.getInstance().getUserTableInfo(),
                                    logEntry);
                    break;
                case GLOBAL_STORE:
                    handleOperation(storeType,
                                    keyValueStoreManager.getInstance().getGlobalKeyValueStore(),
                                    logEntry);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported store type: " + logEntry.getStoreType());
            }

            // Update the last applied term index
            updateLastAppliedTermIndex(transaction.getLogEntry().getTerm(),
                                       transaction.getLogEntry().getIndex());
            future.complete(Message.valueOf("SUCCESS"));
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    // Method that gets invoked for read requests.
    @Override
    public CompletableFuture<Message> query(Message request) {
        final String command = request.getContent().toString(Charset.defaultCharset());
        String value;
        try {
            LogEntry deserializedLog = LogEntry.deserializer(command);
            System.out.println("Data read request received.");
            log.info("Data read request received: Table - {}, Key - {}",
                     deserializedLog.getStoreType(), deserializedLog.getKey());
            System.out.println(deserializedLog.getKey());

            if (deserializedLog.getStoreType() == StoreType.USER_INFO){
                value = (String) keyValueStoreManager.getInstance().getUserInfo().get(deserializedLog.getKey());
                System.out.println(value);
                System.out.println("Entered the if condition");
            } else if (deserializedLog.getStoreType() == StoreType.USER_TABLE_INFO){
                value = ListOfStringObject.serialize(
                        (List<String>) keyValueStoreManager.getInstance().getUserTableInfo().get(deserializedLog.getKey()));
            } else{
                if (deserializedLog.getValue().equals("LIST_KEYS")){
                    // Code to handle the listing of all keys that start with the given prefix
                    String queryKey = deserializedLog.getKey();
                    List<String> allKeys =
                            keyValueStoreManager.getInstance().getGlobalKeyValueStore().getListOfKeys(queryKey);
                    value = ListOfStringObject.serialize(allKeys);
                } else {
                    value = (String) keyValueStoreManager.getInstance(
                    ).getGlobalKeyValueStore().get(deserializedLog.getKey());
                }
            }
            if (value == null){
                value = "";
            }
            return CompletableFuture.completedFuture(Message.valueOf(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to handle the write operation requests for all the data types.
    private <T> void handleOperation(final StoreType operation,
                                     final KeyValueStore<T> store,
                                     LogEntry logEntry) {
        switch (logEntry.getOperation()) {
            case POST:
                store.put(logEntry.getKey(), (T) logEntry.getValue());
                break;
            case PUT:
                if (operation == StoreType.USER_TABLE_INFO) {
                    // Handle appending to the list for userTableInfo
                    List<String> existingList = (List<String>) store.get(logEntry.getKey());
                    if (existingList == null) {
                        existingList = new ArrayList<>();
                    }
                    existingList.add((String) logEntry.getValue());
                    store.put(logEntry.getKey(), (T) existingList);
                } else {
                    store.put(logEntry.getKey(), (T) logEntry.getValue());
                }
                break;
            case DELETE:
                if (operation == StoreType.USER_TABLE_INFO) {
                    List<String> existingList = (List<String>) store.get(logEntry.getKey());
                    if (existingList != null) {
                        existingList.remove((String) logEntry.getValue());
                        if (existingList.isEmpty()) {
                            // Optionally remove the key if the list becomes empty
                            store.delete(logEntry.getKey());
                        } else {
                            store.put(logEntry.getKey(), (T) existingList);
                        }
                    }
                    String keyPrefix = logEntry.getKey() + "_" + logEntry.getValue() + "_";
                    List<String> allKeys =
                            keyValueStoreManager.getInstance().getGlobalKeyValueStore().getListOfKeys(
                                    keyPrefix);
                    for (String key : allKeys) {
                        keyValueStoreManager.getInstance().getGlobalKeyValueStore().delete(keyPrefix + key);
                    }
                } else {
                    store.delete(logEntry.getKey());
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported operation: " +
                                                   logEntry.getOperation());
        }
    }


    // Method to Capture the current state of the data store and save it in a file.
    @Override
    public long takeSnapshot() {
        final TermIndex termIndex = getLastAppliedTermIndex();
        final long index = termIndex.getIndex();
        final long term = termIndex.getTerm();

        final File snapshotFile = storage.getSnapshotFile(term, index);

        // Serialize the data and store the data in a file.
        try (final ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
                Files.newOutputStream(snapshotFile.toPath())))) {

            String data = keyValueStoreManager.serializer();
            out.writeUTF(data);
            out.writeLong(term);
            out.writeLong(index);

        } catch (final IOException ioe) {
            log.warn("Failed to write snapshot file \"" + snapshotFile
                    + "\", last applied index=" + index, ioe);
            return -1;
        }

        // Compute checksum and update storage
        final MD5Hash md5 = MD5FileUtil.computeAndSaveMd5ForFile(snapshotFile);
        final FileInfo info = new FileInfo(snapshotFile.toPath(), md5);
        storage.updateLatestSnapshot(new SingleFileSnapshotInfo(info, TermIndex.valueOf(term, index)));

        log.info("Snapshot taken successfully for term " + term + ", index " + index);
        return index;
    }

    // Method to reinitialize data restoration from the data snapshot.
    @Override
    public void reinitialize() throws IOException {
        load(storage.loadLatestSnapshot());
    }

    void reset() {
        keyValueStoreManager.clear();
        setLastAppliedTermIndex(null);
        log.info("Resetting state machine completed.");
    }

    // Method to load the data and reinstate the data to the datastore.
    private void load(final SingleFileSnapshotInfo snapshot) throws IOException {
        if (snapshot == null) {
            log.warn("No snapshot provided to load.");
            return;
        }

        // Check if the snapshot file exists
        final Path snapshotPath = snapshot.getFile().getPath();
        if (!Files.exists(snapshotPath)) {
            log.warn("The snapshot file {} does not exist for snapshot {}", snapshotPath, snapshot);
            return;
        }

        // Verify MD5 checksum
        final MD5Hash md5 = snapshot.getFile().getFileDigest();
        if (md5 != null) {
            MD5FileUtil.verifySavedMD5(snapshotPath.toFile(), md5);
        }

        final TermIndex last = storage.getTermIndexFromSnapshotFile(snapshotPath.toFile());

        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
                FileUtils.newInputStream(snapshotPath.toFile())))) {

            // Read serialized data
            String serializedData = in.readUTF();
            reset();
            setLastAppliedTermIndex(last);
            keyValueStoreManager.deserializer(serializedData);
        }
        updateLastAppliedTermIndex(last);
        log.info("State loaded from snapshot: term={}, index={}}", last.getTerm(), last.getIndex());
    }

}
