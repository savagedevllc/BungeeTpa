package net.savagedev.tpa.plugin.commands.admin;

import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BungeeTpAdminCommand implements BungeeTpCommand {
    private final Map<String, BungeeTpCommand> children = new HashMap<>();

    public void addChild(String name, BungeeTpCommand command) {
        this.children.put(name, command);
    }

    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        if (args.length == 0) {
            Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", "bungeetp reload"));
            return;
        }

        final String action = args[0].toLowerCase(Locale.ROOT);

        if (!this.children.containsKey(action)) {
            Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", "bungeetp reload"));
            return;
        }

        this.children.get(action).execute(player, args);
    }

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        if (args.length == 0) {
            return this.children.keySet();
        }

        final String action = args[0].toLowerCase(Locale.ROOT);

        if (!this.children.containsKey(action)) {
            final Set<String> completions = new HashSet<>();
            for (String name : this.children.keySet()) {
                if (name.startsWith(action)) {
                    completions.add(action);
                }
            }
            return completions;
        }

        return this.children.get(action).complete(player, args);
    }
}
