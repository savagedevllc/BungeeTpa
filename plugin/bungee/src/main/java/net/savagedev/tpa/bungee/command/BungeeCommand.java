package net.savagedev.tpa.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

public class BungeeCommand extends Command implements TabExecutor {
    private final BungeeTpCommand command;
    private final BungeeTpPlugin plugin;

    public BungeeCommand(BungeeTpCommand command, BungeeTpPlugin plugin, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.command = command;
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        final ProxyPlayer<?, ?> player = this.plugin.getPlayer(((ProxiedPlayer) sender).getUniqueId())
                .orElseThrow(() -> new IllegalStateException("Player not loaded."));

        this.command.execute(player, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return null;
        }

        final ProxyPlayer<?, ?> player = this.plugin.getPlayer(((ProxiedPlayer) sender).getUniqueId())
                .orElseThrow(() -> new IllegalStateException("Player not loaded."));

        return this.command.complete(player, args);
    }
}
