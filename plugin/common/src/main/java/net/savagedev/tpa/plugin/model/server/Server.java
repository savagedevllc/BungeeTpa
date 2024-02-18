package net.savagedev.tpa.plugin.model.server;

import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

public interface Server<T> {
    void setSentBasicInfo(boolean sentBasicInfo);

    void setEconomySupport(boolean economySupport);

    void sendData(String channel, byte[] data);

    boolean isAccessibleTo(ProxyPlayer<?, ?> player);

    boolean hasEconomySupport();

    boolean hasSentBasicInfo();

    String getId();

    T getHandle();
}
