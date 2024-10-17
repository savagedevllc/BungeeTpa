package net.savagedev.tpa.plugin.model.server;

import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface Server<T> {
    void setSentBasicInfo(boolean sentBasicInfo);

    void setEconomySupport(boolean economySupport);

    void setServerSoftware(String software);

    void setBridgeVersion(String bridgeVersion);

    void sendData(String channel, byte[] data);

    boolean isAccessibleTo(ProxyPlayer<?, ?> player);

    boolean hasNoEconomy();

    boolean hasSentBasicInfo();

    CompletableFuture<String> formatCurrency(double amount);

    Collection<String> getAllWorlds();

    String getServerSoftware();

    String getBridgeVersion();

    String getId();

    T getHandle();
}
