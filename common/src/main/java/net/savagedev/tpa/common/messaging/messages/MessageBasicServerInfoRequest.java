package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

public class MessageBasicServerInfoRequest extends Message {
    public static MessageBasicServerInfoRequest deserialize(JsonObject ignored) {
        return new MessageBasicServerInfoRequest();
    }

    @Override
    protected JsonObject asJsonObject() {
        return new JsonObject();
    }
}
