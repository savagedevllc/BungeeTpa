package net.savagedev.tpa.bungee.functions;

import net.md_5.bungee.api.ProxyServer;
import net.savagedev.tpa.bungee.BungeeTpBungeePlugin;
import net.savagedev.tpa.bungee.model.player.BungeePlayer;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.UUID;
import java.util.function.Function;

public class BungeePlayerLoaderFunction implements Function<UUID, ProxyPlayer<?, ?>> {
    private final BungeeTpBungeePlugin platform;

    public BungeePlayerLoaderFunction(BungeeTpBungeePlugin platform) {
        this.platform = platform;
    }

    @Override
    public ProxyPlayer<?, ?> apply(UUID uuid) {
        return new BungeePlayer(ProxyServer.getInstance().getPlayer(uuid), this.platform.getPlugin());
    }
}
