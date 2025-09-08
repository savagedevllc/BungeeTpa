package net.savagedev.tpa.bridge.model;

import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleportCoords;

import java.util.Optional;

public class Location implements TeleportTarget {
    public static Location fromMessage(MessageRequestTeleportCoords requestMessage) {
        return new Location(requestMessage.getWorldName(), requestMessage.getX(), requestMessage.getY(),
                requestMessage.getZ(), requestMessage.getYaw(), requestMessage.getPitch());
    }

    private final String worldName;

    private final float x, y, z;
    private final float yaw, pitch;

    public Location(String worldName, float x, float y, float z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
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

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    @Override
    public void teleportHere(BungeeTpPlayer player) {
        player.teleportTo(this);
    }

    @Override
    public String toString() {
        return this.getWorldName().orElse("unknown;") + this.x + "," + this.y + "," + this.z;
    }
}
