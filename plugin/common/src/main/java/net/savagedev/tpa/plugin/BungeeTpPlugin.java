package net.savagedev.tpa.plugin;

import net.savagedev.tpa.plugin.commands.TpAcceptCommand;
import net.savagedev.tpa.plugin.commands.TpCancelCommand;
import net.savagedev.tpa.plugin.commands.TpCommand;
import net.savagedev.tpa.plugin.commands.TpDenyAllCommand;
import net.savagedev.tpa.plugin.commands.TpDenyCommand;
import net.savagedev.tpa.plugin.commands.TpHereCommand;
import net.savagedev.tpa.plugin.commands.TpaCommand;
import net.savagedev.tpa.plugin.commands.TpaHereCommand;
import net.savagedev.tpa.plugin.commands.admin.BungeeTpAdminCommand;
import net.savagedev.tpa.plugin.commands.admin.ReloadCommand;
import net.savagedev.tpa.plugin.commands.admin.ServerInfoCommand;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.config.updates.ConfigUpdater_v1;
import net.savagedev.tpa.plugin.config.updates.ConfigUpdater_v2;
import net.savagedev.tpa.plugin.model.TeleportManager;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.player.manager.PlayerManager;
import net.savagedev.tpa.plugin.model.server.Server;
import net.savagedev.tpa.plugin.model.server.manager.ServerManager;
import net.savagedev.tpa.plugin.tasks.RequestExpireHousekeeperTask;
import net.savagedev.tpa.plugin.utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

public class BungeeTpPlugin {
    private final ServerManager serverManager;
    private final PlayerManager playerManager;

    private final BungeeTpPlatform platform;

    private TeleportManager teleportManager;

    public BungeeTpPlugin(BungeeTpPlatform platform, Function<UUID, ProxyPlayer<?, ?>> playerLoaderFunction, Function<String, Server<?>> serverLoaderFunction) {
        this.playerManager = new PlayerManager(playerLoaderFunction);
        this.serverManager = new ServerManager(serverLoaderFunction);
        this.platform = platform;
    }

    public void enable() {
        this.platform.getMessenger().init();
        this.teleportManager = new TeleportManager(this.platform);
        this.initConfigs();
        this.applyConfigUpdates();
        this.initCommands();
        this.platform.scheduleTaskRepeating(new RequestExpireHousekeeperTask(this), 1000L);
    }

    public void disable() {
        this.teleportManager.shutdown();
        this.platform.getMessenger().shutdown();
    }

    private void initConfigs() {
        final Path dataFolder = this.platform.getDataPath();
        if (Files.notExists(dataFolder)) {
            try {
                Files.createDirectories(dataFolder);
            } catch (IOException e) {
                this.platform.getLogger().warning("Failed to create data folder: " + e.getMessage());
            }
        }

        final Path langPath = dataFolder.resolve("lang.yml");
        if (Files.notExists(langPath)) {
            try {
                FileUtils.copyResource("lang.yml", langPath);
            } catch (IOException e) {
                this.platform.getLogger().warning("Failed to copy lang.yml: " + e.getMessage());
            }
        }

        final Path configPath = dataFolder.resolve("config.yml");
        if (Files.notExists(configPath)) {
            try {
                FileUtils.copyResource("config.yml", configPath);
            } catch (IOException e) {
                this.platform.getLogger().warning("Failed to copy config.yml: " + e.getMessage());
            }
        }

        try {
            Lang.load(this.platform.newConfigurationLoader(langPath));
        } catch (FileNotFoundException e) {
            this.platform.getLogger().warning("Failed to load lang.yml: " + e.getMessage());
        }
        try {
            Setting.load(this.platform.newConfigurationLoader(configPath));
        } catch (FileNotFoundException e) {
            this.platform.getLogger().warning("Failed to load config.yml: " + e.getMessage());
        }
    }

    private void applyConfigUpdates() {
        // TODO: A better way to do this.
        new ConfigUpdater_v1();
        new ConfigUpdater_v2();
    }

    private void initCommands() {
        final BungeeTpAdminCommand adminCommand = new BungeeTpAdminCommand();
        adminCommand.addChild("reload", new ReloadCommand());
        adminCommand.addChild("serverinfo", new ServerInfoCommand(this));

        this.platform.registerCommand(adminCommand, "bungeetp", "bungeetp.admin");
        this.platform.registerCommand(new TpAcceptCommand(this), "tpaccept", "bungeetp.accept");
        this.platform.registerCommand(new TpaCommand(this), "tpa", "bungeetp.tpa");
        this.platform.registerCommand(new TpaHereCommand(this), "tpahere", "bungeetp.tpahere");
        this.platform.registerCommand(new TpCancelCommand(this), "tpcancel", "bungeetp.cancel");
        this.platform.registerCommand(new TpCommand(this), "tp", "bungeetp.tp");
        this.platform.registerCommand(new TpDenyAllCommand(this), "tpdenyall", "bungeetp.deny.all");
        this.platform.registerCommand(new TpDenyCommand(this), "tpdeny", "bungeetp.deny");
        this.platform.registerCommand(new TpHereCommand(this), "tphere", "bungeetp.tphere", "s");
        // Putting this off until I have the time to write a more robust storage solution.
        // this.platform.registerCommand(new TpToggleCommand(), "tptoggle", "bungeetp.toggle");
    }

    public ServerManager getServerManager() {
        return this.serverManager;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public Collection<? extends ProxyPlayer<?, ?>> getOnlinePlayers() {
        return Collections.unmodifiableCollection(this.playerManager.getAll().values());
    }

    public Optional<ProxyPlayer<?, ?>> getPlayer(String username) {
        return this.playerManager.getByName(username);
    }

    public Optional<ProxyPlayer<?, ?>> getPlayer(UUID id) {
        return this.playerManager.get(id);
    }

    public TeleportManager getTeleportManager() {
        return this.teleportManager;
    }

    public Logger getLogger() {
        return this.platform.getLogger();
    }

    public BungeeTpPlatform getPlatform() {
        return this.platform;
    }
}
