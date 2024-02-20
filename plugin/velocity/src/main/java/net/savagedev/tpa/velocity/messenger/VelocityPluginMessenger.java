package net.savagedev.tpa.velocity.messenger;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelMessageSource;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.plugin.messenger.BungeeTpMessenger;
import net.savagedev.tpa.plugin.model.server.Server;
import net.savagedev.tpa.velocity.BungeeTpVelocityPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class VelocityPluginMessenger extends BungeeTpMessenger<Server<?>> {
    private static final MinecraftChannelIdentifier CHANNEL_IDENTIFIER = MinecraftChannelIdentifier.from(ChannelConstants.CHANNEL_NAME);

    private final BungeeTpVelocityPlugin plugin;

    public VelocityPluginMessenger(BungeeTpVelocityPlugin plugin) {
        super(plugin.getPlugin());
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.plugin.getServer().getChannelRegistrar().register(CHANNEL_IDENTIFIER);
        this.plugin.getServer().getEventManager().register(this.plugin, this);
    }

    @Override
    public void shutdown() {
        this.plugin.getServer().getChannelRegistrar().unregister(CHANNEL_IDENTIFIER);
    }

    @Subscribe
    public void on(PluginMessageEvent event) {
        final ChannelMessageSource source = event.getSource();

        String serverId = null;
        if (source instanceof Player) {
            serverId = ((Player) source).getUsername();
        } else if (source instanceof ServerConnection) {
            serverId = ((ServerConnection) source).getServerInfo().getName();
        }

        super.handleIncomingMessage(serverId, event.getIdentifier().getId(), event.getData());
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
