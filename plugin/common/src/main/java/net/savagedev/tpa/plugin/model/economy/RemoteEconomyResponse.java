package net.savagedev.tpa.plugin.model.economy;

import net.savagedev.tpa.common.hook.economy.EconomyResponse;

public class RemoteEconomyResponse extends EconomyResponse {
    private final String formattedAmount;
    private final String formattedBalance;

    public RemoteEconomyResponse(double amount, double balance, String formattedAmount, String formattedBalance, boolean success) {
        super(amount, balance, success);

        this.formattedAmount = formattedAmount;
        this.formattedBalance = formattedBalance;
    }

    public String getFormattedAmount() {
        return this.formattedAmount;
    }

    public String getFormattedBalance() {
        return this.formattedBalance;
    }
}
