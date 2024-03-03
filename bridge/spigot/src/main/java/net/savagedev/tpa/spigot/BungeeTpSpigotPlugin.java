package net.savagedev.tpa.spigot;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.BungeeTpBridgePlugin;
import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.hook.vanish.AbstractVanishHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.Messenger;
import net.savagedev.tpa.spigot.hook.economy.VaultEconomyHook;
import net.savagedev.tpa.spigot.hook.vanish.EssentialsVanishHook;
import net.savagedev.tpa.spigot.hook.vanish.GenericVanishHook;
import net.savagedev.tpa.spigot.hook.vanish.SuperVanishPluginHook;
import net.savagedev.tpa.spigot.listeners.ConnectionListener;
import net.savagedev.tpa.spigot.messenger.SpigotPluginMessenger;
import net.savagedev.tpa.spigot.model.SpigotPlayer;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BungeeTpSpigotPlugin extends JavaPlugin implements BungeeTpBridgePlatform {
    private static final int B_STATS_ID = 20995;

    private final BungeeTpBridgePlugin plugin = new BungeeTpBridgePlugin(this);

    private final Messenger<BungeeTpPlayer> messenger = new SpigotPluginMessenger(this);

    private AbstractVanishHook vanishProvider;
    private AbstractEconomyHook economyHook;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        this.plugin.enable();

        this.hookEconomy();
        this.hookVanish();

        new Metrics(this, B_STATS_ID);
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }

    private void hookVanish() {
        if (this.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            this.vanishProvider = new EssentialsVanishHook(this);
            return;
        }

        if (this.getServer().getPluginManager().isPluginEnabled("SuperVanish") ||
                this.getServer().getPluginManager().isPluginEnabled("PremiumVanish")) {
            this.vanishProvider = new SuperVanishPluginHook(this);
            return;
        }

        this.vanishProvider = new GenericVanishHook(this);
    }

    private void hookEconomy() {
        if (this.getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.economyHook = new VaultEconomyHook(this);
        }
    }

    @Override
    public void scheduleAsync(Runnable runnable, long delay, long period) {
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, runnable, delay, period / 50L);
    }

    @Override
    public void delay(Runnable runnable, long delay) {
        // The delay should be in ms, and this platform's scheduler uses ticks.
        // To account for that difference, we convert the delay value to the equivalent tick value.
        // 1000 ms = 1s & 20 ticks = 1s therefore, 1000/20 = 50
        this.getServer().getScheduler().runTaskLater(this, runnable, delay / 50L);
    }

    @Override
    public Optional<AbstractVanishHook> getVanishProvider() {
        return Optional.ofNullable(this.vanishProvider);
    }

    @Override
    public Optional<AbstractEconomyHook> getEconomyProvider() {
        if (this.economyHook != null && this.economyHook.isEnabled()) {
            return Optional.of(this.economyHook);
        }
        return Optional.empty();
    }

    @Override
    public String getVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public String getSoftwareName() {
        return this.getServer().getName();
    }

    @Override
    public Messenger<BungeeTpPlayer> getMessenger() {
        return this.messenger;
    }

    public BungeeTpPlayer getBungeeTpPlayer(Player player) {
        return new SpigotPlayer(player, this);
    }

    @Override
    public BungeeTpPlayer getBungeeTpPlayer(UUID uuid) {
        return this.getBungeeTpPlayer(this.getServer().getPlayer(uuid));
    }

    @Override
    public BungeeTpPlayer getABungeeTpPlayer() {
        return this.getBungeeTpPlayer(this.getServer().getOnlinePlayers()
                .iterator()
                .next()
                .getUniqueId());
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
