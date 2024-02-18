package net.savagedev.tpa.spigot.messenger;

import net.savagedev.tpa.bridge.messenger.BungeeTpBridgeMessenger;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.spigot.BungeeTpSpigotPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import java.io.IOException;

public class SpigotPluginMessenger extends BungeeTpBridgeMessenger<BungeeTpPlayer> implements PluginMessageListener {
    private final BungeeTpSpigotPlugin plugin;

    private final Messenger messenger;

    public SpigotPluginMessenger(BungeeTpSpigotPlugin plugin) {
        super(plugin);
        this.messenger = plugin.getServer().getMessenger();
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.messenger.registerIncomingPluginChannel(this.plugin, ChannelConstants.CHANNEL_NAME, this);
        this.messenger.registerOutgoingPluginChannel(this.plugin, ChannelConstants.CHANNEL_NAME);
    }

    @Override
    public void shutdown() {
        this.messenger.unregisterIncomingPluginChannel(this.plugin, ChannelConstants.CHANNEL_NAME);
        this.messenger.unregisterOutgoingPluginChannel(this.plugin, ChannelConstants.CHANNEL_NAME);
    }

    @Override
    public void onPluginMessageReceived(@Nonnull String channel, @Nonnull Player player, @Nonnull byte[] bytes) {
        super.handleIncomingMessage(null, channel, bytes);
    }

    @Override
    public void sendData(BungeeTpPlayer recipient, Message message) {
        if (recipient == null) {
            recipient = this.plugin.getABungeeTpPlayer();
        }
        try {
            recipient.sendData(message);
        } catch (IOException e) {
            this.plugin.getLogger().warning("Failed to send plugin message: " + e.getMessage());
        }
    }
}
