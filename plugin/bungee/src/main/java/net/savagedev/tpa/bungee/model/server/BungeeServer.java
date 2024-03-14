package net.savagedev.tpa.bungee.model.server;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.server.AbstractServer;

public class BungeeServer extends AbstractServer<ServerInfo> {
    public static BungeeServer fromServerInfo(ServerInfo serverInfo, BungeeTpPlugin plugin) {
        return new BungeeServer(serverInfo.getName(), serverInfo, plugin);
    }

    public BungeeServer(String id, ServerInfo handle, BungeeTpPlugin plugin) {
        super(id, handle, plugin);
    }

    @Override
    protected boolean canAccess(ProxyPlayer<?, ?> player) {
        return super.getHandle().canAccess((CommandSender) player.getHandle());
    }

    @Override
    public void sendData(String channel, byte[] data) {
        super.getHandle().sendData(channel, data);
    }
}
