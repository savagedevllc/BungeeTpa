package net.savagedev.tpa.bridge.model;

import java.util.UUID;

public interface BungeeTpPlayer {
    void teleportTo(BungeeTpPlayer target);

    void sendMessage(String message);

    UUID getUniqueId();
}
