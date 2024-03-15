package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractRequestResponseCommand implements BungeeTpCommand {
    protected final BungeeTpPlugin plugin;

    public AbstractRequestResponseCommand(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        final Optional<TeleportRequest> optionalRequest;
        if (args.length == 0) {
            // It's safe to remove the request at this point, since we know for a fact we're acting on the request in on way or another.
            optionalRequest = this.plugin.getTeleportManager().popMostRecentRequest(player);
        } else {
            final Optional<ProxyPlayer<?, ?>> sender = this.plugin.getPlayer(args[0]);

            if (!sender.isPresent()) {
                Lang.UNKNOWN_PLAYER.send(player, new Placeholder("%player%", args[0]));
                return;
            }

            optionalRequest = this.plugin.getTeleportManager().popRequestBySender(player, sender.get());

            if (!optionalRequest.isPresent()) {
                Lang.NO_REQUEST_FROM.send(player, new Placeholder("%player%", sender.get().getName()));
                return;
            }
        }

        if (!optionalRequest.isPresent()) {
            Lang.NO_REQUESTS.send(player);
            return;
        }

        final TeleportRequest request = optionalRequest.get();

        if (!request.getSender().isConnected()) {
            Lang.PLAYER_OFFLINE.send(player, new Lang.Placeholder("%player%", request.getSender().getName()));
            return;
        }

        this.respond(request);
    }

    protected abstract void respond(TeleportRequest request);

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        final Set<String> usernames = this.plugin.getTeleportManager().getRequestStack(player)
                .stream()
                .map(request -> request.getSender().getName())
                .collect(Collectors.toSet());

        if (args.length == 0) {
            return usernames;
        }

        final Set<String> completions = new HashSet<>();
        for (String username : usernames) {
            if (username.toLowerCase().startsWith(args[0].toLowerCase())) {
                completions.add(username);
            }
        }
        return completions;
    }
}
