package net.savagedev.tpa.bridge;

import net.savagedev.tpa.bridge.hook.vanish.AbstractVanishProvider;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.Messenger;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BungeeTpBridgePlatform {
    void delay(Runnable runnable, long delay);

    Optional<AbstractVanishProvider> getVanishProvider();

    Messenger<BungeeTpPlayer> getPlatformMessenger();

    BungeeTpPlayer getBungeeTpPlayer(UUID uuid);

    Map<UUID, UUID> getTpCache();

    String getOfflineUsername(UUID uuid);

    int getMaxPlayers();
}
