package net.savagedev.tpa.velocity.functions;

import com.velocitypowered.api.proxy.ProxyServer;
import net.savagedev.tpa.plugin.model.server.Server;
import net.savagedev.tpa.velocity.model.server.VelocityServer;

import java.util.function.Function;

public class VelocityServerLoaderFunction implements Function<String, Server<?>> {
    private final ProxyServer server;

    public VelocityServerLoaderFunction(ProxyServer server) {
        this.server = server;
    }

    @Override
    public Server<?> apply(String id) {
        return new VelocityServer(id, this.server.getServer(id)
                .orElseThrow(() -> new IllegalStateException("Server not loaded.")));
    }
}
