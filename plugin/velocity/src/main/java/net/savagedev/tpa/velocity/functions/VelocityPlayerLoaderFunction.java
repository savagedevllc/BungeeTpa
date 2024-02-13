package net.savagedev.tpa.velocity.functions;

import com.velocitypowered.api.proxy.ProxyServer;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.velocity.model.player.VelocityPlayer;

import java.util.UUID;
import java.util.function.Function;

public class VelocityPlayerLoaderFunction implements Function<UUID, ProxyPlayer<?, ?>> {
    private final ProxyServer server;

    public VelocityPlayerLoaderFunction(ProxyServer server) {
        this.server = server;
    }

    @Override
    public ProxyPlayer<?, ?> apply(UUID uuid) {
        return new VelocityPlayer(this.server.getPlayer(uuid).orElse(null));
    }
}
