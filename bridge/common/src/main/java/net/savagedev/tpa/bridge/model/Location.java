package net.savagedev.tpa.bridge.model;

public class Location {
    private final String worldName;

    private final float x, y, z;


    public Location(String worldName, float x, float y, float z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
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
}
