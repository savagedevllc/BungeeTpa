package net.savagedev.tpa.plugin.model.server.manager;

import net.savagedev.tpa.plugin.model.AbstractManager;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ServerManager extends AbstractManager<String, Server<?>> {
    private final Map<String, CompletableFuture<String>> awaitingCurrencyFormat = new HashMap<>();

    public ServerManager(Function<String, Server<?>> loader) {
        super(loader);
    }

    public void addAwaitingCurrencyFormat(String id, CompletableFuture<String> future) {
        this.awaitingCurrencyFormat.put(id, future);
    }

    public CompletableFuture<String> removeAwaitingCurrencyFormat(String id) {
        return this.awaitingCurrencyFormat.remove(id);
    }

    @Override
    protected String sanitizeId(String id) {
        return id.toLowerCase(Locale.ROOT);
    }
}
