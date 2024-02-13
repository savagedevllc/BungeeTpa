package net.savagedev.tpa.plugin.model.player;

import net.savagedev.tpa.plugin.model.server.Server;

import java.util.UUID;

public interface ProxyPlayer<T, M> {
    void sendMessage(String message);

    void sendMessage(M message);

    boolean connect(Server<?> server);

    boolean hasPermission(String permission);

    boolean isConnected();

    Server<?> getCurrentServer();

    UUID getUniqueId();

    String getName();

    T getHandle();
}
