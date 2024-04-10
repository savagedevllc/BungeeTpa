package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.Optional;
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
        requestMessage.setTeleportTime(TeleportTime.values()[object.get("t").getAsInt()]);
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
