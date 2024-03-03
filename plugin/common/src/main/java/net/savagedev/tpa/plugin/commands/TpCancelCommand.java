package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
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
        final Optional<TeleportRequest> optionalRequest = this.plugin.getTeleportManager().removeRequestBySender(player);

        if (!optionalRequest.isPresent()) {
            Lang.NO_REQUESTS.send(player);
            return;
        }

        player.deposit(100.0d).whenComplete((response, err) -> {
            if (err != null) {
                return;
            }

            final ProxyPlayer<?, ?> receiver = optionalRequest.get().getReceiver();
            if (receiver.isConnected()) {
                Lang.REQUEST_CANCELLED_RECEIVER.send(receiver, new Placeholder("%player%", player.getName()));
            }
            Lang.REQUEST_CANCELLED_SENDER.send(player, new Placeholder("%player%", receiver.getName()));
        });
    }

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        return Collections.emptyList();
    }
}
