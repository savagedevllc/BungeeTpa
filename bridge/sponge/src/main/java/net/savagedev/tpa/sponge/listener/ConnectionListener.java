package net.savagedev.tpa.sponge.listener;

import net.savagedev.tpa.bridge.listener.AbstractConnectionListener;
import net.savagedev.tpa.sponge.BungeeTpSpongePlugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class ConnectionListener extends AbstractConnectionListener {
    private final BungeeTpSpongePlugin plugin;

    public ConnectionListener(BungeeTpSpongePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Listener
    public void on(ServerSideConnectionEvent.Join event) {
        super.handleJoinEvent(this.plugin.getBungeeTpPlayer(event.player().uniqueId()));
    }
}
