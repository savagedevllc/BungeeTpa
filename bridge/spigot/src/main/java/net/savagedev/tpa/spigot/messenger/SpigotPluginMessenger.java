package net.savagedev.tpa.spigot.messenger;

import net.savagedev.tpa.bridge.messenger.AbstractMessenger;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.spigot.BungeeTpSpigotPlugin;
import net.savagedev.tpa.spigot.model.SpigotPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SpigotPluginMessenger extends AbstractMessenger<BungeeTpPlayer> implements PluginMessageListener {
    private final BungeeTpSpigotPlugin plugin;

    private final Messenger messenger;

    public SpigotPluginMessenger(BungeeTpSpigotPlugin plugin) {
        super(plugin);
        this.messenger = plugin.getServer().getMessenger();
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.messenger.registerIncomingPluginChannel(this.plugin, CHANNEL, this);
        this.messenger.registerOutgoingPluginChannel(this.plugin, CHANNEL);
    }

    @Override
    public void shutdown() {
        this.messenger.unregisterIncomingPluginChannel(this.plugin, CHANNEL);
        this.messenger.unregisterOutgoingPluginChannel(this.plugin, CHANNEL);
    }

    @Override
    public void onPluginMessageReceived(@Nonnull String channel, @Nonnull Player player, @Nonnull byte[] bytes) {
        super.handleIncomingMessage(channel, bytes);
    }

    @Override
    public void sendData(BungeeTpPlayer recipient, Message message) {
        if (recipient == null) {
            recipient = new SpigotPlayer(this.plugin.getServer().getOnlinePlayers().iterator().next());
        }

        try (final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             final DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            outputStream.writeUTF(this.wrapMessage(message).toString());
            (((SpigotPlayer) recipient).getHandle()).sendPluginMessage(this.plugin, CHANNEL, byteStream.toByteArray());
        } catch (IOException e) {
            this.plugin.getLogger().warning("Failed to send plugin message: " + e.getMessage());
        }
    }
}
