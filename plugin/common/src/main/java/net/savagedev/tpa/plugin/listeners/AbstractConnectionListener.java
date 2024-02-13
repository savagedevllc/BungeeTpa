package net.savagedev.tpa.plugin.listeners;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

import java.util.UUID;

public abstract class AbstractConnectionListener {
    protected final BungeeTpPlugin plugin;

    public AbstractConnectionListener(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    protected void handleConnectEvent(UUID uuid) {
        this.plugin.getPlayerManager().getOrLoad(uuid);
    }

    protected void handleDisconnectEvent(ProxyPlayer<?, ?> player) {
        final TeleportRequest request = this.plugin.getTeleportManager().removeRequest(player);
        if (request != null) {
            Lang.PLAYER_OFFLINE.send(request.getSender(), new Lang.Placeholder("%player%", player.getName()));
        }
        this.plugin.getPlayerManager().remove(player.getUniqueId());
    }
}
