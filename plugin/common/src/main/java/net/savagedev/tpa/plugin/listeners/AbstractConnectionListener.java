package net.savagedev.tpa.plugin.listeners;

import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoRequest;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractConnectionListener {
    protected final BungeeTpPlugin plugin;

    private Set<String> warningsSent = new HashSet<>();

    public AbstractConnectionListener(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    protected void handleConnectEvent(UUID uuid) {
        this.plugin.getPlayerManager().getOrLoad(uuid)
                .orElseThrow(() -> new IllegalStateException("Player not loaded."));
    }

    protected void handleServerConnectEvent(ProxyPlayer<?, ?> player, String serverId) {
        final Server<?> server = this.plugin.getServerManager().getOrLoad(serverId)
                .orElseThrow(() -> new IllegalStateException("Server not loaded."));

        if (server.hasSentBasicInfo()) {
            return;
        }

        this.plugin.getPlatform().scheduleTaskDelayed(() -> {
            this.plugin.getPlatform().getMessenger().sendData(player.getCurrentServer(), new MessageBasicServerInfoRequest());
            this.plugin.getPlatform().scheduleTaskDelayed((() -> {
                if (server.hasSentBasicInfo() || !this.warningsSent.add(serverId)) {
                    return;
                }
                this.plugin.getLogger().warning("BungeeTP bridge not detected on the server '" + serverId + ".' Is it installed?");
            }), 3000L); // Give it some time to receive the message & update the ServerManager.
        }, 250);
    }

    protected void handleDisconnectEvent(ProxyPlayer<?, ?> player) {
        final TeleportRequest request = this.plugin.getTeleportManager().removeRequest(player);
        if (request != null) {
            Lang.PLAYER_OFFLINE.send(request.getSender(), new Lang.Placeholder("%player%", player.getName()));
        }
        this.plugin.getPlayerManager().remove(player.getUniqueId());
    }
}
