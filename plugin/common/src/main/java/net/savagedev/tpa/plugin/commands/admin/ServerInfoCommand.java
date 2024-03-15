package net.savagedev.tpa.plugin.commands.admin;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerInfoCommand implements BungeeTpCommand {
    private final BungeeTpPlugin plugin;

    public ServerInfoCommand(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        if (args.length == 1) {
            Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", "bungeetp serverinfo <serverId>"));
            return;
        }

        // TODO: Default values to "unknown."
        this.plugin.getServerManager().get(args[1]).ifPresent(server -> {
            player.sendMessage("&aServer Id: " + server.getId());
            player.sendMessage("&aServer Software: " + server.getServerSoftware());
            player.sendMessage("&aServer Bridge Version: " + server.getBridgeVersion());
            player.sendMessage("&aEconomy Support: " + (server.hasNoEconomy() ? "&cNo" : "Yes") + "&a.");
        });
    }

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        final Set<String> allServerIds = this.plugin.getServerManager().getAll().keySet();

        if (args.length == 1) {
            return allServerIds;
        }

        final String partialServerId = args[1].toLowerCase(Locale.ROOT);

        return allServerIds.stream()
                .filter(serverId -> serverId.startsWith(partialServerId))
                .collect(Collectors.toSet());
    }
}
