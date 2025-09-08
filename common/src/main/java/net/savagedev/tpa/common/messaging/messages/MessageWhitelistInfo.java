package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessageWhitelistInfo extends Message {
    public static MessageWhitelistInfo deserialize(JsonObject object) {
        UUID uniqueId = null;
        if (!object.has("msb") && object.has("lsb")) {
            final long mostSigBits = object.get("msb").getAsLong();
            final long leastSigBits = object.get("lsb").getAsLong();
            uniqueId = new UUID(mostSigBits, leastSigBits);
        }
        return new MessageWhitelistInfo(object.get("is_active").getAsBoolean(), Action.valueOf(object.get("a").getAsString()), uniqueId);
    }

    private boolean active;

    private final UUID uniqueId;

    private final Action action;

    public MessageWhitelistInfo(boolean active, Action action, UUID uniqueId) {
        this.active = active;
        this.uniqueId = uniqueId;
        this.action = action;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public Action getAction() {
        return this.action;
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();

        if (this.uniqueId != null) {
            object.addProperty("msb", this.uniqueId.getMostSignificantBits());
            object.addProperty("lsb", this.uniqueId.getLeastSignificantBits());
        }

        object.addProperty("is_active", this.active);
        object.addProperty("a", this.action.ordinal());
        return object;
    }

    public enum Action {
        ADD,
        REMOVE,
        STATUS_CHANGE
    }
}
