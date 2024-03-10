package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.Collection;
import java.util.Collections;

public class TpToggleCommand implements BungeeTpCommand {
    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        throw new UnsupportedOperationException("Command not implemented supported.");
    }

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        return Collections.emptyList();
    }
}
