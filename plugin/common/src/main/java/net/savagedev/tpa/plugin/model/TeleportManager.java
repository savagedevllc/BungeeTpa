package net.savagedev.tpa.plugin.model;

import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport.TeleportTime;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport.TeleportType;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;
import net.savagedev.tpa.plugin.model.request.TeleportRequest.Direction;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public final class TeleportManager {
    private final Map<UUID, Deque<TeleportRequest>> requestMap = new HashMap<>();

    private final BungeeTpPlugin plugin;

    public TeleportManager(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    // TODO: Find a way to call this on shutdown before the disconnect event, or cancel the disconnect event or something.
    public void shutdown() {
        // Make sure all players are refunded in the event of a server shutdown.
        int count = 0;
        for (TeleportRequest request : this.aggregateRequests()) {
            request.getSender().deposit(Setting.TELEPORT_COST.asFloat()).join();
            count++;
        }
        this.plugin.getPlatform().getLogger().info("Refunded " + count + " player(s).");
        this.requestMap.clear();
    }

    public CompletableFuture<TeleportRequestResponse> acceptRequest(TeleportRequest request) {
        if (request == null) {
            return CompletableFuture.completedFuture(TeleportRequestResponse.SUCCESS);
        }
        if (request.getDirection() == Direction.TO_SENDER) {
            if (request.getReceiver().canBypassDelay()) {
                return this.teleportAsync(request.getReceiver(), request.getSender());
            } else {
                return this.teleportDelayedAsync(request.getReceiver(), request.getSender());
            }
        } else {
            if (request.getSender().canBypassDelay()) {
                return this.teleportAsync(request.getSender(), request.getReceiver());
            } else {
                return this.teleportDelayedAsync(request.getSender(), request.getReceiver());
            }
        }
    }

    private CompletableFuture<TeleportRequestResponse> teleportDelayedAsync(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        final CompletableFuture<TeleportRequestResponse> future = new CompletableFuture<>();
        this.plugin.getPlatform().scheduleTaskDelayed(() ->
                        future.complete(this.teleport(player, other)),
                Setting.DELAY.asLong() * 1000L);
        return future;
    }

    public CompletableFuture<TeleportRequestResponse> teleportAsync(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        return CompletableFuture.supplyAsync(() -> this.teleport(player, other));
    }

    public CompletableFuture<TeleportRequestResponse> teleportAsync(ProxyPlayer<?, ?> player, String server, String world, float x, float y, float z) {
        return CompletableFuture.supplyAsync(() -> this.teleport(player, server, world, x, y, z));
    }

    private TeleportRequestResponse teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        TeleportRequestResponse success = TeleportRequestResponse.SUCCESS;
        final Server<?> targetServer = other.getCurrentServer();

        final MessageRequestTeleport requestMessage = new MessageRequestTeleport(player.getUniqueId(), TeleportType.PLAYER_LOCATION, other.getUniqueId());
        if (player.getCurrentServer().equals(targetServer)) {
            requestMessage.setTeleportTime(TeleportTime.INSTANT);
        } else {
            requestMessage.setTeleportTime(TeleportTime.ON_JOIN);
            success = player.connect(targetServer) ? TeleportRequestResponse.SUCCESS : TeleportRequestResponse.NOT_WHITELISTED;
        }

        this.plugin.getPlatform().getMessenger().sendData(targetServer, requestMessage);
        return success;
    }

    private TeleportRequestResponse teleport(ProxyPlayer<?, ?> player, String server, String world, float x, float y, float z) {
        TeleportRequestResponse success = TeleportRequestResponse.SUCCESS;
        final Optional<Server<?>> optionalTargetServer = this.plugin.getServerManager().getOrLoad(server);

        if (!optionalTargetServer.isPresent()) {
            return TeleportRequestResponse.INVALID_SERVER;
        }

        final MessageRequestTeleport requestMessage = new MessageRequestTeleport(player.getUniqueId(), TeleportType.COORDINATES, world, x, y, z);
        if (player.getCurrentServer().equals(optionalTargetServer.get())) {
            requestMessage.setTeleportTime(TeleportTime.INSTANT);
        } else {
            requestMessage.setTeleportTime(TeleportTime.ON_JOIN);
            success = player.connect(optionalTargetServer.get()) ? TeleportRequestResponse.SUCCESS : TeleportRequestResponse.NOT_WHITELISTED;
        }

        this.plugin.getPlatform().getMessenger().sendData(optionalTargetServer.get(), requestMessage);
        return success;
    }

    public TeleportRequestResponse pushRequest(TeleportRequest request) {
        this.requestMap.computeIfAbsent(request.getReceiver().getUniqueId(), k -> new ConcurrentLinkedDeque<>())
                .push(request);
        return TeleportRequestResponse.SUCCESS;
    }

    public Optional<TeleportRequest> popMostRecentRequest(ProxyPlayer<?, ?> player) {
        final Deque<TeleportRequest> requests = this.requestMap.get(player.getUniqueId());
        if (requests == null || requests.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(requests.pop());
    }

    public Optional<TeleportRequest> popMostRecentSentRequest(ProxyPlayer<?, ?> sender) {
        final Set<TeleportRequest> requests = this.aggregateRequests()
                .stream()
                .filter(req -> req.getSender().equals(sender))
                .collect(Collectors.toSet());

        if (requests.isEmpty()) {
            return Optional.empty();
        }

        TeleportRequest request = null;
        for (TeleportRequest currentRequest : requests) {
            if (request == null || request.getTimeSent() < currentRequest.getTimeSent()) {
                request = currentRequest;
            }
        }

        if (request != null) {
            this.requestMap.get(request.getReceiver().getUniqueId()).remove(request);
        }

        return Optional.ofNullable(request);
    }

    public Optional<TeleportRequest> popRequestBySender(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> sender) {
        final Deque<TeleportRequest> requests = this.requestMap.get(player.getUniqueId());
        if (requests == null || requests.isEmpty()) {
            return Optional.empty();
        }

        TeleportRequest request = null;
        for (TeleportRequest teleportRequest : requests) {
            if (teleportRequest.getSender().equals(sender)) {
                request = teleportRequest;
                break;
            }
        }

        if (request != null) {
            requests.remove(request);
        }

        return Optional.ofNullable(request);
    }

    public Deque<TeleportRequest> getRequestStack(ProxyPlayer<?, ?> player) {
        return this.requestMap.getOrDefault(player.getUniqueId(), new ConcurrentLinkedDeque<>());
    }

    public Collection<TeleportRequest> deleteRequestStack(ProxyPlayer<?, ?> player) {
        final Deque<TeleportRequest> requestStack = this.requestMap.remove(player.getUniqueId());
        return requestStack == null ? Collections.emptySet() : requestStack;
    }

    public void clearRequestStack(ProxyPlayer<?, ?> player) {
        final Deque<TeleportRequest> requestStack = this.requestMap.get(player.getUniqueId());
        if (requestStack == null) {
            return;
        }
        requestStack.clear();
    }

    public Collection<TeleportRequest> aggregateRequests() {
        final Set<TeleportRequest> aggregatedRequests = new HashSet<>();
        for (Deque<TeleportRequest> requestStack : this.requestMap.values()) {
            aggregatedRequests.addAll(requestStack);
        }
        return aggregatedRequests;
    }

    public enum TeleportRequestResponse {
        SUCCESS,
        NOT_WHITELISTED,
        CANT_AFFORD,
        INVALID_SERVER;

        public boolean isSuccess() {
            return this == SUCCESS;
        }
    }
}
