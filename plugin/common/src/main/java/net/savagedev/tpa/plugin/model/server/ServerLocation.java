package net.savagedev.tpa.plugin.model.server;

public class ServerLocation {
    private final Server<?> server;

    private final String worldName;

    private final float x, y, z;
    private final float pitch, yaw;

    public ServerLocation(Server<?> server, String worldName, float x, float y, float z, float pitch, float yaw) {
        this.server = server;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Server<?> getServer() {
        return this.server;
    }

    public String getWorldName() {
        return this.worldName;
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
}
