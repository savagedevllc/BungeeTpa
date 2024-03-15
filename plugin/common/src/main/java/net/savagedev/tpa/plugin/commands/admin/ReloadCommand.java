package net.savagedev.tpa.plugin.commands.admin;

import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

public class ReloadCommand implements BungeeTpCommand {
    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        Lang.reload();
        Setting.reload();
        player.sendMessage("&a[BungeeTP] Configs reloaded.");
    }
}
