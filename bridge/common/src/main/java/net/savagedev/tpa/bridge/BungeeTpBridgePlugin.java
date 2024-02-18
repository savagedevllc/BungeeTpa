package net.savagedev.tpa.bridge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BungeeTpBridgePlugin {
    private final BungeeTpBridgePlatform platform;

    private Map<UUID, UUID> tpCache;

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

    public Map<UUID, UUID> getTpCache() {
        return this.tpCache;
    }
}
