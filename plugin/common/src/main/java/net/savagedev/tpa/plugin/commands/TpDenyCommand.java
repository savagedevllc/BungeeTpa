package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

public class TpDenyCommand extends AbstractRequestResponseCommand {
    public TpDenyCommand(BungeeTpPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void respond(TeleportRequest request) {
        if (request.getDirection() == TeleportRequest.Direction.TO_RECEIVER) {
            Lang.TPA_REQUEST_DENIED.send(request.getSender(), new Lang.Placeholder("%player%", request.getReceiver().getName()));
            Lang.TPA_REQUEST_DENY.send(request.getReceiver(), new Lang.Placeholder("%player%", request.getSender().getName()));
        } else {
            Lang.TPA_HERE_REQUEST_DENIED.send(request.getSender(), new Lang.Placeholder("%player%", request.getReceiver().getName()));
            Lang.TPA_HERE_REQUEST_DENY.send(request.getReceiver(), new Lang.Placeholder("%player%", request.getSender().getName()));
        }
    }
}
