package net.savagedev.tpa.plugin.model;

import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport.Type;
import net.savagedev.tpa.plugin.BungeeTpPlatform;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class TeleportManager {
    private final Map<UUID, TeleportRequest> requestMap = new HashMap<>();
    private final Map<UUID, UUID> mostRecentRequests = new HashMap<>();

    private final BungeeTpPlatform platform;

    public TeleportManager(BungeeTpPlatform platform) {
        this.platform = platform;
    }

    public void shutdown() {
        // Make sure all players are refunded in the event of a server shutdown.
        for (TeleportRequest request : this.requestMap.values()) {
            request.getSender().deposit(Setting.TELEPORT_COST.asFloat()).join();
        }
        this.platform.getLogger().info("Refunded " + this.requestMap.size() + " player(s).");

        this.mostRecentRequests.clear();
        this.requestMap.clear();
    }

    public CompletableFuture<TeleportRequestResponse> acceptRequest(TeleportRequest request) {
        if (request == null) {
            return CompletableFuture.completedFuture(TeleportRequestResponse.SUCCESS);
        }
        if (request.getDirection() == TeleportRequest.Direction.TO_SENDER) {
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

        final MessageRequestTeleport requestMessage = new MessageRequestTeleport(player.getUniqueId(), other.getUniqueId());
        if (player.getCurrentServer().equals(targetServer)) {
            requestMessage.setType(Type.INSTANT);
        } else {
            requestMessage.setType(Type.ON_JOIN);
            success = player.connect(targetServer) ? TeleportRequestResponse.SUCCESS : TeleportRequestResponse.NOT_WHITELISTED;
        }

        this.platform.getMessenger().sendData(other.getCurrentServer(), requestMessage);
        return success;
    }

    public TeleportRequest getRequestTo(ProxyPlayer<?, ?> player) {
        return this.requestMap.get(player.getUniqueId());
    }

    public TeleportRequest removeRequestTo(ProxyPlayer<?, ?> player) {
        final TeleportRequest request = this.requestMap.remove(player.getUniqueId());
        this.mostRecentRequests.remove(request.getSender().getUniqueId());
        return request;
    }

    public Set<TeleportRequest> removeAllRequestsBySenderOrReceiver(ProxyPlayer<?, ?> player) {
        final Set<TeleportRequest> requests = new HashSet<>();
        for (TeleportRequest request : this.getAllRequests()) {
            if (request.getSender().getUniqueId().equals(player.getUniqueId()) || request.getReceiver().getUniqueId().equals(player.getUniqueId())) {
                this.mostRecentRequests.remove(request.getSender().getUniqueId());
                this.requestMap.remove(request.getReceiver().getUniqueId());
                requests.add(request);
            }
        }
        return requests;
    }

    public TeleportRequest removeMostRecentRequest(ProxyPlayer<?, ?> sender) {
        final UUID uuid = this.mostRecentRequests.remove(sender.getUniqueId());

        if (uuid == null) {
            return null;
        }

        return this.requestMap.remove(uuid);
    }

    public boolean addRequest(TeleportRequest request) {
        this.mostRecentRequests.put(request.getSender().getUniqueId(), request.getReceiver().getUniqueId());
        return this.requestMap.putIfAbsent(request.getReceiver().getUniqueId(), request) == null;
    }

    public Collection<TeleportRequest> getAllRequests() {
        return this.requestMap.values();
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
