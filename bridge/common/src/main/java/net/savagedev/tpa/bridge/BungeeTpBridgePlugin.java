package net.savagedev.tpa.bridge;

import net.savagedev.tpa.bridge.model.TeleportTarget;
import net.savagedev.tpa.common.update.UpdateChecker;
import net.savagedev.tpa.common.update.UpdateChecker.Info;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeeTpBridgePlugin {
    private final UpdateChecker updateChecker = new UpdateChecker();

    private final BungeeTpBridgePlatform platform;

    private Map<UUID, TeleportTarget> tpCache;

    public BungeeTpBridgePlugin(BungeeTpBridgePlatform platform) {
        this.platform = platform;
    }

    public void enable() {
        this.tpCache = new HashMap<>(this.platform.getMaxPlayers());
        this.platform.getMessenger().init();

        // Check for an update twice a day.
        this.platform.scheduleAsync(() -> {
            final Info updateInfo = this.updateChecker.checkForUpdateAsync().join();

            if (updateInfo.isUpdateAvailable(this.platform.getPluginVersion()) && !updateInfo.isPrerelease()) {
                this.platform.getLogger().info("A new version is available (" + updateInfo.getTag() + ") at: " + updateInfo.getDownloadUrl());
            } else {
                this.platform.getLogger().info("You are running the latest version of BungeeTP! Enjoy :)");
            }
        }, 0L, TimeUnit.HOURS.toMillis(12));
    }

    public void disable() {
        this.platform.getMessenger().shutdown();
    }

    public UpdateChecker getUpdateChecker() {
        return this.updateChecker;
    }

    public Map<UUID, TeleportTarget> getTpCache() {
        return this.tpCache;
    }

    public BungeeTpBridgePlatform getPlatform() {
        return this.platform;
    }
}
