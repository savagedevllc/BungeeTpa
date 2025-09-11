package net.savagedev.tpa.bridge.listener;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.BungeeTpBridgePlugin;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.bridge.model.TeleportTarget;
import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoResponse;
import net.savagedev.tpa.common.messaging.messages.MessagePlayerInfo;

public class AbstractConnectionListener {
    private final BungeeTpBridgePlatform platform;
    private final BungeeTpBridgePlugin plugin;

    private boolean shouldSendBasicInfo = true;

    public AbstractConnectionListener(BungeeTpBridgePlugin plugin) {
        this.platform = plugin.getPlatform();
        this.plugin = plugin;
    }

    protected void handleJoinEvent(BungeeTpPlayer player) {
        if (player.hasPermission("bungeetp.update.notifications")) {
            this.plugin.getUpdateChecker().checkForUpdateAsync().whenComplete((updateInfo, err) -> {
                if (err != null) {
                    this.platform.getLogger().warning("An error occurred checking for an update.");
                    return;
                }

                if (updateInfo.isUpdateAvailable(this.platform.getPluginVersion())) {
                    player.sendMessage("&aA new version is available (" + updateInfo.getTag() + ") at: &n" + updateInfo.getDownloadUrl());
                } else {
                    player.sendMessage("&a[BungeeTP] You are running the latest version of BungeeTP! Enjoy :)");
                }
            });
        }

        if (this.shouldSendBasicInfo) {
            this.platform.getMessenger().sendData(new MessageBasicServerInfoResponse(this.platform.getSoftwareName(),
                    this.platform.getVersion(), this.platform.getEconomyProvider().isPresent(), this.platform.isWhitelisted(),
                    this.platform.getAllWorlds(), this.platform.getWhitelist()));
            this.shouldSendBasicInfo = false;
        }

        this.platform.delay(this.platform.getMessenger()::flushMessageQueue, 125L);

        // This is fine for now, since we really only use this message to tell the proxy if a player is vanished or not.
        // But if MessagePlayerInfo starts relaying more information to the proxy in the future, this needs to be changed.
        this.platform.delay(() -> this.platform.getVanishProvider().ifPresent(provider ->
                this.platform.getMessenger().sendData(player, new MessagePlayerInfo(player.getUniqueId(), provider.isVanished(player)))
        ), 250L);

        final TeleportTarget teleportTarget = this.plugin.getTpCache().remove(player.getUniqueId());

        if (teleportTarget == null) {
            return;
        }

        this.platform.delay(() -> teleportTarget.teleportHere(player), 250L);
    }

    protected void handleQuitEvent(BungeeTpPlayer player) {
        // TODO: Check if the player has a pending transaction.
        this.platform.getEconomyProvider().ifPresent(economy -> economy.deposit(player, 0.0));
    }
}
