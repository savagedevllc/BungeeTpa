package net.savagedev.tpa.spigot.hook.economy;

import net.milkbowl.vault.economy.Economy;
import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.spigot.BungeeTpSpigotPlugin;
import net.savagedev.tpa.spigot.model.SpigotPlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomyHook extends AbstractEconomyHook {
    private Economy economy;

    public VaultEconomyHook(BungeeTpSpigotPlugin platform) {
        super(platform);

        final RegisteredServiceProvider<Economy> economyService = platform.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyService == null) {
            platform.getLogger().info("No economy plugin found! Disabling economy support.");
            return;
        }
        this.economy = economyService.getProvider();
    }

    @Override
    public double withdraw(BungeeTpPlayer player, double amount) {
        if (this.economy == null) {
            return 0.0;
        }
        return this.economy.withdrawPlayer(((SpigotPlayer) player).getHandle(), amount).balance;
    }

    @Override
    public double getBalance(BungeeTpPlayer player) {
        if (this.economy == null) {
            return 0.0;
        }
        return this.economy.getBalance(((SpigotPlayer) player).getHandle());
    }

    @Override
    public boolean isEnabled() {
        return this.economy != null;
    }
}
