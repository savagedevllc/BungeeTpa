package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessagePlayerInfo extends Message {
    public static MessagePlayerInfo deserialize(JsonObject object) {
        final long mostSigBits = object.get("msb").getAsLong();
        final long leastSigBits = object.get("lsb").getAsLong();
        return new MessagePlayerInfo(new UUID(mostSigBits, leastSigBits), object.get("hidden").getAsBoolean());
    }

    private final UUID uniqueId;

    private final boolean hidden;

    public MessagePlayerInfo(UUID uniqueId, boolean hidden) {
        this.uniqueId = uniqueId;
        this.hidden = hidden;
    }

    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("msb", this.uniqueId.getMostSignificantBits());
        object.addProperty("lsb", this.uniqueId.getLeastSignificantBits());
        object.addProperty("hidden", this.hidden);
        return object;
    }
}
