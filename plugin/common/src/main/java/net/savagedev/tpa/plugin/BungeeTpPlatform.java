package net.savagedev.tpa.plugin;

import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.loader.ConfigurationLoader;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.logging.Logger;

public interface BungeeTpPlatform<B> {
    void registerCommand(BungeeTpCommand command, String name, String permission, String... aliases);

    void registerChannel(String channelName);

    void unregisterChannel(String channelName);

    void scheduleTaskDelayed(Runnable task, long delay);

    void scheduleTaskRepeating(Runnable task, long period);

    ConfigurationLoader newConfigurationLoader(Path path) throws FileNotFoundException;

    Logger getLogger();

    Path getDataPath();
}
