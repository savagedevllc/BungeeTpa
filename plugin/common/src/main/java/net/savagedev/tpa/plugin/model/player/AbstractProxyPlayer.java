package net.savagedev.tpa.plugin.model.player;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.model.server.Server;

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
}
