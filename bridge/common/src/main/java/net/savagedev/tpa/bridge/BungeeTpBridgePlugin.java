package net.savagedev.tpa.bridge;

import net.savagedev.tpa.bridge.messenger.Messenger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BungeeTpBridgePlugin {
    private final BungeeTpBridgePlatform platform;

    private Map<UUID, UUID> tpCache;
    private Messenger messenger;

    public BungeeTpBridgePlugin(BungeeTpBridgePlatform platform) {
        this.platform = platform;
    }

    public void enable() {
        this.tpCache = new HashMap<>(this.platform.getMaxPlayers());

        this.messenger = this.platform.getPlatformMessenger();
        this.messenger.init();
    }

    public void disable() {
        this.messenger.shutdown();
    }

    public Map<UUID, UUID> getTpCache() {
        return this.tpCache;
    }
}
