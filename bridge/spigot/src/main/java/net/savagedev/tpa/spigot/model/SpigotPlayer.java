package net.savagedev.tpa.spigot.model;

import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpigotPlayer implements BungeeTpPlayer {
    private final Player player;

    public SpigotPlayer(Player player) {
        this.player = player;
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
}
