package net.savagedev.tpa.sponge.listener;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.listener.AbstractWorldListener;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;

public class WorldListener extends AbstractWorldListener {
    public WorldListener(BungeeTpBridgePlatform platform) {
        super(platform);
    }

    @Listener
    public void on(LoadWorldEvent event) {
        super.handleWorldLoadEvent(event.world().key().value());
    }

    @Listener
    public void on(UnloadWorldEvent event) {
        super.handleWorldUnloadEvent(event.world().key().value());
    }
}
