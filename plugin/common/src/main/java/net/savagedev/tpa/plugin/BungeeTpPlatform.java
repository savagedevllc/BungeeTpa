package net.savagedev.tpa.plugin;

import net.savagedev.tpa.common.messaging.Messenger;
import net.savagedev.tpa.plugin.command.BungeeTpCommand;
import net.savagedev.tpa.plugin.config.loader.ConfigurationLoader;
import net.savagedev.tpa.plugin.model.server.Server;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.logging.Logger;

public interface BungeeTpPlatform {
    void registerCommand(BungeeTpCommand command, String name, String permission, String... aliases);

    int scheduleTaskDelayed(Runnable task, long delay);

    int scheduleTaskRepeating(Runnable task, long period);

    void cancelTask(int id);

    ConfigurationLoader newConfigurationLoader(Path path) throws FileNotFoundException;

    Messenger<Server<?>> getMessenger();

    Collection<String> getAllServerIds();

    Logger getLogger();

    Path getDataPath();

    String getPluginVersion();
}
