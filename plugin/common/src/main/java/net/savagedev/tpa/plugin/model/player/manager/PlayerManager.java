package net.savagedev.tpa.plugin.model.player.manager;

import net.savagedev.tpa.plugin.model.AbstractManager;
import net.savagedev.tpa.plugin.model.economy.RemoteEconomyResponse;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PlayerManager extends AbstractManager<UUID, ProxyPlayer<?, ?>> {
    private final Map<UUID, CompletableFuture<RemoteEconomyResponse>> pendingTransactions = new HashMap<>();

    public PlayerManager(Function<UUID, ProxyPlayer<?, ?>> loader) {
        super(loader);
    }

    public Optional<ProxyPlayer<?, ?>> getByName(String username) {
        return this.getAll().values()
                .stream()
                .filter(player -> player.getName().equalsIgnoreCase(username))
                .findFirst();
    }

    public void addPendingTransaction(UUID uuid, CompletableFuture<RemoteEconomyResponse> future) {
        this.pendingTransactions.put(uuid, future);
    }

    public CompletableFuture<RemoteEconomyResponse> removePendingTransaction(UUID uuid) {
        return this.pendingTransactions.remove(uuid);
    }

    @Override
    protected UUID sanitizeId(UUID id) {
        return id;
    }
}
