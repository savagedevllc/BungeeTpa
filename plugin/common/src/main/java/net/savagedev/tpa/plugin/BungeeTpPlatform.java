package net.savagedev.tpa.plugin;

import net.savagedev.tpa.common.messaging.Messenger;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.loader.ConfigurationLoader;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.logging.Logger;

public interface BungeeTpPlatform {
    void registerCommand(BungeeTpCommand command, String name, String permission, String... aliases);

    void scheduleTaskDelayed(Runnable task, long delay);

    void scheduleTaskRepeating(Runnable task, long period);

    ConfigurationLoader newConfigurationLoader(Path path) throws FileNotFoundException;

    Messenger<ProxyPlayer<?, ?>> getPlatformMessenger();

    Logger getLogger();

    Path getDataPath();
}
