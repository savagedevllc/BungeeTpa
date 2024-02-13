package net.savagedev.tpa.plugin.model.server;

import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

public interface Server<T> {
    void sendData(String channel, byte[] data);

    boolean isAccessibleTo(ProxyPlayer<?, ?> player);

    String getId();

    T getHandle();
}
