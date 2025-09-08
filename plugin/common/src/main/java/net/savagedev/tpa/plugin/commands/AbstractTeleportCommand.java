package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
            Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", this.name + " <player>"));
            return;
        }

        final String target = args[0];

        if (target.equalsIgnoreCase(player.getName())) {
            Lang.TELEPORT_SELF.send(player, new Placeholder("%player%", player.getName()));
            return;
        }

        final Optional<ProxyPlayer<?, ?>> targetPlayer = this.plugin.getPlayer(target);

        if (targetPlayer.isEmpty() || targetPlayer.get().isHidden()) {
            Lang.UNKNOWN_PLAYER.send(player, new Placeholder("%player%", target));
            return;
        }

        this.teleportPlayer(player, targetPlayer.get());
    }

    // tp <player> - Teleports the sender to the <player>
    protected void teleportPlayer(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        final Optional<TeleportRequest> receiverRequest = this.plugin.getTeleportManager().getRequestStack(other)
                .stream()
                .filter(request -> request.getSender().equals(player))
                .findAny();

        if (receiverRequest.isPresent()) {
            Lang.REQUEST_ALREADY_SENT.send(player, new Placeholder("%player%", other.getName()));
            return;
        }

        final Optional<TeleportRequest> senderRequest = this.plugin.getTeleportManager().getRequestStack(player)
                .stream()
                .filter(request -> request.getSender().equals(other))
                .findAny();

        if (senderRequest.isPresent()) {
            Lang.REQUEST_ALREADY_RECEIVED.send(player, new Placeholder("%player%", other.getName()));
            return;
        }

        final float teleportCost = Setting.TELEPORT_COST.asFloat();
        if (player.hasPermission("bungeetp.bypass.economy") || teleportCost == 0.0f || player.getCurrentServer().hasNoEconomy()) {
            this.teleport(player, other, false, 0.0d, null);
            return;
        }

        player.withdraw(teleportCost).whenComplete((response, err) -> {
            if (err != null) {
                player.sendMessage("&cError during teleportation: " + err.getMessage());
                return;
            }

            if (response.isSuccess()) {
                this.teleport(player, other, true, response.getAmount(), response.getFormattedAmount());
            } else {
                player.getCurrentServer().formatCurrency(Setting.TELEPORT_COST.asFloat()).whenComplete((formattedAmount, ignored) ->
                        Lang.PAYMENT_FAILED.send(player,
                                new Placeholder("%amount%", formattedAmount),
                                new Placeholder("%balance%", response.getFormattedBalance()))
                );
            }
        });
    }

    protected abstract void teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other, boolean paid, double amount, String formattedAmount);

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        final List<String> targets = this.plugin.getOnlinePlayers().stream()
                .filter(ProxyPlayer::notHidden)
                .map(ProxyPlayer::getName)
                .collect(Collectors.toList());

        targets.remove(player.getName());

        if (args.length == 0) {
            return targets;
        }

        if (args.length == 1) {
            return filterCompletions(0, args, targets);
        }

        return Collections.emptySet();
    }
}
