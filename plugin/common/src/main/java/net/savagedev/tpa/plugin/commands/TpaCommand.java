package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;
import net.savagedev.tpa.plugin.model.server.Server;
import net.savagedev.tpa.plugin.utils.TimeUtils;

public class TpaCommand extends AbstractTeleportCommand {
    public TpaCommand(BungeeTpPlugin plugin) {
        super(plugin, "tpa");
    }

    @Override
    protected void teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        final Server<?> currentServer = player.getCurrentServer();
        if (currentServer.hasEconomySupport()) {
            player.sendMessage("Requesting balance from current server...");
            player.withdraw(100.0d).whenComplete((response, err) -> {
                if (err != null) {
                    return;
                }

                if (response.isSuccess()) {
                    this.sendRequest(player, other);
                } else {
                    player.sendMessage("You do not have enough to send a teleport request.");
                }
            });
        } else {
            this.sendRequest(player, other);
        }
    }

    private void sendRequest(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other) {
        final boolean sent = this.plugin.getTeleportManager().addRequest(other, new TeleportRequest(player, other, TeleportRequest.Direction.TO_RECEIVER));
        if (sent) {
            Lang.TPA_REQUEST_RECEIVED.send(other, new Lang.Placeholder("%player%", player.getName()),
                    new Placeholder("%expires%", TimeUtils.formatTime(Setting.TP_REQUEST_EXPIRE.asLong() * 1000L, Setting.TIME_FORMAT.asTimeLengthFormat())));
        }
        Lang.TPA_REQUEST_SENT.send(player, new Lang.Placeholder("%player%", other.getName()),
                new Placeholder("%expires%", TimeUtils.formatTime(Setting.TP_REQUEST_EXPIRE.asLong() * 1000L, Setting.TIME_FORMAT.asTimeLengthFormat())));
    }
}
