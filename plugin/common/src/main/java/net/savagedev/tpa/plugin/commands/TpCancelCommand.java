package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class TpCancelCommand implements BungeeTpCommand {
    private final BungeeTpPlugin plugin;

    public TpCancelCommand(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        // TODO: Fix this, I need to implements some kind of sent request stack for the senders to keep track of the order in which requests are sent out.
        //       popMostRecentRequest only works on most recent RECEIVED requests, not sent, and without doing a lot of heavy math, there's really no way (That I can think of)
        //       to find the player's most recent sent request.
        final Optional<TeleportRequest> optionalRequest = this.plugin.getTeleportManager().popMostRecentRequest(player);

        if (!optionalRequest.isPresent()) {
            Lang.NO_REQUESTS.send(player);
            return;
        }

        final TeleportRequest request = optionalRequest.get();
        final ProxyPlayer<?, ?> receiver = request.getReceiver();

        if (receiver.isConnected()) {
            Lang.REQUEST_CANCELLED_RECEIVER.send(receiver, new Placeholder("%player%", request.getSender().getName()));
        }
        Lang.REQUEST_CANCELLED_SENDER.send(request.getSender(), new Placeholder("%player%", receiver.getName()));

        // To avoid a race condition, we cancel the teleport request before requesting a refund.
        if (request.isPaid()) {
            player.deposit(Setting.TELEPORT_COST.asFloat());
        }
    }

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        return Collections.emptyList();
    }
}
