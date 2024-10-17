package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessageRequestTeleportPlayer extends AbstractMessageRequestTeleport {
    public static MessageRequestTeleportPlayer deserialize(JsonObject object) {
        final long requesterMostSigBits = object.get("req_msb").getAsLong();
        final long requesterLeastSigBits = object.get("req_lsb").getAsLong();
        final UUID requester = new UUID(requesterMostSigBits, requesterLeastSigBits);

        final long senderMostSigBits = object.get("rec_msb").getAsLong();
        final long senderLeastSigBits = object.get("rec_lsb").getAsLong();
        final UUID receiver = new UUID(senderMostSigBits, senderLeastSigBits);

        final MessageRequestTeleportPlayer requestMessage = new MessageRequestTeleportPlayer(requester, receiver);
        requestMessage.setType(Type.values()[object.get("t").getAsInt()]);
        return requestMessage;
    }


    private final UUID receiver;

    public MessageRequestTeleportPlayer(UUID requester, UUID receiver) {
        super(requester);
        this.receiver = receiver;
    }

    public UUID getReceiver() {
        return this.receiver;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();

        object.addProperty("rec_msb", this.receiver.getMostSignificantBits());
        object.addProperty("rec_lsb", this.receiver.getLeastSignificantBits());
        super.insertSerializedData(object);

        return object;
    }
}
