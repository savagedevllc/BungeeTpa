package net.savagedev.tpa.plugin.model.request;

import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Setting;
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

    public void deny() {
        // Here we don't have to worry about a race condition because the request was already removed.
        if (this.isPaid()) {
            this.getSender().deposit(Setting.TELEPORT_COST.asFloat());
        }

        if (this.getDirection() == TeleportRequest.Direction.TO_RECEIVER) {
            Lang.TPA_REQUEST_DENIED.send(this.getSender(), new Lang.Placeholder("%player%", this.getReceiver().getName()));
            Lang.TPA_REQUEST_DENY.send(this.getReceiver(), new Lang.Placeholder("%player%", this.getSender().getName()));
        } else {
            Lang.TPA_HERE_REQUEST_DENIED.send(this.getSender(), new Lang.Placeholder("%player%", this.getReceiver().getName()));
            Lang.TPA_HERE_REQUEST_DENY.send(this.getReceiver(), new Lang.Placeholder("%player%", this.getSender().getName()));
        }
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
