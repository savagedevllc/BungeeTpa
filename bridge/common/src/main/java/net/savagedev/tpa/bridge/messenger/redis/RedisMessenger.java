package net.savagedev.tpa.bridge.messenger.redis;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.messenger.BungeeTpBridgeMessenger;
import net.savagedev.tpa.common.messaging.messages.Message;

/*
 * Yes, I know it's bizarre and isn't D.R.Y. compliant to have each platform to have its own implementation of Redis/RabbitMQ messenger.
 * It's only structured like this since each platform only has "decoder functions" that are necessary for its respective platform.
 */
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
