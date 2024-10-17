package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class MessageBasicServerInfoResponse extends Message {
    public static MessageBasicServerInfoResponse deserialize(JsonObject object) {
        return new MessageBasicServerInfoResponse(object.get("software_name").getAsString(),
                object.get("bridge_version").getAsString(),
                object.get("economy_support").getAsBoolean(),
                object.getAsJsonArray("worlds").getAsJsonArray().asList()
                        .stream()
                        .map(JsonElement::getAsString).collect(Collectors.toSet()));
    }

    private final boolean economySupport;

    private final String bridgeVersion;

    private final String softwareName;

    private final Collection<String> worlds = new HashSet<>();

    public MessageBasicServerInfoResponse(String softwareName, String bridgeVersion, boolean economySupport, Collection<String> worlds) {
        this.softwareName = softwareName;
        this.bridgeVersion = bridgeVersion;
        this.economySupport = economySupport;
        this.worlds.addAll(worlds);
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

    public Collection<String> getWorlds() {
        return this.worlds;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("software_name", this.softwareName);
        object.addProperty("bridge_version", this.bridgeVersion);
        object.addProperty("economy_support", this.economySupport);

        final JsonArray worlds = new JsonArray();
        this.worlds.forEach(worlds::add);
        object.add("worlds", worlds);

        return object;
    }
}
