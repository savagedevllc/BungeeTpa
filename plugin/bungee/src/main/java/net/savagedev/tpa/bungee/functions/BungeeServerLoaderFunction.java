package net.savagedev.tpa.bungee.functions;

import net.md_5.bungee.api.ProxyServer;
import net.savagedev.tpa.bungee.BungeeTpBungeePlugin;
import net.savagedev.tpa.bungee.model.server.BungeeServer;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.function.Function;

public class BungeeServerLoaderFunction implements Function<String, Server<?>> {
    private final BungeeTpBungeePlugin platform;

    public BungeeServerLoaderFunction(BungeeTpBungeePlugin platform) {
        this.platform = platform;
    }

    @Override
    public Server<?> apply(String id) {
        return BungeeServer.fromServerInfo(ProxyServer.getInstance().getServerInfo(id), this.platform.getPlugin());
    }
}
