package net.savagedev.tpa.spigot.model;

import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.bridge.model.Location;
import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.spigot.BungeeTpSpigotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.World;
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
    public void teleportTo(Location location) {
        World world = this.plugin.getServer().getWorld(location.getWorldName());
        if (world == null) {
            // The world at index 0 is the default world on this platform.
            world = this.plugin.getServer().getWorlds().get(0);
        }
        this.player.teleport(new org.bukkit.Location(world, location.getX(), location.getY(), location.getZ()));
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public Location getLocation() {
        final org.bukkit.Location location = this.player.getLocation();
        return new Location(this.player.getWorld().getName(), (float) location.getX(),
                (float) location.getY(), (float) location.getZ());
    }

    public Player getHandle() {
        return this.player;
    }
}
