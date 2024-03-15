package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;

public class TpDenyAllCommand implements BungeeTpCommand {
    private final BungeeTpPlugin plugin;

    public TpDenyAllCommand(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        final Deque<TeleportRequest> requestStack = this.plugin.getTeleportManager().getRequestStack(player);

        if (requestStack.isEmpty()) {
            Lang.NO_REQUESTS.send(player);
            return;
        }

        for (TeleportRequest request : requestStack) {
            request.deny();
        }

        this.plugin.getTeleportManager().clearRequestStack(player);
    }

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        return Collections.emptySet();
    }
}
