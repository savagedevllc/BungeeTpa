package net.savagedev.tpa.bridge.listener;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.common.messaging.messages.MessageWhitelistInfo;
import net.savagedev.tpa.common.messaging.messages.MessageWhitelistInfo.Action;

import java.util.UUID;

public class AbstractWhitelistListener {
    private final BungeeTpBridgePlatform platform;

    private boolean lastKnownWhitelistState;

    public AbstractWhitelistListener(BungeeTpBridgePlatform platform) {
        this.platform = platform;

        this.lastKnownWhitelistState = platform.isWhitelisted();
    }

    protected void handleWhitelistAddEvent(UUID uniqueId) {
        this.platform.getMessenger().sendData(new MessageWhitelistInfo(this.platform.isWhitelisted(), Action.ADD, uniqueId));
    }

    protected void handleWhitelistRemoveEvent(UUID uniqueId) {
        this.platform.getMessenger().sendData(new MessageWhitelistInfo(this.platform.isWhitelisted(), Action.REMOVE, uniqueId));
    }

    protected void handleWhitelistStatusChange(boolean newStatus) {
        // We only want to send a status change if the whitelist status actually changed.
        if (newStatus != this.lastKnownWhitelistState) {
            this.platform.getMessenger().sendData(new MessageWhitelistInfo(newStatus, Action.STATUS_CHANGE, null));
            this.lastKnownWhitelistState = newStatus;
        }
    }
}
