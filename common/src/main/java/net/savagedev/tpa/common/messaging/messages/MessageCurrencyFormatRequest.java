package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

public class MessageCurrencyFormatRequest extends Message {
    public static MessageCurrencyFormatRequest deserialize(JsonObject object) {
        return new MessageCurrencyFormatRequest(object.get("amt").getAsDouble());
    }

    private final double amount;

    public MessageCurrencyFormatRequest(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return this.amount;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("amt", this.amount);
        return object;
    }
}
