package net.savagedev.tpa.plugin.model.request;

import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

public class TeleportRequest {
    private final long timeSent = System.currentTimeMillis();

    private final ProxyPlayer<?, ?> sender;
    private final ProxyPlayer<?, ?> receiver;

    private final Direction direction;

    private final boolean paid;

    public TeleportRequest(ProxyPlayer<?, ?> sender, ProxyPlayer<?, ?> receiver, Direction direction, boolean paid) {
        this.sender = sender;
        this.receiver = receiver;
        this.direction = direction;
        this.paid = paid;
    }

    public ProxyPlayer<?, ?> getSender() {
        return this.sender;
    }

    public ProxyPlayer<?, ?> getReceiver() {
        return this.receiver;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public long getTimeSent() {
        return this.timeSent;
    }

    public boolean isPaid() {
        return this.paid;
    }

    public enum Direction {
        TO_SENDER,
        TO_RECEIVER
    }
}
