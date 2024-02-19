package net.savagedev.tpa.velocity.functions;

import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.velocity.BungeeTpVelocityPlugin;
import net.savagedev.tpa.velocity.model.player.VelocityPlayer;

import java.util.UUID;
import java.util.function.Function;

public class VelocityPlayerLoaderFunction implements Function<UUID, ProxyPlayer<?, ?>> {
    private final BungeeTpVelocityPlugin plugin;

    public VelocityPlayerLoaderFunction(BungeeTpVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ProxyPlayer<?, ?> apply(UUID uuid) {
        return new VelocityPlayer(this.plugin.getServer().getPlayer(uuid).orElse(null), this.plugin.getPlugin());
    }
}
