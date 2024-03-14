package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

public class MessageCurrencyFormatResponse extends Message {
    public static MessageCurrencyFormatResponse deserialize(JsonObject object) {
        return new MessageCurrencyFormatResponse(object.get("amt").getAsDouble(), object.get("fmtd_amt").getAsString());
    }

    private final double amount;

    private final String formattedAmount;

    public MessageCurrencyFormatResponse(double amount, String formattedAmount) {
        this.amount = amount;
        this.formattedAmount = formattedAmount;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getFormattedAmount() {
        return this.formattedAmount;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("amt", this.amount);
        object.addProperty("fmtd_amt", this.formattedAmount);
        return object;
    }
}
