package net.savagedev.tpa.sponge.hook.economy;

import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.sponge.BungeeTpSpongePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.math.BigDecimal;
import java.util.Optional;

public class SpongeEconomyHook extends AbstractEconomyHook {
    private EconomyService economyService;

    private final boolean enabled;

    public SpongeEconomyHook(BungeeTpSpongePlugin platform) {
        super(platform);

        final Optional<EconomyService> serviceOpt = Sponge.server().serviceProvider().economyService();

        this.enabled = serviceOpt.isPresent();
        if (this.enabled) {
            this.economyService = serviceOpt.get();
        }
    }

    @Override
    public double withdraw(BungeeTpPlayer player, double amount) {
        final Optional<UniqueAccount> optionalAccount = this.economyService.findOrCreateAccount(player.getUniqueId());
        return optionalAccount.map(account ->
                account.withdraw(this.economyService.defaultCurrency(), BigDecimal.valueOf(amount)).amount().doubleValue()
        ).orElse(0.0);
    }

    @Override
    public double getBalance(BungeeTpPlayer player) {
        final Optional<UniqueAccount> optionalAccount = this.economyService.findOrCreateAccount(player.getUniqueId());
        return optionalAccount.map(account ->
                account.balance(this.economyService.defaultCurrency()).doubleValue()
        ).orElse(0.0);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
