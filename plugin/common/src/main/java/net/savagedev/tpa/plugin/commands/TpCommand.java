package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.Optional;

public class TpCommand implements BungeeTpCommand {
    private final BungeeTpPlugin plugin;

    public TpCommand(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        if (args.length == 0) {
            Lang.INVALID_ARGUMENTS.send(player, new Lang.Placeholder("%command%", "tp <player|server> [[world] <x> <y> <z>]"));
            return;
        }

        final String targetId = args[0];

        final Optional<ProxyPlayer<?, ?>> optionalTargetPlayer = this.plugin.getPlayer(targetId);

        if (optionalTargetPlayer.isPresent()) {
            // Great! We know it's a player they're after... Or do we?
            return;
        }

        Lang.TELEPORTING.send(player, new Lang.Placeholder("%player%", other.getName()));
        this.plugin.getTeleportManager().teleportAsync(player, other);
    }
}
