package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

public class TpHereCommand extends AbstractTeleportCommand {
    public TpHereCommand(BungeeTpPlugin plugin) {
        super(plugin, "tphere");
    }

    @Override
    protected void teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other, boolean ignored, String formattedAmount) {
        Lang.TELEPORT_HERE.send(player, new Lang.Placeholder("%player%", other.getName()));
        this.plugin.getTeleportManager().teleportAsync(other, player);
    }
}
