package net.savagedev.tpa.spigot.listeners;

import net.savagedev.tpa.bridge.listener.AbstractConnectionListener;
import net.savagedev.tpa.spigot.BungeeTpSpigotPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionListener extends AbstractConnectionListener implements Listener {
    private final BungeeTpSpigotPlugin plugin;

    public ConnectionListener(BungeeTpSpigotPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent e) {
        super.handleJoinEvent(this.plugin.getBungeeTpPlayer(e.getPlayer().getUniqueId()));
    }
}
