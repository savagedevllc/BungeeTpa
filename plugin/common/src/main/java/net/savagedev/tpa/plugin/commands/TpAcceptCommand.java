package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;
import net.savagedev.tpa.plugin.utils.TimeUtils;

public class TpAcceptCommand extends AbstractRequestResponseCommand {

    public TpAcceptCommand(BungeeTpPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void respond(TeleportRequest request) {
        if (request.getDirection() == TeleportRequest.Direction.TO_RECEIVER) {
            if (!request.getReceiver().getCurrentServer().isAccessibleTo(request.getSender())) {
                Lang.RESTRICTED_SERVER.send(request.getReceiver());
                return;
            }

            if (request.getSender().canBypassDelay()) {
                Lang.TPA_REQUEST_ACCEPTED.send(request.getSender(), new Lang.Placeholder("%player%", request.getReceiver().getName()));
            } else {
                Lang.TPA_REQUEST_ACCEPTED_DELAYED.send(request.getSender(), new Lang.Placeholder("%player%", request.getReceiver().getName()),
                        new Lang.Placeholder("%delay%", TimeUtils.formatTime(Setting.DELAY.asLong() * 1000L, Setting.TIME_FORMAT.asTimeLengthFormat())));
            }

            Lang.TPA_REQUEST_ACCEPT.send(request.getReceiver(), new Lang.Placeholder("%player%", request.getSender().getName()));
        } else {
            if (!request.getSender().getCurrentServer().isAccessibleTo(request.getReceiver())) {
                Lang.RESTRICTED_SERVER.send(request.getReceiver());
                return;
            }

            if (request.getReceiver().canBypassDelay()) {
                Lang.TPA_HERE_REQUEST_ACCEPT.send(request.getReceiver(), new Lang.Placeholder("%player%", request.getSender().getName()));
            } else {
                Lang.TPA_HERE_REQUEST_ACCEPT_DELAYED.send(request.getReceiver(), new Lang.Placeholder("%player%", request.getSender().getName()),
                        new Lang.Placeholder("%delay%", TimeUtils.formatTime(Setting.DELAY.asLong() * 1000L, Setting.TIME_FORMAT.asTimeLengthFormat())));
            }

            Lang.TPA_HERE_REQUEST_ACCEPTED.send(request.getSender(), new Lang.Placeholder("%player%", request.getReceiver().getName()));
        }

        this.plugin.getTeleportManager().acceptRequest(request);
    }
}
