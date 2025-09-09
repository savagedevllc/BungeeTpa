package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

public class MessageWorldInfo extends Message {
    public static MessageWorldInfo deserialize(JsonObject object) {
        return new MessageWorldInfo(Action.values()[object.get("a").getAsInt()], object.get("n").getAsString());
    }

    private final String worldId;

    private final Action action;

    public MessageWorldInfo(Action action, String worldId) {
        this.worldId = worldId;
        this.action = action;
    }

    public String getWorldId() {
        return this.worldId;
    }

    public Action getAction() {
        return this.action;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("a", this.action.ordinal());
        object.addProperty("n", this.worldId);
        return object;
    }

    public enum Action {
        ADD,
        REMOVE
    }
}
