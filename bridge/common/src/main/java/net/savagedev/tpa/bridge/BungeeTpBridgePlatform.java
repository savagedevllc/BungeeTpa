package net.savagedev.tpa.bridge;

import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.hook.vanish.AbstractVanishHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.bridge.model.TeleportTarget;
import net.savagedev.tpa.common.messaging.Messenger;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BungeeTpBridgePlatform {
    void scheduleAsync(Runnable runnable, long delay, long period);

    void delay(Runnable runnable, long delay);

    Messenger<BungeeTpPlayer> getMessenger();

    BungeeTpPlayer getBungeeTpPlayer(UUID uuid);

    BungeeTpPlayer getABungeeTpPlayer();

    Map<UUID, TeleportTarget> getTpCache();

    String getOfflineUsername(UUID uuid);

    Optional<AbstractVanishHook> getVanishProvider();

    Optional<AbstractEconomyHook> getEconomyProvider();

    Collection<String> getAllWorlds();

    Collection<UUID> getWhitelist();

    String getVersion();

    String getSoftwareName();

    boolean isWhitelisted();

    int getMaxPlayers();
}
