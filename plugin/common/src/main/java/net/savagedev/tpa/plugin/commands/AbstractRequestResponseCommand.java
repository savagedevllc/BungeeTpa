package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

import java.util.Collection;

public abstract class AbstractRequestResponseCommand implements BungeeTpCommand {
    protected final BungeeTpPlugin plugin;

    public AbstractRequestResponseCommand(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        // It's safe to remove the request at this point, since we know for a fact we're acting on the request in on way or another.
        final TeleportRequest request = this.plugin.getTeleportManager().removeRequest(player);

        if (request == null) {
            Lang.NO_REQUESTS.send(player);
            return;
        }

        if (!request.getSender().isConnected()) {
            Lang.PLAYER_OFFLINE.send(player, new Lang.Placeholder("%player%", request.getSender().getName()));
            return;
        }

        this.respond(request);
    }

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        return null;
    }

    protected abstract void respond(TeleportRequest request);
}
