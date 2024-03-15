package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

import java.util.Optional;

public class TpCancelCommand implements BungeeTpCommand {
    private final BungeeTpPlugin plugin;

    public TpCancelCommand(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        final Optional<TeleportRequest> optionalRequest;
        if (args.length == 0) {
            optionalRequest = this.plugin.getTeleportManager().popMostRecentSentRequest(player);
        } else {
            final Optional<ProxyPlayer<?, ?>> optionalReceiver = this.plugin.getPlayer(args[0]);

            if (!optionalReceiver.isPresent()) {
                Lang.UNKNOWN_PLAYER.send(player, new Placeholder("%player%", args[0]));
                return;
            }

            optionalRequest = this.plugin.getTeleportManager().popRequestBySender(optionalReceiver.get(), player);
        }

        if (!optionalRequest.isPresent()) {
            Lang.NO_REQUESTS.send(player);
            return;
        }

        final TeleportRequest request = optionalRequest.get();
        final ProxyPlayer<?, ?> receiver = request.getReceiver();

        if (receiver.isConnected()) {
            Lang.REQUEST_CANCELLED_RECEIVER.send(receiver, new Placeholder("%player%", request.getSender().getName()));
        }
        player.getCurrentServer().formatCurrency(Setting.TELEPORT_COST.asFloat()).whenComplete((formattedAmount, ignored) ->
                Lang.REQUEST_CANCELLED_SENDER.send(request.getSender(),
                        new Placeholder("%player%", receiver.getName()),
                        new Placeholder("%amount%", formattedAmount))
        );

        // To avoid a race condition, we cancel the teleport request before requesting a refund.
        if (request.isPaid()) {
            player.deposit(Setting.TELEPORT_COST.asFloat());
        }
    }
}
