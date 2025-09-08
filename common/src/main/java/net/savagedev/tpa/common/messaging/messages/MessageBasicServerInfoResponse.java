package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class MessageBasicServerInfoResponse extends Message {
    public static MessageBasicServerInfoResponse deserialize(JsonObject object) {
        return new MessageBasicServerInfoResponse(object.get("software_name").getAsString(),
                object.get("bridge_version").getAsString(),
                object.get("economy_support").getAsBoolean(),
                object.get("whitelist_enabled").getAsBoolean(),
                object.getAsJsonArray("worlds").getAsJsonArray().asList()
                        .stream()
                        .map(JsonElement::getAsString).collect(Collectors.toSet()),
                object.getAsJsonArray("whitelist").getAsJsonArray().asList()
                        .stream()
                        .map(o -> UUID.fromString(o.getAsString())).collect(Collectors.toSet()));
    }

    private final boolean economySupport;
    private final boolean whitelistEnabled;

    private final String bridgeVersion;

    private final String softwareName;

    private final Collection<String> worlds = new HashSet<>();

    private final Collection<UUID> whitelist = new HashSet<>();

    public MessageBasicServerInfoResponse(String softwareName, String bridgeVersion, boolean economySupport, boolean whitelistEnabled, Collection<String> worlds, Collection<UUID> whitelist) {
        this.softwareName = softwareName;
        this.bridgeVersion = bridgeVersion;
        this.economySupport = economySupport;
        this.whitelistEnabled = whitelistEnabled;
        this.worlds.addAll(worlds);
        this.whitelist.addAll(whitelist);
    }

    public boolean hasEconomySupport() {
        return this.economySupport;
    }

    public boolean isWhitelisted() {
        return this.whitelistEnabled;
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

    public Collection<UUID> getWhitelist() {
        return this.whitelist;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("software_name", this.softwareName);
        object.addProperty("bridge_version", this.bridgeVersion);
        object.addProperty("economy_support", this.economySupport);
        object.addProperty("whitelist_enabled", this.whitelistEnabled);

        final JsonArray worlds = new JsonArray();
        this.worlds.forEach(worlds::add);
        object.add("worlds", worlds);

        final JsonArray whitelist = new JsonArray();
        this.whitelist.forEach(uuid -> whitelist.add(uuid.toString()));
        object.add("whitelist", whitelist);

        return object;
    }
}
