package net.savagedev.tpa.plugin.model.player;

import net.savagedev.tpa.common.hook.economy.EconomyResponse;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ProxyPlayer<T, M> {
    void setHidden(boolean hidden);

    void sendMessage(String message);

    void sendMessage(M message);

    CompletableFuture<EconomyResponse> deposit(double amount);

    CompletableFuture<EconomyResponse> withdraw(double amount);

    boolean connect(Server<?> server);

    boolean hasPermission(String permission);

    boolean canBypassDelay();

    boolean isConnected();

    boolean isHidden();

    boolean notHidden();

    Server<?> getCurrentServer();

    UUID getUniqueId();

    String getName();

    T getHandle();
}
