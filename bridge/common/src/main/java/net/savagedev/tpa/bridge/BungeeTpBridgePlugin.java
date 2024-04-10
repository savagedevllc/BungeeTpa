package net.savagedev.tpa.bridge;

import net.savagedev.tpa.bridge.model.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BungeeTpBridgePlugin {
    private final BungeeTpBridgePlatform platform;

    private Map<UUID, Location> tpCache;

    public BungeeTpBridgePlugin(BungeeTpBridgePlatform platform) {
        this.platform = platform;
    }

    public void enable() {
        this.tpCache = new HashMap<>(this.platform.getMaxPlayers());
        this.platform.getMessenger().init();
    }

    public void disable() {
        this.platform.getMessenger().shutdown();
    }

    public Map<UUID, Location> getTpCache() {
        return this.tpCache;
    }
}
