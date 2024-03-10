package net.savagedev.tpa.plugin.tasks;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RequestExpireHousekeeperTask implements Runnable {
    private final BungeeTpPlugin plugin;

    public RequestExpireHousekeeperTask(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (TeleportRequest request : this.plugin.getTeleportManager().aggregateRequests()) {
            final long elapsed = System.currentTimeMillis() - request.getTimeSent();
            if (TimeUnit.MILLISECONDS.toSeconds(elapsed) >= Setting.TP_REQUEST_EXPIRE.asLong()) {
                final Optional<TeleportRequest> expiredRequest = this.plugin.getTeleportManager().popRequestBySender(request.getReceiver(), request.getSender());
                if (!expiredRequest.isPresent()) {
                    continue;
                }

                final ProxyPlayer<?, ?> sender = expiredRequest.get().getSender();

                // This should be safe because if the sender disconnects from the server, the request is removed and they are refunded.
                // So we can safely assume that the sender is still online at this point, and that they haven't been refunded yet.
                if (request.isPaid()) {
                    sender.deposit(Setting.TELEPORT_COST.asFloat());
                }

                final ProxyPlayer<?, ?> receiver = expiredRequest.get().getReceiver();
                if (receiver.isConnected()) {
                    Lang.REQUEST_EXPIRED_FROM.send(receiver, new Placeholder("%player%", sender.getName()));
                }
                Lang.REQUEST_EXPIRED_TO.send(sender, new Placeholder("%player%", receiver.getName()));
            }
        }
    }
}
