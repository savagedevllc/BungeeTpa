package net.savagedev.tpa.plugin.command;

import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface BungeeTpCommand {
    void execute(ProxyPlayer<?, ?> player, String[] args);

    default Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        return Collections.emptySet();
    }

    default Collection<String> filterCompletions(String[] args, Set<String> values) {
        if (args.length == 0) {
            return values;
        }

        final Set<String> completions = new HashSet<>();
        for (String value : values) {
            if (value.toLowerCase().startsWith(args[0].toLowerCase())) {
                completions.add(value);
            }
        }
        return completions;
    }
}
