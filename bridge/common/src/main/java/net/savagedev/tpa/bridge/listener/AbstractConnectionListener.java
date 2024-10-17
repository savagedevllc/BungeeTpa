package net.savagedev.tpa.bridge.listener;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.bridge.model.Teleportable;
import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoResponse;
import net.savagedev.tpa.common.messaging.messages.MessagePlayerInfo;

public class AbstractConnectionListener {
    private final BungeeTpBridgePlatform platform;

    private boolean shouldSendBasicInfo = true;

    public AbstractConnectionListener(BungeeTpBridgePlatform platform) {
        this.platform = platform;
    }

    protected void handleJoinEvent(BungeeTpPlayer player) {
        if (this.shouldSendBasicInfo) {
            this.platform.getMessenger().sendData(new MessageBasicServerInfoResponse(this.platform.getSoftwareName(),
                    this.platform.getVersion(), this.platform.getEconomyProvider().isPresent(), this.platform.getAllWorlds()));
            this.shouldSendBasicInfo = false;
        }
        this.platform.delay(() -> this.platform.getVanishProvider().ifPresent(provider -> {
            this.platform.getMessenger().sendData(player, new MessagePlayerInfo(player.getUniqueId(), provider.isVanished(player)));
        }), 250L);

        final Teleportable teleportable = this.platform.getTpCache().remove(player.getUniqueId());

        if (teleportable == null) {
            return;
        }

//        final BungeeTpPlayer target = this.platform.getBungeeTpPlayer(teleportable);
//
//        if (target == null) {
//            player.sendMessage("&c" + this.platform.getOfflineUsername(teleportable) + " is no longer online.");
//            return;
//        }

        this.platform.delay(() -> teleportable.teleportHere(player), 250L);
    }

    protected void handleQuitEvent(BungeeTpPlayer player) {
        // TODO: Check if the player has a pending transaction.
        this.platform.getEconomyProvider().ifPresent(economy -> economy.deposit(player, 0.0));
    }
}
