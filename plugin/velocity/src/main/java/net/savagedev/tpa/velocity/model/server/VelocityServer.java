package net.savagedev.tpa.velocity.model.server;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.server.AbstractServer;

public class VelocityServer extends AbstractServer<RegisteredServer> {
    public VelocityServer(String id, RegisteredServer handle, BungeeTpPlugin plugin) {
        super(id, handle, plugin);
    }

    @Override
    protected boolean canAccess(ProxyPlayer<?, ?> player) {
        return true;
    }

    @Override
    public void sendData(String channel, byte[] data) {
        this.getHandle().sendPluginMessage(MinecraftChannelIdentifier.from(channel), data);
    }
}
