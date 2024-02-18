package net.savagedev.tpa.bridge.hook.vanish;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.messages.MessagePlayerInfo;

public abstract class AbstractVanishHook {
    private final BungeeTpBridgePlatform platform;

    public AbstractVanishHook(BungeeTpBridgePlatform platform) {
        this.platform = platform;
    }

    protected void onVanishEvent(BungeeTpPlayer player, boolean vanished) {
        this.platform.getMessenger().sendData(player, new MessagePlayerInfo(player.getUniqueId(), vanished));
    }

    /**
     * Checks if the player is vanished on the server.
     *
     * @param player - The player whose vanish status will be checked.
     * @return - The vanish status of the player.
     */
    public abstract boolean isVanished(BungeeTpPlayer player);
}
