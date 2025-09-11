package net.savagedev.tpa.spigot;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.BungeeTpBridgePlugin;
import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.hook.vanish.AbstractVanishHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.bridge.model.TeleportTarget;
import net.savagedev.tpa.common.messaging.Messenger;
import net.savagedev.tpa.spigot.hook.economy.VaultEconomyHook;
import net.savagedev.tpa.spigot.hook.vanish.EssentialsVanishHook;
import net.savagedev.tpa.spigot.hook.vanish.GenericVanishHook;
import net.savagedev.tpa.spigot.hook.vanish.SuperVanishPluginHook;
import net.savagedev.tpa.spigot.listeners.ConnectionListener;
import net.savagedev.tpa.spigot.listeners.WhitelistListener;
import net.savagedev.tpa.spigot.listeners.WorldListener;
import net.savagedev.tpa.spigot.messenger.SpigotPluginMessenger;
import net.savagedev.tpa.spigot.model.SpigotPlayer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class BungeeTpSpigotPlugin extends JavaPlugin implements BungeeTpBridgePlatform {
    private static final int B_STATS_ID = 20995;

    private final BungeeTpBridgePlugin plugin = new BungeeTpBridgePlugin(this);

    private final Messenger<BungeeTpPlayer> messenger = new SpigotPluginMessenger(this);

    private AbstractVanishHook vanishProvider;
    private AbstractEconomyHook economyHook;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        this.getServer().getPluginManager().registerEvents(new WhitelistListener(this), this);
        this.getServer().getPluginManager().registerEvents(new WorldListener(this), this);

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
        if (this.getServer().getPluginManager().isPluginEnabled("SuperVanish") ||
                this.getServer().getPluginManager().isPluginEnabled("PremiumVanish")) {
            this.vanishProvider = new SuperVanishPluginHook(this);
            return;
        }

        if (this.getServer().getPluginManager().isPluginEnabled("Essentials")) {
            this.vanishProvider = new EssentialsVanishHook(this);
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
    public Collection<String> getAllWorlds() {
        return Bukkit.getWorlds().stream()
                .map(World::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<UUID> getWhitelist() {
        return this.getServer().getWhitelistedPlayers().stream()
                .map(OfflinePlayer::getUniqueId)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPluginVersion() {
        return this.getDescription().getVersion();
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
    public boolean isWhitelisted() {
        return this.getServer().hasWhitelist();
    }

    @Override
    public Messenger<BungeeTpPlayer> getMessenger() {
        return this.messenger;
    }

    public BungeeTpPlayer getBungeeTpPlayer(Player player) {
        if (player == null) {
            return null;
        }
        return new SpigotPlayer(player, this);
    }

    @Override
    public BungeeTpPlayer getBungeeTpPlayer(UUID uuid) {
        return this.getBungeeTpPlayer(this.getServer().getPlayer(uuid));
    }

    @Override
    public Optional<BungeeTpPlayer> getAnyBungeeTpPlayer() {
        return this.getServer().getOnlinePlayers().stream().findAny()
                .map(Player::getUniqueId)
                .map(this::getBungeeTpPlayer);
    }

    @Override
    public Map<UUID, TeleportTarget> getTpCache() {
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

    public BungeeTpBridgePlugin getPlugin() {
        return this.plugin;
    }
}
