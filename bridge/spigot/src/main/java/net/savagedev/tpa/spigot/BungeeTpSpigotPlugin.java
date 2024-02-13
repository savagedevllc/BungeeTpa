package net.savagedev.tpa.spigot;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.BungeeTpBridgePlugin;
import net.savagedev.tpa.bridge.messenger.Messenger;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.spigot.listeners.ConnectionListener;
import net.savagedev.tpa.spigot.messenger.SpigotPluginMessenger;
import net.savagedev.tpa.spigot.model.SpigotPlayer;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class BungeeTpSpigotPlugin extends JavaPlugin implements BungeeTpBridgePlatform {
    private static final int B_STATS_ID = 20995;

    private final BungeeTpBridgePlugin plugin = new BungeeTpBridgePlugin(this);

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        this.plugin.enable();

        new Metrics(this, B_STATS_ID);
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }

    @Override
    public void delay(Runnable runnable, long delay) {
        // The delay should be in ms, and this platform's scheduler uses ticks.
        // To account for that difference, we convert the delay value to the equivalent tick value.
        // 1000 ms = 1s & 20 ticks = 1s therefore, 1000/20 = 50
        this.getServer().getScheduler().runTaskLater(this, runnable, delay / 50L);
    }

    @Override
    public Messenger getPlatformMessenger() {
        return new SpigotPluginMessenger(this);
    }

    @Override
    public BungeeTpPlayer getBungeeTpPlayer(UUID uuid) {
        return new SpigotPlayer(this.getServer().getPlayer(uuid));
    }

    @Override
    public Map<UUID, UUID> getTpCache() {
        return this.plugin.getTpCache();
    }

    @Override
    public int getMaxPlayers() {
        return this.getServer().getMaxPlayers();
    }

    @Override
    public String getOfflineUsername(UUID uuid) {
        return this.getServer().getOfflinePlayer(uuid).getName();
    }
}
