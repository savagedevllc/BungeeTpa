package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

import java.util.UUID;

public class MessageRequestTeleportCoords extends AbstractMessageRequestTeleport {
    public static MessageRequestTeleportCoords deserialize(JsonObject object) {
        final long requesterMostSigBits = object.get("req_msb").getAsLong();
        final long requesterLeastSigBits = object.get("req_lsb").getAsLong();
        final UUID requester = new UUID(requesterMostSigBits, requesterLeastSigBits);

        String worldName = null;
        if (object.has("world")) {
            worldName = object.get("world").getAsString();
        }
        final float x = object.get("x").getAsFloat();
        final float y = object.get("y").getAsFloat();
        final float z = object.get("z").getAsFloat();

        final float pitch = object.get("pitch").getAsFloat();
        final float yaw = object.get("yaw").getAsFloat();

        final MessageRequestTeleportCoords requestMessage = new MessageRequestTeleportCoords(requester, worldName, x, y, z, pitch, yaw);
        requestMessage.setType(Type.values()[object.get("t").getAsInt()]);
        return requestMessage;
    }

    private final float x, y, z;
    private final float pitch, yaw;

    private final String worldName;

    public MessageRequestTeleportCoords(UUID requester, String worldName, float x, float y, float z, float pitch, float yaw) {
        super(requester);
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
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

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public String getWorldName() {
        return this.worldName;
    }

    @Override
    protected JsonObject asJsonObject() {
        final JsonObject object = new JsonObject();

        if (this.worldName != null) {
            object.addProperty("world", this.worldName);
        }
        object.addProperty("x", this.x);
        object.addProperty("y", this.y);
        object.addProperty("z", this.z);
        object.addProperty("pitch", this.pitch);
        object.addProperty("yaw", this.yaw);
        super.insertSerializedData(object);

        return object;
    }
}
