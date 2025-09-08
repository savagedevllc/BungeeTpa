package net.savagedev.tpa.bridge.model;

import net.savagedev.tpa.common.messaging.messages.Message;

import java.io.IOException;
import java.util.UUID;

public interface BungeeTpPlayer extends TeleportTarget {
    void sendData(Message message) throws IOException;

    void teleportTo(BungeeTpPlayer target);

    void teleportTo(Location location);

    void sendMessage(String message);

    UUID getUniqueId();
}
