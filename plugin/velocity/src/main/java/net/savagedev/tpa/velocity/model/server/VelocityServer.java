package net.savagedev.tpa.velocity.model.server;

import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.server.AbstractServer;

public class VelocityServer extends AbstractServer<ServerConnection> {
    public VelocityServer(String id, ServerConnection handle) {
        super(id, handle);
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
