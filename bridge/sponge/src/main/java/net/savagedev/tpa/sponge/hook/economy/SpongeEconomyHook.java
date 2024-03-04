package net.savagedev.tpa.sponge.hook.economy;

import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.hook.economy.EconomyResponse;
import net.savagedev.tpa.sponge.BungeeTpSpongePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.Optional;

public class SpongeEconomyHook extends AbstractEconomyHook {
    private static final EconomyResponse EMPTY_FAILED_ECONOMY_RESPONSE = new EconomyResponse(0, 0, false);

    private EconomyService economy;

    private final boolean enabled;

    public SpongeEconomyHook(BungeeTpSpongePlugin platform) {
        super(platform);

        final Optional<EconomyService> serviceOpt = Sponge.server().serviceProvider().economyService();

        this.enabled = serviceOpt.isPresent();
        if (this.enabled) {
            this.economy = serviceOpt.get();
        }
    }

    @Override
    public EconomyResponse deposit(BungeeTpPlayer player, double amount) {
        return this.economy.findOrCreateAccount(player.getUniqueId())
                .map(account -> {
                            final TransactionResult result = account.deposit(this.economy.defaultCurrency(), BigDecimal.valueOf(amount));
                            return new EconomyResponse(result.amount().doubleValue(), result.account().balance(result.currency()).doubleValue(), result.result() == ResultType.SUCCESS);
                        }
                ).orElse(EMPTY_FAILED_ECONOMY_RESPONSE);
    }

    @Override
    public EconomyResponse withdraw(BungeeTpPlayer player, double amount) {
        return this.economy.findOrCreateAccount(player.getUniqueId())
                .map(account -> {
                            final TransactionResult result = account.withdraw(this.economy.defaultCurrency(), BigDecimal.valueOf(amount));
                            return new EconomyResponse(result.amount().doubleValue(), result.account().balance(result.currency()).doubleValue(), result.result() == ResultType.SUCCESS);
                        }
                ).orElse(EMPTY_FAILED_ECONOMY_RESPONSE);
    }

    @Override
    public String format(double amount) {
        return this.economy.defaultCurrency().format(BigDecimal.valueOf(amount)).toString();
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
