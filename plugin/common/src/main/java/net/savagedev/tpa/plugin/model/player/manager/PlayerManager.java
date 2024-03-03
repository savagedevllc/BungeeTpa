package net.savagedev.tpa.plugin.model.player.manager;

import net.savagedev.tpa.common.hook.economy.EconomyResponse;
import net.savagedev.tpa.plugin.model.AbstractManager;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class PlayerManager extends AbstractManager<UUID, ProxyPlayer<?, ?>> {
    private final Map<UUID, CompletableFuture<EconomyResponse>> awaitingResponse = new HashMap<>();

    public PlayerManager(Function<UUID, ProxyPlayer<?, ?>> loader) {
        super(loader);
    }

    public Optional<ProxyPlayer<?, ?>> getByName(String username) {
        return this.getAll().values()
                .stream()
                .filter(player -> player.getName().equalsIgnoreCase(username))
                .findFirst();
    }

    public void addAwaitingResponse(UUID uuid, CompletableFuture<EconomyResponse> future) {
        this.awaitingResponse.put(uuid, future);
    }

    public CompletableFuture<EconomyResponse> removeAwaitingResponse(UUID uuid) {
        return this.awaitingResponse.remove(uuid);
    }

    @Override
    protected UUID sanitizeId(UUID id) {
        return id;
    }
}
