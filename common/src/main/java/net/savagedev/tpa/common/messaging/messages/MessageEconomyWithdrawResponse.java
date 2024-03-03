package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessageEconomyWithdrawResponse extends Message {
    public static MessageEconomyWithdrawResponse deserialize(JsonObject object) {
        return null;
    }

    public final UUID uniqueId;

    private final double withdrawAmount;
    private final double newBalance;

    private final boolean successful;

    public MessageEconomyWithdrawResponse(UUID uniqueId, double withdrawAmount, double newBalance, boolean successful) {
        this.uniqueId = uniqueId;
        this.withdrawAmount = withdrawAmount;
        this.newBalance = newBalance;
        this.successful = successful;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public double getWithdrawAmount() {
        return this.withdrawAmount;
    }

    public double getNewBalance() {
        return this.newBalance;
    }

    public boolean wasSuccessful() {
        return this.successful;
    }

    @Override
    protected JsonObject asJsonObject() {
        return null;
    }
}
