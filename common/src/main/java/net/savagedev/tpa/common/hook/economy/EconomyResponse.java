package net.savagedev.tpa.common.hook.economy;

public class EconomyResponse {
    private final double amount;
    private final double balance;

    private final boolean success;

    public EconomyResponse(double amount, double balance, boolean success) {
        this.amount = amount;
        this.balance = balance;
        this.success = success;
    }

    public double getAmount() {
        return this.amount;
    }

    public double getBalance() {
        return this.balance;
    }

    public boolean isSuccess() {
        return this.success;
    }
}
