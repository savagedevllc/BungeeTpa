package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessagePlayerInfo implements Message {
    public static MessagePlayerInfo deserialize(JsonObject object) {
        return new MessagePlayerInfo(UUID.fromString(object.get("uuid").getAsString()), object.get("hidden").getAsBoolean());
    }

    private final UUID uuid;

    private final boolean hidden;

    public MessagePlayerInfo(UUID uuid, boolean hidden) {
        this.uuid = uuid;
        this.hidden = hidden;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public JsonObject serialize() {
        final JsonObject object = new JsonObject();
        object.addProperty("uuid", this.uuid.toString());
        object.addProperty("hidden", this.hidden);
        return object;
    }
}
