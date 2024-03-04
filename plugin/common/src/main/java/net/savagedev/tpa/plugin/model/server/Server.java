package net.savagedev.tpa.plugin.model.server;

import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

public interface Server<T> {
    void setSentBasicInfo(boolean sentBasicInfo);

    void setEconomySupport(boolean economySupport);

    void setBridgeVersion(String bridgeVersion);

    void sendData(String channel, byte[] data);

    boolean isAccessibleTo(ProxyPlayer<?, ?> player);

    boolean hasNoEconomy();

    boolean hasSentBasicInfo();

    String getBridgeVersion();

    String getId();

    T getHandle();
}
