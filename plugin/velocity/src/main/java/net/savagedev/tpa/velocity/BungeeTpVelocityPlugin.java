package net.savagedev.tpa.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.savagedev.tpa.common.messaging.Messenger;
import net.savagedev.tpa.plugin.BungeeTpPlatform;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.loader.ConfigurationLoader;
import net.savagedev.tpa.plugin.model.server.Server;
import net.savagedev.tpa.velocity.command.VelocityCommand;
import net.savagedev.tpa.velocity.config.VelocityConfigurationLoader;
import net.savagedev.tpa.velocity.functions.VelocityChatFormattingFunction;
import net.savagedev.tpa.velocity.functions.VelocityPlayerLoaderFunction;
import net.savagedev.tpa.velocity.functions.VelocityServerLoaderFunction;
import net.savagedev.tpa.velocity.listeners.VelocityConnectionListener;
import net.savagedev.tpa.velocity.messenger.VelocityPluginMessenger;
import org.bstats.velocity.Metrics;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

@Plugin(id = "velocitytp", name = "VelocityTP", version = "${version}",
        url = "https://www.savagedev.net", description = "Teleport across a Velocity network.", authors = {"SavageAvocado"})
public class BungeeTpVelocityPlugin implements BungeeTpPlatform {
    private static final int B_STATS_ID = 20994;

    public static final Function<String, Component> CHAT_MESSAGE_FORMATTING_FUNCTION = new VelocityChatFormattingFunction();

    private final BungeeTpPlugin plugin;
    private final Path dataPath;
    private final VelocityPluginMessenger messenger;

    private final ProxyServer server;
    private final Logger logger;
    private final Metrics.Factory metricsFactory;

    @Inject
    public BungeeTpVelocityPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataPath, Metrics.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataPath = dataPath;
        this.metricsFactory = metricsFactory;
        this.messenger = new VelocityPluginMessenger(this);
        this.plugin = new BungeeTpPlugin(this, new VelocityPlayerLoaderFunction(this), new VelocityServerLoaderFunction(this.server));
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.server.getEventManager().register(this, new VelocityConnectionListener(this.plugin));
        this.plugin.enable();

        this.metricsFactory.make(this, B_STATS_ID);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.plugin.disable();
    }

    @Override
    public void registerCommand(BungeeTpCommand command, String name, String permission, String... aliases) {
        final CommandManager commandManager = this.server.getCommandManager();
        final CommandMeta meta = commandManager.metaBuilder(name)
                .aliases(aliases)
                .plugin(this)
                .build();
        commandManager.register(meta, new VelocityCommand(command, permission, this.plugin));
    }

    @Override
    public void scheduleTaskDelayed(Runnable task, long delay) {
        this.server.getScheduler().buildTask(this, task)
                .delay(delay, TimeUnit.MILLISECONDS)
                .schedule();
    }

    @Override
    public void scheduleTaskRepeating(Runnable task, long period) {
        this.server.getScheduler().buildTask(this, task)
                .repeat(period, TimeUnit.MILLISECONDS)
                .schedule();
    }

    @Override
    public ConfigurationLoader newConfigurationLoader(Path path) throws FileNotFoundException {
        return new VelocityConfigurationLoader(path);
    }

    @Override
    public Messenger<Server<?>> getMessenger() {
        return this.messenger;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public Path getDataPath() {
        return this.dataPath;
    }

    public ProxyServer getServer() {
        return this.server;
    }

    public BungeeTpPlugin getPlugin() {
        return this.plugin;
    }
}
