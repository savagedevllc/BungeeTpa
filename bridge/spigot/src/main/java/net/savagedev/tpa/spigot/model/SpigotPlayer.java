package net.savagedev.tpa.spigot.model;

import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.spigot.BungeeTpSpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SpigotPlayer implements BungeeTpPlayer {
    private final Player player;

    private final BungeeTpSpigotPlugin plugin;

    public SpigotPlayer(Player player, BungeeTpSpigotPlugin plugin) {
        if (player == null) {
            throw new IllegalStateException("Player cannot be null!");
        }
        this.player = player;
        this.plugin = plugin;
    }

    @Override
    public void sendData(Message message) throws IOException {
        try (final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             final DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            outputStream.writeUTF(message.serialize());
            this.player.sendPluginMessage(this.plugin, ChannelConstants.CHANNEL_NAME, byteStream.toByteArray());
        }
    }

    @Override
    public void teleportTo(BungeeTpPlayer target) {
        final Player targetPlayer = Bukkit.getPlayer(target.getUniqueId());

        if (targetPlayer == null) {
            throw new IllegalStateException("Cannot teleport to a null player");
        }

        this.player.teleport(targetPlayer);
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    public Player getHandle() {
        return this.player;
    }
}
