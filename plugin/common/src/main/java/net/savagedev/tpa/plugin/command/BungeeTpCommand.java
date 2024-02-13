package net.savagedev.tpa.plugin.command;

import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.Collection;

public interface BungeeTpCommand {
    void execute(ProxyPlayer<?, ?> player, String[] args);

    Collection<String> complete(ProxyPlayer<?, ?> player, String[] args);
}
