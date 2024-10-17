package net.savagedev.tpa.velocity.functions;

import net.savagedev.tpa.plugin.model.server.Server;
import net.savagedev.tpa.velocity.BungeeTpVelocityPlugin;
import net.savagedev.tpa.velocity.model.server.VelocityServer;

import java.util.function.Function;

public class VelocityServerLoaderFunction implements Function<String, Server<?>> {
    private final BungeeTpVelocityPlugin platform;

    public VelocityServerLoaderFunction(BungeeTpVelocityPlugin platform) {
        this.platform = platform;
    }

    @Override
    public Server<?> apply(String id) {
        return this.platform.getServer().getServer(id)
                .map(server -> new VelocityServer(id, server, this.platform.getPlugin()))
                .orElse(null);
    }
}
