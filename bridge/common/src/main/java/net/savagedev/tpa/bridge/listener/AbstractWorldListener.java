package net.savagedev.tpa.bridge.listener;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.common.messaging.messages.MessageWorldInfo;
import net.savagedev.tpa.common.messaging.messages.MessageWorldInfo.Action;

public class AbstractWorldListener {
    private final BungeeTpBridgePlatform platform;

    public AbstractWorldListener(BungeeTpBridgePlatform platform) {
        this.platform = platform;
    }

    protected void handleWorldLoadEvent(String worldId) {
        this.platform.getMessenger().sendData(new MessageWorldInfo(Action.ADD, worldId));
    }

    protected void handleWorldUnloadEvent(String worldId) {
        this.platform.getMessenger().sendData(new MessageWorldInfo(Action.REMOVE, worldId));
    }
}
