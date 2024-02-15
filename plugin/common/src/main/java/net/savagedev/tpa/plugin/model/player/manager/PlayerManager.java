package net.savagedev.tpa.plugin.model.player.manager;

import net.savagedev.tpa.plugin.model.AbstractManager;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class PlayerManager extends AbstractManager<UUID, ProxyPlayer<?, ?>> {
    public PlayerManager(Function<UUID, ProxyPlayer<?, ?>> loader) {
        super(loader);
    }

    public Optional<ProxyPlayer<?, ?>> getByName(String username) {
        return this.getAll().values()
                .stream()
                .filter(player -> player.getName().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    protected UUID sanitizeId(UUID id) {
        return id;
    }
}
