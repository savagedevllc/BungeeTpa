package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public abstract class AbstractMessageRequestTeleport extends Message {
    private Type type = Type.ON_JOIN;

    private final UUID requester;

    public AbstractMessageRequestTeleport(UUID requester) {
        this.requester = requester;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public UUID getRequester() {
        return this.requester;
    }

    public Type getType() {
        return this.type;
    }

    protected void insertSerializedData(JsonObject object) {
        object.addProperty("req_msb", this.requester.getMostSignificantBits());
        object.addProperty("req_lsb", this.requester.getLeastSignificantBits());
        object.addProperty("t", this.type.ordinal());
    }

    public enum Type {
        ON_JOIN,
        INSTANT
    }
}
