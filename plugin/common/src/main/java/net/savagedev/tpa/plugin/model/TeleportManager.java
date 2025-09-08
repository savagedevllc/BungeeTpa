package net.savagedev.tpa.plugin.model;

import net.savagedev.tpa.common.messaging.messages.AbstractMessageRequestTeleport.Type;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleportCoords;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleportPlayer;
import net.savagedev.tpa.plugin.BungeeTpPlatform;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;
import net.savagedev.tpa.plugin.model.request.TeleportRequest.Direction;
import net.savagedev.tpa.plugin.model.server.Server;
import net.savagedev.tpa.plugin.model.server.ServerLocation;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public final class TeleportManager {
    private final Map<UUID, Deque<TeleportRequest>> requestMap = new ConcurrentHashMap<>();
    private final Map<UUID, MessageRequestTeleportCoords> locationTeleportCache = new HashMap<>();

    private final BungeeTpPlatform platform;

    public TeleportManager(BungeeTpPlatform platform) {
        this.platform = platform;
    }

    public void shutdown() {
        int count = 0;
        for (TeleportRequest request : this.aggregateRequests()) {
            // TODO: Replace this with a write to a local cache (or the database) to store all pending requests &
            //       reload them all upon restarting & send players a message about their pending request.
            request.getSender().deposit(Setting.TELEPORT_COST.asFloat()).join();
            count++;
        }

        // this.platform.getLogger().info("Saved " + count + " pending request" + (count == 1 ? "." : "s."));
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
        this.platform.scheduleTaskDelayed(() ->
                        future.complete(this.teleport(player, other)),
                Setting.DELAY.asLong() * 1000L);
        return future;
    }

    public CompletableFuture<TeleportRequestResponse> teleportAsync(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        return CompletableFuture.supplyAsync(() -> this.teleport(player, other));
    }

    private TeleportRequestResponse teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        TeleportRequestResponse success = TeleportRequestResponse.SUCCESS;
        final Server<?> targetServer = other.getCurrentServer();

        final MessageRequestTeleportPlayer requestMessage = new MessageRequestTeleportPlayer(player.getUniqueId(), other.getUniqueId());
        if (player.getCurrentServer().equals(targetServer)) {
            requestMessage.setType(Type.INSTANT);
        } else {
            requestMessage.setType(Type.ON_JOIN);
            success = player.connect(targetServer) ? TeleportRequestResponse.SUCCESS : TeleportRequestResponse.NOT_WHITELISTED;
        }

        this.platform.getMessenger().sendData(targetServer, requestMessage);
        return success;
    }

    public TeleportRequestResponse teleport(ProxyPlayer<?, ?> player, ServerLocation location) {
        TeleportRequestResponse success = TeleportRequestResponse.SUCCESS;
        final Server<?> targetServer = location.getServer();

        final MessageRequestTeleportCoords requestMessage = new MessageRequestTeleportCoords(player.getUniqueId(),
                location.getWorldName(), location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
        requestMessage.setType(Type.INSTANT);
        this.locationTeleportCache.put(player.getUniqueId(), requestMessage);

        if (player.getCurrentServer().equals(targetServer)) {
            this.completeTeleportToCoords(player);
        } else {
            success = player.connect(targetServer) ? TeleportRequestResponse.SUCCESS : TeleportRequestResponse.NOT_WHITELISTED;
        }

        return success;
    }

    public void completeTeleportToCoords(ProxyPlayer<?, ?> player) {
        final MessageRequestTeleportCoords message = this.locationTeleportCache.remove(player.getUniqueId());

        if (message == null) {
            return;
        }

        this.platform.getMessenger().sendData(player.getCurrentServer(), message);
    }

    public TeleportRequestResponse pushRequest(TeleportRequest request) {
        boolean canAccess;

        if (request.getDirection() == Direction.TO_RECEIVER) {
            canAccess = request.getReceiver().getCurrentServer().isAccessibleTo(request.getSender());
        } else {
            canAccess = request.getSender().getCurrentServer().isAccessibleTo(request.getReceiver());
        }

        this.requestMap.computeIfAbsent(request.getReceiver().getUniqueId(), k -> new ConcurrentLinkedDeque<>())
                .push(request);
        return canAccess ? TeleportRequestResponse.SUCCESS : TeleportRequestResponse.NOT_WHITELISTED;
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
        CANT_AFFORD;

        public boolean isSuccess() {
            return this == SUCCESS;
        }
    }
}
