package com.distributedkvstore.client;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.Setter;

import com.beust.jcommander.Parameter;
import com.distributedkvstore.user.User;

import org.apache.ratis.client.RaftClient;
import org.apache.ratis.conf.Parameters;
import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcFactory;
import org.apache.ratis.protocol.*;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;


public class KeyValueClient implements AutoCloseable{

    // Class for initializing running and maintaining a raft client server.
    private static KeyValueClient keyValueClientInstance = null;
    @Getter
    @Setter
    private static User userSession = null;
    @Getter
    private RaftClient raftClient;
    final RaftProperties properties = new RaftProperties();
    @Getter
    @Parameter(names = {"--raftGroup", "-g"}, description = "Raft group identifier")
    private String raftGroupId = "demoRaftGroup123";

    @Parameter(names = {"--peers", "-r"},
                       description = "Raft peers (format: name:host:port)",
                       required = true)
    private static String peers;

    // Method to close the raft client.
    @Override
    public void close() throws IOException {
        if (raftClient != null) {
            raftClient.close();
        }
    }

    // Method to get the key value client instance
    public static KeyValueClient getInstance() {
        if (keyValueClientInstance == null) {
            synchronized (KeyValueClient.class) {
                if (keyValueClientInstance == null) {
                    keyValueClientInstance = new KeyValueClient();
                }
            }
        }
        return keyValueClientInstance;
    }

    // Method to get the peers.
    public RaftPeer getPeer(RaftPeerId raftPeerId) {
        Objects.requireNonNull(raftPeerId, "raftPeerId == null");
        for (RaftPeer p : getPeers()) {
            if (raftPeerId.equals(p.getId())) {
                return p;
            }
        }
        throw new IllegalArgumentException("Raft peer id " + raftPeerId
                + " is not part of the raft group definitions: " + peers);
    }

    // Method to parse the peers configuration.
    public static RaftPeer[] parsePeers(String peers) {
        System.out.println(Stream.of(peers.split(",")));
        return Stream.of(peers.split(","))
                .map(address -> {
                    String[] addressParts = address.split(":");
                    System.out.println(addressParts);
                    if (addressParts.length < 3) {
                        throw new IllegalArgumentException(
                                "Raft peer " + address + " is not a legitimate format. "
                                        + "(format: name:host:port)");
                    }
                    return RaftPeer.newBuilder()
                            .setId(addressParts[0])
                            .setAddress(addressParts[1] + ":" + addressParts[2])
                            .build();
                })
                .toArray(RaftPeer[]::new);
    }

    public RaftPeer[] getPeers() {
        return parsePeers(peers);
    }

    // Method to send a write operation
    public RaftClientReply writeOperation(Message messageStr){
        try {
            final RaftClientReply reply = raftClient.io().send(messageStr);
            return reply;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to send a read operation
    public RaftClientReply readOperation(Message messageStr){
        try {
            return raftClient.io().sendReadOnly(messageStr);
        } catch (IOException e) {
            System.err.println("Failed read-only request");
            return RaftClientReply.newBuilder().setSuccess(false).build();
        }
    }

    // Method to read data from the followers instead of the leader.
    public RaftClientReply readOperationFromFollowers(Message messageStr){
        try {
            return raftClient.io().sendReadOnlyNonLinearizable(messageStr);
        } catch (IOException e) {
            System.err.println("Failed read-only request");
            return RaftClientReply.newBuilder().setSuccess(false).build();
        }
    }

    // Method to initiate and run the raft client
    protected void run() throws Exception {

        final RaftGroup raftGroup = RaftGroup.valueOf(
                RaftGroupId.valueOf(ByteString.copyFromUtf8(getRaftGroupId())),
                getPeers());

        RaftClient.Builder builder = RaftClient.newBuilder().setProperties(properties);
        builder.setRaftGroup(raftGroup);
        builder.setClientRpc(
                new GrpcFactory(new Parameters()).newRaftClientRpc(ClientId.randomId(), properties));
        raftClient = builder.build();

    }
}
