package net.savagedev.tpa.bungee.functions;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
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
        final ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(id);

        if (serverInfo == null) {
            return null;
        }

        return BungeeServer.fromServerInfo(serverInfo, this.platform.getPlugin());
    }
}
