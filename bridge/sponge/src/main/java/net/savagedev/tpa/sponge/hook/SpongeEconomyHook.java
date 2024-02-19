package net.savagedev.tpa.sponge.hook;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;

public class SpongeEconomyHook extends AbstractEconomyHook {
    public SpongeEconomyHook(BungeeTpBridgePlatform platform) {
        super(platform);
    }

    @Override
    public double withdraw(BungeeTpPlayer player, double amount) {
        return 0;
    }

    @Override
    public double getBalance(BungeeTpPlayer player) {
        return 0;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
