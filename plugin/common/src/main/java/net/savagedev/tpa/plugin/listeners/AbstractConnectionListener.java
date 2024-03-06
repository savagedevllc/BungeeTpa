package net.savagedev.tpa.plugin.listeners;

import net.savagedev.tpa.common.messaging.messages.MessageBasicServerInfoRequest;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;
import net.savagedev.tpa.plugin.model.request.TeleportRequest;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractConnectionListener {
    protected final BungeeTpPlugin plugin;

    private final Set<String> warningsSent = new HashSet<>();

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

        this.plugin.getPlatform().scheduleTaskDelayed(() -> {
            if (server.hasSentBasicInfo()) {
                return;
            }

            this.plugin.getPlatform().getMessenger().sendData(server, new MessageBasicServerInfoRequest());
            this.plugin.getPlatform().scheduleTaskDelayed((() -> {
                if (server.hasSentBasicInfo() || !this.warningsSent.add(serverId)) {
                    return;
                }
                this.plugin.getLogger().warning("BungeeTP bridge not detected on the server '" + serverId + ".' Is it installed?");
            }), 1000L); // Give it some time to receive the message & update the ServerManager.
        }, 1000);
    }

    protected void handleDisconnectEvent(ProxyPlayer<?, ?> player) {
        final Set<TeleportRequest> requests = this.plugin.getTeleportManager().removeAllRequestsBySenderOrReceiver(player);

        for (TeleportRequest request : requests) {
            final ProxyPlayer<?, ?> receiver = request.getReceiver();
            if (receiver.isConnected()) {
                Lang.PLAYER_OFFLINE.send(receiver, new Lang.Placeholder("%player%", player.getName()));
            }

            final ProxyPlayer<?, ?> sender = request.getSender();
            if (sender.isConnected()) {
                Lang.PLAYER_OFFLINE.send(sender, new Lang.Placeholder("%player%", player.getName()));
            }

            if (request.isPaid()) {
                sender.deposit(Setting.TELEPORT_COST.asFloat());
            }
        }

        this.plugin.getPlayerManager().remove(player.getUniqueId());
    }
}
