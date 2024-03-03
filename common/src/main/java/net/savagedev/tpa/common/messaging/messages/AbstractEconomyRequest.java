package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public abstract class AbstractEconomyRequest extends Message {
    private final UUID uniqueId;

    private final double amount;

    public AbstractEconomyRequest(UUID uniqueId, double amount) {
        this.uniqueId = uniqueId;
        this.amount = amount;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public double getAmount() {
        return this.amount;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("msb", this.uniqueId.getMostSignificantBits());
        object.addProperty("lsb", this.uniqueId.getLeastSignificantBits());
        object.addProperty("amt", this.amount);
        return object;
    }
}
