package net.savagedev.tpa.spigot.listeners;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.listener.AbstractWhitelistListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Locale;

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
        final String[] commandParts = fullCommand.split(" ", 3);

        if (commandParts.length < 3) {
            return;
        }

        final String command = commandParts[0].replace("/", "");
        final String baseCommand = command.contains(":") ? command.split(":", 2)[1] : command;

        if (!baseCommand.equalsIgnoreCase("whitelist")) {
            return;
        }

        final OfflinePlayer player = Bukkit.getOfflinePlayer(commandParts[2]);

        switch (commandParts[1].toLowerCase(Locale.ROOT)) {
            case "add":
                super.handleWhitelistAddEvent(player.getUniqueId());
                break;
            case "remove":
                super.handleWhitelistRemoveEvent(player.getUniqueId());
                break;
            case "on":
                super.handleWhitelistStatusChange(true);
                break;
            case "off":
                super.handleWhitelistStatusChange(false);
        }
    }
}
