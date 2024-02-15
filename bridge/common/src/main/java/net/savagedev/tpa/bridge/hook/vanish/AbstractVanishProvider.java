package net.savagedev.tpa.bridge.hook.vanish;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.messages.MessagePlayerInfo;

public abstract class AbstractVanishProvider {
    private final BungeeTpBridgePlatform platform;

    public AbstractVanishProvider(BungeeTpBridgePlatform platform) {
        this.platform = platform;
    }

    protected void onVanishEvent(BungeeTpPlayer player, boolean vanished) {
        this.platform.getPlatformMessenger().sendData(player, new MessagePlayerInfo(player.getUniqueId(), vanished));
    }

    public abstract boolean isVanished(BungeeTpPlayer player);
}
