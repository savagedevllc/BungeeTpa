package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessageRequestTeleport extends Message {
    public static MessageRequestTeleport deserialize(JsonObject object) {
        final long requesterMostSigBits = object.get("req_msb").getAsLong();
        final long requesterLeastSigBits = object.get("req_lsb").getAsLong();
        final UUID requester = new UUID(requesterMostSigBits, requesterLeastSigBits);

        final long senderMostSigBits = object.get("rec_msb").getAsLong();
        final long senderLeastSigBits = object.get("rec_lsb").getAsLong();
        final UUID receiver = new UUID(senderMostSigBits, senderLeastSigBits);

        final MessageRequestTeleport requestMessage = new MessageRequestTeleport(requester, receiver);
        requestMessage.setType(Type.values()[object.get("t").getAsInt()]);
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
        object.addProperty("req_msb", this.requester.getMostSignificantBits());
        object.addProperty("req_lsb", this.requester.getLeastSignificantBits());
        object.addProperty("rec_msb", this.receiver.getMostSignificantBits());
        object.addProperty("rec_lsb", this.receiver.getLeastSignificantBits());
        object.addProperty("t", this.type.ordinal());
        return object;
    }

    public enum Type {
        ON_JOIN,
        INSTANT
    }
}
