package net.savagedev.tpa.bungee.listeners;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.savagedev.tpa.bungee.model.player.BungeePlayer;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.listeners.AbstractConnectionListener;

public class BungeeConnectionListener extends AbstractConnectionListener implements Listener {

    public BungeeConnectionListener(BungeeTpPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(PostLoginEvent event) {
        this.handleConnectEvent(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void on(PlayerDisconnectEvent event) {
        super.handleDisconnectEvent(super.plugin.getPlayer(event.getPlayer().getUniqueId())
                .orElse(new BungeePlayer(event.getPlayer())));
    }
}
