package net.savagedev.tpa.spigot.listeners;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.listener.AbstractWorldListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener extends AbstractWorldListener implements Listener {
    public WorldListener(BungeeTpBridgePlatform platform) {
        super(platform);
    }

    @EventHandler
    public void on(WorldLoadEvent event) {
        super.handleWorldLoadEvent(event.getWorld().getName());
    }

    @EventHandler
    public void on(WorldUnloadEvent event) {
        super.handleWorldUnloadEvent(event.getWorld().getName());
    }
}
