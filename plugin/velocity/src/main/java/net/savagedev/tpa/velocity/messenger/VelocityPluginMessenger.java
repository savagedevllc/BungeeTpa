package net.savagedev.tpa.velocity.messenger;

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
    private final BungeeTpVelocityPlugin plugin;

    public VelocityPluginMessenger(BungeeTpVelocityPlugin plugin) {
        super(plugin.getPlugin());
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.plugin.getServer().getChannelRegistrar().register(MinecraftChannelIdentifier.from(ChannelConstants.CHANNEL_NAME));
    }

    @Override
    public void shutdown() {
        this.plugin.getServer().getChannelRegistrar().unregister(MinecraftChannelIdentifier.from(ChannelConstants.CHANNEL_NAME));
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
