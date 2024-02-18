package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessageRequestTeleport extends Message {
    public static MessageRequestTeleport deserialize(JsonObject object) {
        final UUID requester = UUID.fromString(object.get("requester").getAsString());
        final UUID receiver = UUID.fromString(object.get("receiver").getAsString());

        final MessageRequestTeleport requestMessage = new MessageRequestTeleport(requester, receiver);
        requestMessage.setType(Type.valueOf(object.get("type").getAsString()));
        return requestMessage;
    }


    private final UUID requester;
    private final UUID receiver;

    private Type type;

    public MessageRequestTeleport(UUID requester, UUID receiver) {
        this.requester = requester;
        this.receiver = receiver;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public UUID getRequester() {
        return this.requester;
    }

    public UUID getReceiver() {
        return this.receiver;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("requester", this.requester.toString());
        object.addProperty("receiver", this.receiver.toString());
        object.addProperty("type", this.type.name());
        return object;
    }

    public enum Type {
        ON_JOIN,
        INSTANT
    }
}
