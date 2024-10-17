package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;
import net.savagedev.tpa.plugin.model.server.Server;
import net.savagedev.tpa.plugin.model.server.ServerLocation;

import java.util.Collection;
import java.util.Collections;
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
            Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", this.name + " <player|server> [world] [<y> <y> <z> [pitch] [yaw]]"));
            return;
        }

        final String target = args[0];

        if (target.equalsIgnoreCase(player.getName())) {
            Lang.TELEPORT_SELF.send(player, new Placeholder("%player%", player.getName()));
            return;
        }

        final Optional<ProxyPlayer<?, ?>> targetPlayer = this.plugin.getPlayer(target);

        // TODO: Permission checks.
        if (!targetPlayer.isPresent() || targetPlayer.get().isHidden()) {
            final Optional<Server<?>> targetServer = this.plugin.getServerManager().getOrLoad(target);
            if (targetServer.isPresent()) {
                if (args.length < 4) {
                    Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", this.name + " " + targetServer.get().getId() + " [world] <y> <y> <z> [pitch] [yaw]"));
                    return;
                }

                String worldName = null;
                // TODO: Some kind of index offset value based on whether the world name is null or not.

                float x, y, z;
                try {
                    x = Float.parseFloat(args[1]);
                } catch (NumberFormatException ignored) {
                    if (targetServer.get().getAllWorlds().contains(args[1])) {
                        worldName = args[1];
                    } else {
                        // TODO: Whaaa??
                        player.sendMessage("&cUnknown world.");
                    }
                    x = Float.parseFloat(args[2]);
                }

                if (worldName != null && args.length < 5) {
                    Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", this.name + " " + targetServer.get().getId() + " " + worldName + " <y> <y> <z> [pitch] [yaw]"));
                    return;
                }

                try {
                    y = Float.parseFloat(args[worldName == null ? 2 : 3]);
                    z = Float.parseFloat(args[worldName == null ? 3 : 4]);
                } catch (NumberFormatException ignored) {
                    Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", this.name + " " + targetServer.get().getId() + " " + worldName + " <y> <y> <z> [pitch] [yaw]"));
                    return;
                }

                float pitch = 0.0f, yaw = 0.0f;

                if (worldName == null && args.length == 6) {
                    try {
                        pitch = Float.parseFloat(args[4]);
                        yaw = Float.parseFloat(args[5]);
                    } catch (NumberFormatException ignored) {
                        Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", this.name + " " + targetServer.get().getId() + " " + worldName + " <y> <y> <z> [pitch] [yaw]"));
                        return;
                    }
                }

                if (worldName != null && args.length == 7) {
                    try {
                        pitch = Float.parseFloat(args[5]);
                        yaw = Float.parseFloat(args[6]);
                    } catch (NumberFormatException ignored) {
                        Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", this.name + " " + targetServer.get().getId() + " " + worldName + " <y> <y> <z> [pitch] [yaw]"));
                        return;
                    }
                }

                this.teleportCoords(player, targetServer.get(), worldName, x, y, z, pitch, yaw);
            } else {
                Lang.UNKNOWN_PLAYER.send(player, new Placeholder("%player%", target));
            }
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
            this.teleport(player, other, false, null);
            return;
        }

        player.withdraw(teleportCost).whenComplete((response, err) -> {
            if (err != null) {
                return;
            }

            if (response.isSuccess()) {
                this.teleport(player, other, true, response.getFormattedAmount());
                return;
            }

            player.getCurrentServer().formatCurrency(Setting.TELEPORT_COST.asFloat()).whenComplete((formattedAmount, ignored) ->
                    Lang.PAYMENT_FAILED.send(player,
                            new Placeholder("%amount%", formattedAmount),
                            new Placeholder("%balance%", response.getFormattedBalance()))
            );
        });
    }

    // tp [server] [world] <x> <y> <z> [pitch] [yaw] - Teleports the sender to the specified coordinates of the specified world on the specified server.
    protected void teleportCoords(ProxyPlayer<?, ?> player, Server<?> server, String world, float x, float y, float z, float pitch, float yaw) {
        this.plugin.getTeleportManager().teleport(player, new ServerLocation(server, world, x, y, z, pitch, yaw));
    }

    protected abstract void teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other, boolean paid, String formattedAmount);

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        if (args.length == 1) {
            final Set<String> targets = this.plugin.getOnlinePlayers().stream()
                    .filter(ProxyPlayer::notHidden)
                    .map(ProxyPlayer::getName)
                    .collect(Collectors.toSet());

            targets.addAll(this.plugin.getServerManager().getAll().values()
                    .stream()
                    .map(Server::getId)
                    .collect(Collectors.toSet()));

            targets.remove(player.getName());

            return filterCompletions(0, args, targets);
        }

        if (args.length == 2) {
            final Optional<Server<?>> targetServer = this.plugin.getServerManager().get(args[0]);

            if (!targetServer.isPresent()) {
                return Collections.emptySet();
            }

            return filterCompletions(1, args, targetServer.get().getAllWorlds());
        }

        return Collections.emptySet();
    }
}
