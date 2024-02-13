package net.savagedev.tpa.plugin.commands.admin;

import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.Collection;
import java.util.Collections;

public class ReloadCommand implements BungeeTpCommand {
    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        Lang.reload();
        Setting.reload();
        player.sendMessage("&a[BungeeTP] Configs reloaded.");
    }

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        return Collections.emptySet();
    }
}
