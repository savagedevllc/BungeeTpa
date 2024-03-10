package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Lang.Placeholder;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;
import net.savagedev.tpa.plugin.utils.TimeUtils;

public class TpaCommand extends AbstractTeleportCommand {
    public TpaCommand(BungeeTpPlugin plugin) {
        super(plugin, "tpa");
    }

    @Override
    protected void teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other, boolean paid, String formattedAmount) {
        final boolean sent = this.plugin.getTeleportManager().pushRequest(new TeleportRequest(player, other, TeleportRequest.Direction.TO_RECEIVER, paid)).isSuccess();
        if (sent) {
            Lang.TPA_REQUEST_RECEIVED.send(other, new Lang.Placeholder("%player%", player.getName()),
                    new Placeholder("%expires%", TimeUtils.formatTime(Setting.TP_REQUEST_EXPIRE.asLong() * 1000L, Setting.TIME_FORMAT.asTimeLengthFormat())));
        }

        if (paid) {
            Lang.TPA_REQUEST_SENT_PAID.send(player, new Lang.Placeholder("%player%", other.getName()),
                    new Placeholder("%expires%", TimeUtils.formatTime(Setting.TP_REQUEST_EXPIRE.asLong() * 1000L, Setting.TIME_FORMAT.asTimeLengthFormat())),
                    new Placeholder("%amount%", formattedAmount));
        } else {
            Lang.TPA_REQUEST_SENT.send(player, new Lang.Placeholder("%player%", other.getName()),
                    new Placeholder("%expires%", TimeUtils.formatTime(Setting.TP_REQUEST_EXPIRE.asLong() * 1000L, Setting.TIME_FORMAT.asTimeLengthFormat())));
        }
    }
}
