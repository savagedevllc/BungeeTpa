package net.savagedev.tpa.bungee.messenger;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.savagedev.tpa.bungee.BungeeTpBungeePlugin;
import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.plugin.messenger.BungeeTpMessenger;
import net.savagedev.tpa.plugin.model.server.Server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeePluginMessenger extends BungeeTpMessenger<Server<?>> implements Listener {
    private final BungeeTpBungeePlugin plugin;

    public BungeePluginMessenger(BungeeTpBungeePlugin plugin) {
        super(plugin.getPlugin());
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.plugin.getProxy().registerChannel(ChannelConstants.CHANNEL_NAME);
        this.plugin.getProxy().getPluginManager().registerListener(this.plugin, this);
    }

    @Override
    public void shutdown() {
        this.plugin.getProxy().getPluginManager().unregisterListener(this);
        this.plugin.getProxy().unregisterChannel(ChannelConstants.CHANNEL_NAME);
    }

    @EventHandler
    public void on(PluginMessageEvent event) {
        final Connection connection = event.getReceiver();

        String serverId = null;
        if (connection instanceof net.md_5.bungee.api.connection.Server) {
            serverId = ((net.md_5.bungee.api.connection.Server) connection).getInfo().getName();
        } else if (connection instanceof ProxiedPlayer) {
            serverId = ((ProxiedPlayer) connection).getServer().getInfo().getName();
        }

        super.handleIncomingMessage(serverId, event.getTag(), event.getData());
    }

    @Override
    public void sendData(Server<?> server, Message message) {
        if (server == null) {
            throw new IllegalStateException("Recipient cannot be null!");
        }

        try (final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             final DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            outputStream.writeUTF(message.serialize());
            server.sendData(ChannelConstants.CHANNEL_NAME, byteStream.toByteArray());
        } catch (IOException e) {
            this.plugin.getLogger().warning("Failed to send plugin message: " + e.getMessage());
        }
    }
}
