package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

public class MessageBasicServerInfoResponse extends Message {
    public static MessageBasicServerInfoResponse deserialize(JsonObject object) {
        return new MessageBasicServerInfoResponse(object.get("software_name").getAsString(), object.get("bridge_version").getAsString(), object.get("economy_support").getAsBoolean());
    }

    private final boolean economySupport;

    private final String bridgeVersion;

    private final String softwareName;

    public MessageBasicServerInfoResponse(String softwareName, String bridgeVersion, boolean economySupport) {
        this.softwareName = softwareName;
        this.bridgeVersion = bridgeVersion;
        this.economySupport = economySupport;
    }

    public boolean hasEconomySupport() {
        return this.economySupport;
    }

    public String getBridgeVersion() {
        return this.bridgeVersion;
    }

    public String getSoftwareName() {
        return this.softwareName;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("software_name", this.softwareName);
        object.addProperty("bridge_version", this.bridgeVersion);
        object.addProperty("economy_support", this.economySupport);
        return object;
    }
}
