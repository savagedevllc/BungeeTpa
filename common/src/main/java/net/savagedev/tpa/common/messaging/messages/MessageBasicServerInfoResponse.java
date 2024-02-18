package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

public class MessageBasicServerInfoResponse extends Message {
    public static MessageBasicServerInfoResponse deserialize(JsonObject object) {
        return new MessageBasicServerInfoResponse(object.get("software_name").getAsString(), object.get("economy_support").getAsBoolean());
    }

    private final boolean economySupport;

    private final String softwareName;

    public MessageBasicServerInfoResponse(String softwareName, boolean economySupport) {
        this.softwareName = softwareName;
        this.economySupport = economySupport;
    }

    public boolean hasEconomySupport() {
        return this.economySupport;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("software_name", this.softwareName);
        object.addProperty("economy_support", this.economySupport);
        return object;
    }
}
