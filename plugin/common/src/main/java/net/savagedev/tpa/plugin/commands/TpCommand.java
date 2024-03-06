package net.savagedev.tpa.plugin.commands;

import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

public class TpCommand extends AbstractTeleportCommand {
    public TpCommand(BungeeTpPlugin plugin) {
        super(plugin, "tp");
    }

    @Override
    protected void teleport(ProxyPlayer<?, ?> player, ProxyPlayer<?, ?> other, boolean ignored, String formattedAmount) {
        Lang.TELEPORTING.send(player, new Lang.Placeholder("%player%", other.getName()));
        this.plugin.getTeleportManager().teleportAsync(player, other);
    }
}
