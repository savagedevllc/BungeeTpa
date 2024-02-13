package net.savagedev.tpa.bridge;

import net.savagedev.tpa.bridge.messenger.Messenger;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;

import java.util.Map;
import java.util.UUID;

public interface BungeeTpBridgePlatform {
    void delay(Runnable runnable, long delay);

    Messenger getPlatformMessenger();

    BungeeTpPlayer getBungeeTpPlayer(UUID uuid);

    Map<UUID, UUID> getTpCache();

    String getOfflineUsername(UUID uuid);

    int getMaxPlayers();
}
