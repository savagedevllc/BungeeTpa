package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessageEconomyResponse extends Message {
    public static MessageEconomyResponse deserialize(JsonObject object) {
        final long mostSigBits = object.get("msb").getAsLong();
        final long leastSigBits = object.get("lsb").getAsLong();
        return new MessageEconomyResponse(new UUID(mostSigBits, leastSigBits), object.get("amt").getAsDouble(), object.get("bal").getAsDouble(), object.get("s").getAsBoolean());
    }

    public final UUID uniqueId;

    private final double amount;
    private final double balance;

    private final boolean successful;

    public MessageEconomyResponse(UUID uniqueId, double amount, double balance, boolean successful) {
        this.uniqueId = uniqueId;
        this.amount = amount;
        this.balance = balance;
        this.successful = successful;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public double getAmount() {
        return this.amount;
    }

    public double getBalance() {
        return this.balance;
    }

    public boolean wasSuccessful() {
        return this.successful;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("msb", this.uniqueId.getMostSignificantBits());
        object.addProperty("lsb", this.uniqueId.getLeastSignificantBits());
        object.addProperty("amt", this.amount);
        object.addProperty("bal", this.balance);
        object.addProperty("s", this.successful);
        return object;
    }
}
