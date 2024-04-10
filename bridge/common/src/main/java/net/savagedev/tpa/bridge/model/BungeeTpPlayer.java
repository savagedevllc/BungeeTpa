package net.savagedev.tpa.bridge.model;

import net.savagedev.tpa.common.messaging.messages.Message;

import java.io.IOException;
import java.util.UUID;

public interface BungeeTpPlayer {
    void sendData(Message message) throws IOException;

    void teleportTo(Location location);

    void sendMessage(String message);

    UUID getUniqueId();

    Location getLocation();
}
