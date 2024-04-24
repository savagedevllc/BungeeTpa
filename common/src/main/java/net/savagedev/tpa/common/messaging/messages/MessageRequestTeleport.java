package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.Optional;
import java.util.UUID;

public class MessageRequestTeleport extends Message {
    public static MessageRequestTeleport deserialize(JsonObject object) {
        final long requesterMostSigBits = object.get("req_msb").getAsLong();
        final long requesterLeastSigBits = object.get("req_lsb").getAsLong();
        final UUID requester = new UUID(requesterMostSigBits, requesterLeastSigBits);

        final TeleportType type = TeleportType.values()[object.get("type").getAsInt()];

        final MessageRequestTeleport requestMessage;
        if (type == TeleportType.COORDINATES) {
            final String worldName = object.get("world").getAsString();
            final float x = object.get("x").getAsFloat();
            final float y = object.get("y").getAsFloat();
            final float z = object.get("z").getAsFloat();

            requestMessage = new MessageRequestTeleport(requester, type, worldName, x, y, z);
        } else {
            final long receiverMostSigBits = object.get("rec_msb").getAsLong();
            final long receiverLeastSigBits = object.get("rec_lsb").getAsLong();
            final UUID receiver = new UUID(receiverMostSigBits, receiverLeastSigBits);

            requestMessage = new MessageRequestTeleport(requester, type, receiver);
        }
        requestMessage.setTeleportTime(TeleportTime.values()[object.get("time").getAsInt()]);
        return requestMessage;
    }

    private final TeleportType teleportType;
    private final UUID requester;

    private String worldName;
    private float x, y, z;
    private UUID receiver;

    private TeleportTime teleportTime;

    public MessageRequestTeleport(UUID requester, TeleportType teleportType, UUID receiver) {
        this.requester = requester;
        this.teleportType = teleportType;
        this.receiver = receiver;
    }

    public MessageRequestTeleport(UUID requester, TeleportType teleportType, String worldName, float x, float y, float z) {
        this.requester = requester;
        this.teleportType = teleportType;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setTeleportTime(TeleportTime teleportTime) {
        this.teleportTime = teleportTime;
    }

    public UUID getRequester() {
        return this.requester;
    }

    public Optional<UUID> getReceiver() {
        return Optional.ofNullable(this.receiver);
    }

    public Optional<String> getWorldName() {
        return Optional.ofNullable(this.worldName);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public TeleportTime getTeleportTime() {
        return this.teleportTime;
    }

    public TeleportType getTeleportType() {
        return this.teleportType;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("req_msb", this.requester.getMostSignificantBits());
        object.addProperty("req_lsb", this.requester.getLeastSignificantBits());
        object.addProperty("rec_msb", this.receiver.getMostSignificantBits());
        object.addProperty("rec_lsb", this.receiver.getLeastSignificantBits());
        object.addProperty("type", this.teleportType.ordinal());
        object.addProperty("time", this.teleportTime.ordinal());
        object.addProperty("world", this.worldName);
        object.addProperty("x", this.x);
        object.addProperty("y", this.y);
        object.addProperty("z", this.z);
        return object;
    }

    public enum TeleportType {
        COORDINATES,
        PLAYER_LOCATION
    }

    public enum TeleportTime {
        ON_JOIN,
        INSTANT
    }
}
