package net.savagedev.tpa.bungee.listeners;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.listeners.AbstractConnectionListener;

public class BungeeConnectionListener extends AbstractConnectionListener implements Listener {

    public BungeeConnectionListener(BungeeTpPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(PostLoginEvent event) {
        super.handleConnectEvent(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(ServerConnectedEvent event) {
        super.handleServerConnectEvent(this.plugin.getPlayer(event.getPlayer().getUniqueId())
                .orElseThrow(() -> new IllegalStateException("Player not loaded.")), event.getServer().getInfo().getName());
    }

    @EventHandler
    public void on(PlayerDisconnectEvent event) {
        super.handleDisconnectEvent(super.plugin.getPlayer(event.getPlayer().getUniqueId())
                .orElseThrow(() -> new IllegalStateException("Player not loaded.")));
    }
}
