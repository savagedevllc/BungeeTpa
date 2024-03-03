package net.savagedev.tpa.sponge;

import com.google.inject.Inject;
import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.BungeeTpBridgePlugin;
import net.savagedev.tpa.bridge.hook.economy.AbstractEconomyHook;
import net.savagedev.tpa.bridge.hook.vanish.AbstractVanishHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.Messenger;
import net.savagedev.tpa.sponge.hook.economy.SpongeEconomyHook;
import net.savagedev.tpa.sponge.listener.ConnectionListener;
import net.savagedev.tpa.sponge.messenger.SpongePluginMessenger;
import net.savagedev.tpa.sponge.model.SpongePlayer;
import org.apache.logging.log4j.Logger;
import org.bstats.sponge.Metrics;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Plugin("bungeetpasponge")
public class BungeeTpSpongePlugin implements BungeeTpBridgePlatform {
    private static final int B_STATS_ID = 20996;

    private final BungeeTpBridgePlugin plugin = new BungeeTpBridgePlugin(this);
    private final Messenger<BungeeTpPlayer> messenger = new SpongePluginMessenger(this);

    @Inject
    public Logger logger;

    @Inject
    private PluginContainer container;

    @Inject
    private Metrics.Factory metricsFactory;

    @Listener
    public void on(StartedEngineEvent<Server> ignored) {
        Sponge.eventManager().registerListeners(this.container, new ConnectionListener(this));
        this.plugin.enable();

        this.metricsFactory.make(B_STATS_ID);
    }

    @Listener
    public void on(StoppingEngineEvent<Server> ignored) {
        this.plugin.disable();
    }

    @Override
    public void scheduleAsync(Runnable runnable, long delay, long period) {
        final Task task = Task.builder()
                .delay(delay, TimeUnit.MILLISECONDS)
                .interval(period, TimeUnit.MICROSECONDS)
                .execute(runnable)
                .plugin(this.container)
                .build();
        Sponge.server().scheduler().submit(task);
    }

    @Override
    public void delay(Runnable runnable, long delay) {
        final Task task = Task.builder()
                .delay(delay, TimeUnit.MILLISECONDS)
                .execute(runnable)
                .plugin(this.container)
                .build();
        Sponge.server().scheduler().submit(task);
    }

    @Override
    public Messenger<BungeeTpPlayer> getMessenger() {
        return this.messenger;
    }

    @Override
    public BungeeTpPlayer getBungeeTpPlayer(UUID uuid) {
        return new SpongePlayer(Sponge.server().player(uuid)
                .orElseThrow(() -> new IllegalStateException("cannot get bungeetpplayer for offline sponge player")));
    }

    @Override
    public BungeeTpPlayer getABungeeTpPlayer() {
        return this.getBungeeTpPlayer(Sponge.server().onlinePlayers().iterator().next().uniqueId());
    }

    @Override
    public Map<UUID, UUID> getTpCache() {
        return this.plugin.getTpCache();
    }

    @Override
    public String getOfflineUsername(UUID uuid) {
        Optional<User> userOptional = Sponge.server().userManager().load(uuid).join();
        return userOptional.map(User::name).orElse(null);
    }

    @Override
    public Optional<AbstractVanishHook> getVanishProvider() {
        return Optional.empty();
    }

    @Override
    public Optional<AbstractEconomyHook> getEconomyProvider() {
        return Optional.of(new SpongeEconomyHook(this));
    }

    @Override
    public String getVersion() {
        return this.container.metadata().version().toString();
    }

    @Override
    public String getSoftwareName() {
        return "Sponge";
    }

    @Override
    public int getMaxPlayers() {
        return Sponge.server().maxPlayers();
    }
}
