package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;

public class TpDenyCommand extends AbstractRequestResponseCommand {
    public TpDenyCommand(BungeeTpPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void respond(TeleportRequest request) {
        request.deny();
    }
}
