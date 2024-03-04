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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class TeleportManager {
    private final Map<UUID, TeleportRequest> requestMap = new HashMap<>();

    private final BungeeTpPlatform platform;

    public TeleportManager(BungeeTpPlatform platform) {
        this.platform = platform;
    }

    public void shutdown() {
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

    public TeleportRequest removeRequest(ProxyPlayer<?, ?> player) {
        return this.requestMap.remove(player.getUniqueId());
    }

    public TeleportRequest removeRequestBySenderOrReceiver(ProxyPlayer<?, ?> player) {
        return this.removeRequestBySender(player).orElse(this.removeRequest(player));
    }

    public Optional<TeleportRequest> removeRequestBySender(ProxyPlayer<?, ?> sender) {
        for (TeleportRequest request : this.getAllRequests()) {
            if (request.getSender().getUniqueId().equals(sender.getUniqueId())) {
                return Optional.of(this.removeRequest(request.getReceiver()));
            }
        }
        return Optional.empty();
    }

    public boolean addRequest(ProxyPlayer<?, ?> player, TeleportRequest request) {
        return this.requestMap.putIfAbsent(player.getUniqueId(), request) == null;
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
