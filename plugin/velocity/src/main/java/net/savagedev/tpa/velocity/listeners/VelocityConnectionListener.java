package net.savagedev.tpa.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.listeners.AbstractConnectionListener;
import net.savagedev.tpa.velocity.model.player.VelocityPlayer;

public class VelocityConnectionListener extends AbstractConnectionListener {
    public VelocityConnectionListener(BungeeTpPlugin plugin) {
        super(plugin);
    }

    @Subscribe
    public void on(ServerPostConnectEvent event) {
        super.handleConnectEvent(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void on(DisconnectEvent event) {
        super.handleDisconnectEvent(super.plugin.getPlayer(event.getPlayer().getUsername())
                .orElse(new VelocityPlayer(event.getPlayer())));
    }
}
