package net.savagedev.tpa.bungee;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.savagedev.tpa.bungee.command.BungeeCommand;
import net.savagedev.tpa.bungee.config.BungeeConfigurationLoader;
import net.savagedev.tpa.bungee.functions.BungeeChatFormattingFunction;
import net.savagedev.tpa.bungee.functions.BungeePlayerLoaderFunction;
import net.savagedev.tpa.bungee.functions.BungeeServerLoaderFunction;
import net.savagedev.tpa.bungee.listeners.BungeeConnectionListener;
import net.savagedev.tpa.bungee.messenger.BungeePluginMessenger;
import net.savagedev.tpa.common.messaging.Messenger;
import net.savagedev.tpa.plugin.BungeeTpPlatform;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.loader.ConfigurationLoader;
import net.savagedev.tpa.plugin.model.server.Server;
import org.bstats.bungeecord.Metrics;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class BungeeTpBungeePlugin extends Plugin implements BungeeTpPlatform {
    private static final int B_STATS_ID = 20993;

    public static final Function<String, BaseComponent[]> CHAT_MESSAGE_FORMATTING_FUNCTION = new BungeeChatFormattingFunction();

    private final BungeeTpPlugin plugin = new BungeeTpPlugin(this,
            new BungeePlayerLoaderFunction(this),
            new BungeeServerLoaderFunction(this));
    private final Messenger<Server<?>> messenger = new BungeePluginMessenger(this);

    private final PluginManager pluginManager = this.getProxy().getPluginManager();

    @Override
    public void onEnable() {
        this.initListeners();
        this.plugin.enable();

        new Metrics(this, B_STATS_ID);
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }

    private void initListeners() {
        this.pluginManager.registerListener(this, new BungeeConnectionListener(this.plugin));
    }

    @Override
    public void registerCommand(BungeeTpCommand command, String name, String permission, String... aliases) {
        this.pluginManager.registerCommand(this,
                new BungeeCommand(command, this.plugin, name, permission, aliases));
    }

    @Override
    public void scheduleTaskDelayed(Runnable task, long delay) {
        this.getProxy().getScheduler().schedule(this, task, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void scheduleTaskRepeating(Runnable task, long period) {
        this.getProxy().getScheduler().schedule(this, task, 0L, period, TimeUnit.MILLISECONDS);
    }

    @Override
    public ConfigurationLoader newConfigurationLoader(Path path) throws FileNotFoundException {
        return new BungeeConfigurationLoader(path);
    }

    @Override
    public Messenger<Server<?>> getMessenger() {
        return this.messenger;
    }

    @Override
    public Path getDataPath() {
        return this.getDataFolder().toPath();
    }

    public BungeeTpPlugin getPlugin() {
        return this.plugin;
    }
}
