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
        return new VelocityServer(id, this.platform.getServer().getServer(id)
                .orElseThrow(() -> new IllegalStateException("Server not loaded.")), this.platform.getPlugin());
    }
}
