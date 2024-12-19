package com.distributedkvstore.server;

import lombok.Getter;

import java.io.File;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.distributedkvstore.kvstore.KeyValueStoreManager;
import com.distributedkvstore.statemachine.KeyValueStateMachine;
import com.distributedkvstore.utils.Constants;

import org.apache.ratis.conf.RaftProperties;
import org.apache.ratis.grpc.GrpcConfigKeys;
import org.apache.ratis.protocol.RaftGroup;
import org.apache.ratis.protocol.RaftGroupId;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.protocol.RaftPeerId;
import org.apache.ratis.server.DivisionInfo;
import org.apache.ratis.server.RaftServer;
import org.apache.ratis.server.RaftServerConfigKeys;
import org.apache.ratis.server.storage.RaftStorage;
import org.apache.ratis.statemachine.StateMachine;
import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
import org.apache.ratis.util.LifeCycle;
import org.apache.ratis.util.NetUtils;


@Parameters(commandDescription = "Start a KV server")
@Slf4j
public class Server {

    @Parameter(names = {"--id", "-i"}, description = "Raft id of this server", required = true)
    private String id;

    @Parameter(names = {"--storage", "-s"}, description = "Storage directory", required = true)
    private File storageDir;

    @Getter
    @Parameter(names = {"--raftGroup", "-g"}, description = "Raft group identifier")
    private String raftGroupId = "demoRaftGroup123";

    @Parameter(names = {"--peers", "-r"},
               description = "Raft peers (format: name:host:port)", required = true)
    private String peers;

    // Method to initialize and  run the raft server.
    public void run() throws Exception {
        RaftPeerId peerId = RaftPeerId.valueOf(id);
        RaftProperties properties = new RaftProperties();

        RaftPeer peer = getPeer(peerId);
        final int port = NetUtils.createSocketAddr(peer.getAddress()).getPort();
        GrpcConfigKeys.Server.setPort(properties, port);

        Optional.ofNullable(peer.getClientAddress()).ifPresent(address ->
                GrpcConfigKeys.Client.setPort(properties, NetUtils.createSocketAddr(address).getPort()));
        Optional.ofNullable(peer.getAdminAddress()).ifPresent(address ->
                GrpcConfigKeys.Admin.setPort(properties, NetUtils.createSocketAddr(address).getPort()));

        //Setting up raft server configurations
        RaftServerConfigKeys.setStorageDir(properties, Collections.singletonList(storageDir));
        RaftServerConfigKeys.Snapshot.setAutoTriggerEnabled(properties, true);
        RaftServerConfigKeys.Snapshot.setAutoTriggerThreshold(properties, Constants.SNAPSHOT_FILE_COUNT);
        RaftServerConfigKeys.Snapshot.setRetentionFileNum(properties, Constants.SNAPSHOT_FILE_COUNT);

        final StateMachine stateMachine = new KeyValueStateMachine(KeyValueStoreManager.getInstance());

        final RaftGroup raftGroup = RaftGroup.valueOf(RaftGroupId.valueOf(ByteString.copyFromUtf8(raftGroupId)),
                getPeers());

        // Building the raft server
        final RaftServer raftServer = RaftServer.newBuilder()
                .setServerId(peerId)
                .setStateMachine(stateMachine)
                .setProperties(properties)
                .setGroup(raftGroup)
                .setOption(RaftStorage.StartupOption.RECOVER)
                .build();

        // Starting raft server
        raftServer.start();
        log.info("Initialized Raft server");
        while (raftServer.getLifeCycleState() != LifeCycle.State.CLOSED) {
            TimeUnit.SECONDS.sleep(15);

            for (RaftGroup raftG  : raftServer.getGroups()) {
                RaftServer.Division division = raftServer.getDivision(raftG.getGroupId());
                DivisionInfo info = division.getInfo();
                RaftPeerId leaderId = info.getLeaderId();
                System.out.println("Current Leader: " + (leaderId != null ? leaderId : "No leader elected"));

                RaftGroup group = division.getGroup();
                for (RaftPeer peeer : group.getPeers()) {
                    RaftPeerId peeerId = peeer.getId();
                    String state = (peeerId.equals(leaderId)) ? "LEADER" : "FOLLOWER";
                    System.out.println("Peer: " + peeerId + ", Address: "
                                       + peeer.getAddress() + ", State: " + state);
                }
            }
        }
        log.info("Server stopped");
    }

    // Method to retrieve peers.
    public RaftPeer getPeer(RaftPeerId raftPeerId) {
        Objects.requireNonNull(raftPeerId, "raftPeerId == null");
        for (RaftPeer p : getPeers()) {
            if (raftPeerId.equals(p.getId())) {
                return p;
            }
        }
        throw new IllegalArgumentException("Raft peer id "
                                            + raftPeerId
                                            + " is not part of the raft group definitions: "
                                            + peers);
    }

    public static RaftPeer[] parsePeers(String peers) {
        return Stream.of(peers.split(","))
                .map(address -> {
                    String[] addressParts = address.split(":");
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

    public static void main(String[] args) {
        //Initializing the raft server.
        Server server = new Server();
        JCommander.newBuilder()
                .addObject(server)
                .build()
                .parse(args);

        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
