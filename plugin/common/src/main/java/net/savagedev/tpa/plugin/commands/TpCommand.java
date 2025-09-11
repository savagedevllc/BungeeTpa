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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TpCommand implements BungeeTpCommand {
    private static final String COMMAND_NAME = "tp";

    protected final BungeeTpPlugin plugin;

    private boolean shouldSendAutocompleteNotice = true;

    public TpCommand(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ProxyPlayer<?, ?> player, String[] args) {
        if (args.length == 0) {
            Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", COMMAND_NAME + " <player|server> [world] [<y> <y> <z> [pitch] [yaw]]"));
            return;
        }

        final String target = args[0];

        if (target.equalsIgnoreCase(player.getName())) {
            Lang.TELEPORT_SELF.send(player, new Placeholder("%player%", player.getName()));
            return;
        }

        final Optional<ProxyPlayer<?, ?>> targetPlayer = this.plugin.getPlayer(target);

        if (targetPlayer.isEmpty() || targetPlayer.get().isHidden()) {
            final Optional<Server<?>> targetServer = this.plugin.getServerManager().getOrLoad(target);
            if (targetServer.isPresent()) {
                if (args.length < 4) {
                    Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", COMMAND_NAME + " " + targetServer.get().getId() + " [world] <y> <y> <z> [pitch] [yaw]"));
                    return;
                }

                String worldName = null;

                float x = 0.0f, y, z;
                try {
                    x = Float.parseFloat(args[1]);
                } catch (NumberFormatException ignored) {
                    worldName = args[1];
                }

                if (worldName != null && args.length < 5) {
                    Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", COMMAND_NAME + " " + targetServer.get().getId() + " " + worldName + " <y> <y> <z> [pitch] [yaw]"));
                    return;
                }

                try {
                    if (worldName != null) {
                        x = Float.parseFloat(args[2]);
                    }
                    y = Float.parseFloat(args[worldName == null ? 2 : 3]);
                    z = Float.parseFloat(args[worldName == null ? 3 : 4]);
                } catch (NumberFormatException ignored) {
                    Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", COMMAND_NAME + " " + targetServer.get().getId() + " " + worldName + " <y> <y> <z> [pitch] [yaw]"));
                    return;
                }

                float pitch = 0.0f, yaw = 0.0f;

                if (worldName == null && args.length == 6) {
                    try {
                        pitch = Float.parseFloat(args[4]);
                        yaw = Float.parseFloat(args[5]);
                    } catch (NumberFormatException ignored) {
                        Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", COMMAND_NAME + " " + targetServer.get().getId() + " <y> <y> <z> [pitch] [yaw]"));
                        return;
                    }
                }

                if (worldName != null && args.length == 7) {
                    try {
                        pitch = Float.parseFloat(args[5]);
                        yaw = Float.parseFloat(args[6]);
                    } catch (NumberFormatException ignored) {
                        Lang.INVALID_ARGUMENTS.send(player, new Placeholder("%command%", COMMAND_NAME + " " + targetServer.get().getId() + " " + worldName + " <y> <y> <z> [pitch] [yaw]"));
                        return;
                    }
                }

                this.teleportCoords(player, targetServer.get(), worldName, x, y, z, yaw, pitch);
            } else { // If the target server does not exist:
                String worldName = null;
                int argOffset = 0;
                if (player.getCurrentServer().getAllWorlds().contains(target)) { // We assume the target must be a local world then.
                    worldName = target;
                    argOffset = 1;
                }
                try {
                    final float x, y, z;
                    x = Float.parseFloat(args[argOffset]);
                    y = Float.parseFloat(args[1 + argOffset]);
                    z = Float.parseFloat(args[2 + argOffset]);

                    float pitch = 0.0f, yaw = 0.0f;
                    if (args.length >= 4 + argOffset) {
                        pitch = Float.parseFloat(args[3 + argOffset]);
                    }

                    if (args.length >= 5 + argOffset) {
                        yaw = Float.parseFloat(args[4 + argOffset]);
                    }

                    this.teleportCoords(player, player.getCurrentServer(), worldName, x, y, z, yaw, pitch);
                } catch (NumberFormatException ignored) {
                    Lang.UNKNOWN_PLAYER.send(player, new Placeholder("%player%", target));
                }
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
            this.teleport(player, other);
            return;
        }

        player.withdraw(teleportCost).whenComplete((response, err) -> {
            if (err != null) {
                player.sendMessage("&cError during teleportation: " + err.getMessage());
                return;
            }

            if (response.isSuccess()) {
                this.teleport(player, other);
            } else {
                player.getCurrentServer().formatCurrency(Setting.TELEPORT_COST.asFloat()).whenComplete((formattedAmount, ignored) ->
                        Lang.PAYMENT_FAILED.send(player,
                                new Placeholder("%amount%", formattedAmount),
                                new Placeholder("%balance%", response.getFormattedBalance()))
                );
            }
        });
    }

    // tp [server] [world] <x> <y> <z> [pitch] [yaw] - Teleports the sender to the specified coordinates of the specified world on the specified server.
    private void teleportCoords(ProxyPlayer<?, ?> player, Server<?> server, String world, float x, float y, float z, float pitch, float yaw) {
        if (server == null) {
            server = player.getCurrentServer();
        }

        this.plugin.getTeleportManager().teleport(player, new ServerLocation(server, world, x, y, z, pitch, yaw));
    }

    private void teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        Lang.TELEPORTING.send(player, new Lang.Placeholder("%player%", other.getName()));
        this.plugin.getTeleportManager().teleportAsync(player, other);
    }

    @Override
    public Collection<String> complete(ProxyPlayer<?, ?> player, String[] args) {
        if (args.length == 1) {
            final List<String> targets = this.plugin.getOnlinePlayers().stream()
                    .filter(ProxyPlayer::notHidden)
                    .map(ProxyPlayer::getName)
                    .collect(Collectors.toList());

            targets.addAll(this.plugin.getPlatform().getAllServerIds());
            targets.addAll(player.getCurrentServer().getAllWorlds());

            targets.remove(player.getName());

            return filterCompletions(0, args, targets);
        }

        if (args.length == 2) {
            final Optional<Server<?>> targetServer = this.plugin.getServerManager().get(args[0]);

            if (targetServer.isEmpty()) {
                return Collections.emptySet();
            }

            if (this.shouldSendAutocompleteNotice && !targetServer.get().hasSentBasicInfo()) {
                player.sendMessage("&c[BungeeTP] It looks like nobody has connected to this server yet. Due to plugin messaging limitations, world auto-completions will be unavailable until a player has connected to this server. You can disable this message in the config.yml.");
                this.shouldSendAutocompleteNotice = false;
            }

            return filterCompletions(1, args, targetServer.get().getAllWorlds());
        }

        return Collections.emptySet();
    }
}
