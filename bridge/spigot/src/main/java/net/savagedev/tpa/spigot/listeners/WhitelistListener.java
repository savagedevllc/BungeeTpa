package net.savagedev.tpa.spigot.listeners;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.listener.AbstractWhitelistListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class WhitelistListener extends AbstractWhitelistListener implements Listener {
    public WhitelistListener(BungeeTpBridgePlatform platform) {
        super(platform);
    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent event) {
        this.handleEvent(event.getMessage());
    }

    @EventHandler
    public void on(ServerCommandEvent event) {
        this.handleEvent(event.getCommand());
    }

    private void handleEvent(String fullCommand) {
        final String[] commandParts = fullCommand.split(" ");

        if (commandParts.length < 3) {
            return;
        }

        final String command = commandParts[0];
        final String baseCommand = command.contains(":") ? command.split(":", 2)[1] : command;

        if (!baseCommand.equalsIgnoreCase("whitelist")) {
            return;
        }

        final OfflinePlayer player = Bukkit.getOfflinePlayer(commandParts[2]);

        if (commandParts[1].equalsIgnoreCase("add")) {
            super.handleWhitelistAddEvent(player.getUniqueId());
            return;
        }

        if (commandParts[1].equalsIgnoreCase("remove")) {
            super.handleWhitelistRemoveEvent(player.getUniqueId());
        }
    }
}
