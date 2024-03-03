package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessageEconomyWithdrawRequest extends Message {
    public static MessageEconomyWithdrawRequest deserialize(JsonObject object) {
        final long mostSigBits = object.get("msb").getAsLong();
        final long leastSigBits = object.get("lsb").getAsLong();
        return new MessageEconomyWithdrawRequest(new UUID(mostSigBits, leastSigBits), object.get("amt").getAsFloat());
    }

    private final UUID uniqueId;

    private final float amount;

    public MessageEconomyWithdrawRequest(UUID uniqueId, float amount) {
        this.uniqueId = uniqueId;
        this.amount = amount;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public float getAmount() {
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
