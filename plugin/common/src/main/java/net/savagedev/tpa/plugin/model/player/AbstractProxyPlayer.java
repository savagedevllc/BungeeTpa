package net.savagedev.tpa.plugin.model.player;

import java.util.function.Function;

public abstract class AbstractProxyPlayer<T, M> implements ProxyPlayer<T, M> {
    private final Function<String, M> messageFormattingFunction;

    private final T handle;

    public AbstractProxyPlayer(T handle, Function<String, M> messageFormattingFunction) {
        this.messageFormattingFunction = messageFormattingFunction;
        this.handle = handle;
    }

    @Override
    public void sendMessage(String message) {
        this.sendMessage(this.messageFormattingFunction.apply(message));
    }

    @Override
    public T getHandle() {
        return this.handle;
    }
}
