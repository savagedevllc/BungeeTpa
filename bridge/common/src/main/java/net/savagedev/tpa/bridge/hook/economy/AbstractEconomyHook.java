package net.savagedev.tpa.bridge.hook.economy;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;

public abstract class AbstractEconomyHook {
    private final BungeeTpBridgePlatform platform;

    public AbstractEconomyHook(BungeeTpBridgePlatform platform) {
        this.platform = platform;
    }

    /**
     * Removes the specified amount from the player's balance.
     *
     * @param player - The player whose balance will be effected.
     * @return - The player's new balance.
     */
    public abstract double withdraw(BungeeTpPlayer player, double amount);

    /**
     * Returns the player's balance.
     *
     * @param player - The player whose balance will be returned.
     * @return - The player's balance.
     */
    public abstract double getBalance(BungeeTpPlayer player);

    public abstract boolean isEnabled();
}
