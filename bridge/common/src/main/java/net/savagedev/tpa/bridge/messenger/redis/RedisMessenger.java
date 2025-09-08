package net.savagedev.tpa.bridge.messenger.redis;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.messenger.BungeeTpBridgeMessenger;
import net.savagedev.tpa.common.messaging.messages.Message;

// Hold off on this for now - it isn't necessary. We got bigger fish to fry.
public class RedisMessenger extends BungeeTpBridgeMessenger<Void> {
    public RedisMessenger(BungeeTpBridgePlatform platform) {
        super(platform);
    }

    @Override
    public void init() {
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void sendData(Void unused, Message message) {
    }
}
