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

    public void acceptRequest(TeleportRequest request) {
        if (request == null) {
            CompletableFuture.completedFuture(false);
            return;
        }
        if (request.getDirection() == TeleportRequest.Direction.TO_SENDER) {
            if (request.getReceiver().hasPermission("bungeetp.delay.bypass")) {
                this.teleport(request.getReceiver(), request.getSender());
            } else {
                this.teleportDelayed(request.getReceiver(), request.getSender());
            }
        } else {
            if (request.getSender().hasPermission("bungeetp.delay.bypass")) {
                this.teleport(request.getSender(), request.getReceiver());
            } else {
                this.teleportDelayed(request.getSender(), request.getReceiver());
            }
        }
    }

    private void teleportDelayed(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        this.platform.scheduleTaskDelayed(() -> this.teleport(player, other), Setting.DELAY.asLong() * 1000L);
    }

    // TODO: Move the boolean result down the line...
    public boolean teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        boolean success = true;
        final Server<?> targetServer = other.getCurrentServer();

        final MessageRequestTeleport requestMessage = new MessageRequestTeleport(player.getUniqueId(), other.getUniqueId());
        if (player.getCurrentServer().equals(targetServer)) {
            requestMessage.setType(Type.INSTANT);
        } else {
            requestMessage.setType(Type.ON_JOIN);
            success = player.connect(targetServer);
        }

        this.platform.getPlatformMessenger().sendData(other, requestMessage);
        return success;
    }

    public TeleportRequest removeRequest(ProxyPlayer<?, ?> player) {
        return this.requestMap.remove(player.getUniqueId());
    }

    public boolean addRequest(ProxyPlayer<?, ?> player, TeleportRequest request) {
        return this.requestMap.putIfAbsent(player.getUniqueId(), request) == null;
    }

    public Collection<TeleportRequest> getAllRequests() {
        return this.requestMap.values();
    }
}
