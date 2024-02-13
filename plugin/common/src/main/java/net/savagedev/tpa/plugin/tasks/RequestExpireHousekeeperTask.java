package net.savagedev.tpa.plugin.tasks;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

import java.util.concurrent.TimeUnit;

public class RequestExpireHousekeeperTask implements Runnable {
    private final BungeeTpPlugin plugin;

    public RequestExpireHousekeeperTask(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (TeleportRequest request : this.plugin.getTeleportManager().getAllRequests()) {
            final long elapsed = System.currentTimeMillis() - request.getTimeSent();
            if (TimeUnit.MILLISECONDS.toSeconds(elapsed) >= Setting.TP_REQUEST_EXPIRE.asLong()) {
                final TeleportRequest expiredRequest = this.plugin.getTeleportManager().removeRequest(request.getReceiver());
                final ProxyPlayer<?, ?> sender = expiredRequest.getSender();
                final ProxyPlayer<?, ?> receiver = expiredRequest.getReceiver();

                if (sender.isConnected()) {
                    Lang.REQUEST_EXPIRED_TO.send(sender, new Placeholder("%player%", receiver.getName()));
                }
                if (receiver.isConnected()) {
                    Lang.REQUEST_EXPIRED_FROM.send(receiver, new Placeholder("%player%", sender.getName()));
                }
            }
        }
    }
}
