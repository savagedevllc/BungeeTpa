package net.savagedev.tpa.bridge.hook.economy;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.hook.economy.EconomyResponse;

public abstract class AbstractEconomyHook {
    private final BungeeTpBridgePlatform platform;

    public AbstractEconomyHook(BungeeTpBridgePlatform platform) {
        this.platform = platform;
    }

    /**
     * Adds the specified amount to the player's balance.
     *
     * @param player - The player whose balance will be effected.
     * @return - The player's new balance.
     */
    public abstract EconomyResponse deposit(BungeeTpPlayer player, double amount);

    /**
     * Removes the specified amount from the player's balance.
     *
     * @param player - The player whose balance will be effected.
     * @return - The player's new balance.
     */
    public abstract EconomyResponse withdraw(BungeeTpPlayer player, double amount);

    public abstract String format(double amount);

    public abstract boolean isEnabled();
}
