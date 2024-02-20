package net.savagedev.tpa.bungee.model.player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.savagedev.tpa.bungee.BungeeTpBungeePlugin;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.model.player.AbstractProxyPlayer;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.UUID;

public class BungeePlayer extends AbstractProxyPlayer<ProxiedPlayer, BaseComponent[]> {
    private final ProxiedPlayer player;

    public BungeePlayer(ProxiedPlayer player, BungeeTpPlugin plugin) {
        super(plugin, player, BungeeTpBungeePlugin.CHAT_MESSAGE_FORMATTING_FUNCTION);
        this.player = player;
    }

    @Override
    public void sendMessage(BaseComponent[] message) {
        this.player.sendMessage(message);
    }

    @Override
    public boolean connect(Server<?> server) {
        final boolean canAccess = server.isAccessibleTo(this);
        if (canAccess) {
            this.player.connect((ServerInfo) server.getHandle());
        }
        return canAccess;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.player.hasPermission(permission);
    }

    @Override
    public boolean isConnected() {
        return this.player.isConnected();
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    protected String getCurrentServerId() {
        return this.player.getServer().getInfo().getName();
    }
}
