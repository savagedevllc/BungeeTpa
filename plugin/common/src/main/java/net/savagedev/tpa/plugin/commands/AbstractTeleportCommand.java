package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractTeleportCommand implements BungeeTpCommand {
    protected final BungeeTpPlugin plugin;

    private final String name;

    public AbstractTeleportCommand(BungeeTpPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        if (args.length == 0) {
            Lang.INVALID_ARGUMENTS.send(player, new Lang.Placeholder("%command%", this.name + " <player>"));
            return;
        }

        final String username = args[0];

        if (username.equalsIgnoreCase(player.getName())) {
            Lang.TELEPORT_SELF.send(player, new Lang.Placeholder("%player%", player.getName()));
            return;
        }

        final Optional<ProxyPlayer<?, ?>> target = this.plugin.getPlayer(username);

        if (!target.isPresent() || target.get().isHidden()) {
            Lang.UNKNOWN_PLAYER.send(player, new Lang.Placeholder("%player%", username));
            return;
        }


        final Optional<TeleportRequest> receiverRequest = this.plugin.getTeleportManager().getRequestStack(target.get())
                .stream()
                .filter(request -> request.getSender().equals(player))
                .findAny();

        if (receiverRequest.isPresent()) {
            Lang.REQUEST_ALREADY_SENT.send(player, new Placeholder("%player%", target.get().getName()));
            return;
        }


        final Optional<TeleportRequest> senderRequest = this.plugin.getTeleportManager().getRequestStack(player)
                .stream()
                .filter(request -> request.getSender().equals(target.get()))
                .findAny();

        if (senderRequest.isPresent()) {
            Lang.REQUEST_ALREADY_RECEIVED.send(player, new Placeholder("%player%", target.get().getName()));
            return;
        }

        final float teleportCost = Setting.TELEPORT_COST.asFloat();
        if (player.hasPermission("bungeetp.bypass.economy") || teleportCost == 0.0f || player.getCurrentServer().hasNoEconomy()) {
            this.teleport(player, target.get(), false, null);
            return;
        }

        player.withdraw(teleportCost).whenComplete((response, err) -> {
            if (err != null) {
                return;
            }

            if (response.isSuccess()) {
                this.teleport(player, target.get(), true, response.getFormattedAmount());
                return;
            }

            player.getCurrentServer().formatCurrency(Setting.TELEPORT_COST.asFloat()).whenComplete((formattedAmount, ignored) ->
                    Lang.PAYMENT_FAILED.send(player,
                            new Placeholder("%amount%", formattedAmount),
                            new Placeholder("%balance%", response.getFormattedBalance()))
            );
        });
    }

    protected abstract void teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other, boolean paid, String formattedAmount);

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        final Set<String> usernames = this.plugin.getOnlinePlayers().stream()
                .filter(ProxyPlayer::notHidden)
                .map(ProxyPlayer::getName)
                .collect(Collectors.toSet());

        usernames.remove(player.getName());

        if (args.length == 0) {
            return usernames;
        }

        final Set<String> completions = new HashSet<>();
        for (String username : usernames) {
            if (username.toLowerCase().startsWith(args[0].toLowerCase())) {
                completions.add(username);
            }
        }
        return completions;
    }
}
