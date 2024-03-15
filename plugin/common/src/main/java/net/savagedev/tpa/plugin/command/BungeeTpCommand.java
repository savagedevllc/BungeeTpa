package net.savagedev.tpa.plugin.command;

import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.Collection;
import java.util.Collections;

public interface BungeeTpCommand {
    void execute(ProxyPlayer<?, ?> player, String[] args);

    default Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        return Collections.emptySet();
    }
}
