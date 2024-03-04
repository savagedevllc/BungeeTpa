package net.savagedev.tpa.spigot.hook.economy;

import net.milkbowl.vault.economy.Economy;
import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.hook.economy.EconomyResponse;
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
    public EconomyResponse deposit(BungeeTpPlayer player, double amount) {
        if (this.economy == null) {
            throw new IllegalStateException();
        }

        final net.milkbowl.vault.economy.EconomyResponse response = this.economy.depositPlayer(((SpigotPlayer) player).getHandle(), amount);
        return new EconomyResponse(response.amount, response.balance, response.transactionSuccess());
    }

    @Override
    public EconomyResponse withdraw(BungeeTpPlayer player, double amount) {
        if (this.economy == null) {
            throw new IllegalStateException();
        }

        final net.milkbowl.vault.economy.EconomyResponse response = this.economy.withdrawPlayer(((SpigotPlayer) player).getHandle(), amount);
        return new EconomyResponse(response.amount, response.balance, response.transactionSuccess());
    }

    @Override
    public String format(double amount) {
        if (this.economy == null) {
            throw new IllegalStateException();
        }
        return this.economy.format(amount);
    }

    @Override
    public boolean isEnabled() {
        return this.economy != null;
    }
}
