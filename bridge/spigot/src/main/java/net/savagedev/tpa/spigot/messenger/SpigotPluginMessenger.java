package net.savagedev.tpa.spigot.messenger;

import net.savagedev.tpa.bridge.messenger.AbstractMessenger;
import net.savagedev.tpa.spigot.BungeeTpSpigotPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.annotation.Nonnull;

public class SpigotPluginMessenger extends AbstractMessenger implements PluginMessageListener {
    private final BungeeTpSpigotPlugin plugin;

    public SpigotPluginMessenger(BungeeTpSpigotPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void init() {
        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, CHANNEL, this);
    }

    @Override
    public void shutdown() {
        this.plugin.getServer().getMessenger().unregisterIncomingPluginChannel(this.plugin, CHANNEL);
    }

    @Override
    public void onPluginMessageReceived(@Nonnull String channel, @Nonnull Player player, @Nonnull byte[] bytes) {
        super.handleIncomingMessage(channel, bytes);
    }
}
