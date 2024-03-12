package net.savagedev.tpa.plugin.model.player;

import net.savagedev.tpa.common.messaging.messages.MessageEconomyDepositRequest;
import net.savagedev.tpa.common.messaging.messages.MessageEconomyWithdrawRequest;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.model.economy.RemoteEconomyResponse;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractProxyPlayer<T, M> implements ProxyPlayer<T, M> {
    private final Function<String, M> messageFormattingFunction;
    private final T handle;

    private final BungeeTpPlugin plugin;

    private boolean hidden = false;

    public AbstractProxyPlayer(BungeeTpPlugin plugin, T handle, Function<String, M> messageFormattingFunction) {
        this.messageFormattingFunction = messageFormattingFunction;
        this.handle = handle;

        this.plugin = plugin;
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage(this.messageFormattingFunction.apply(message));
    }

    @Override
    public CompletableFuture<RemoteEconomyResponse> deposit(double amount) {
        final CompletableFuture<RemoteEconomyResponse> future = new CompletableFuture<>();
        this.plugin.getPlatform().getMessenger().sendData(this.getCurrentServer(), new MessageEconomyDepositRequest(this.getUniqueId(), amount));
        this.plugin.getPlayerManager().addPendingTransaction(this.getUniqueId(), future);
        return future;
    }

    @Override
    public CompletableFuture<RemoteEconomyResponse> withdraw(double amount) {
        final CompletableFuture<RemoteEconomyResponse> future = new CompletableFuture<>();
        this.plugin.getPlatform().getMessenger().sendData(this.getCurrentServer(), new MessageEconomyWithdrawRequest(this.getUniqueId(), amount));
        this.plugin.getPlayerManager().addPendingTransaction(this.getUniqueId(), future);
        return future;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public Server<?> getCurrentServer() {
        return this.plugin.getServerManager().getOrLoad(this.getCurrentServerId())
                .orElseThrow(() -> new IllegalStateException("Player not loaded."));
    }

    protected abstract String getCurrentServerId();

    @Override
    public boolean canBypassDelay() {
        return this.hasPermission("bungeetp.delay.bypass") || this.hasPermission("bungeetp.bypass.delay");
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public boolean notHidden() {
        return !this.hidden;
    }

    @Override
    public T getHandle() {
        return this.handle;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProxyPlayer && ((ProxyPlayer<?, ?>) obj).getUniqueId().equals(this.getUniqueId());
    }
}
