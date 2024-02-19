package net.savagedev.tpa.spigot.hook.vanish;

import net.savagedev.tpa.bridge.hook.vanish.AbstractVanishHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.spigot.BungeeTpSpigotPlugin;
import net.savagedev.tpa.spigot.model.SpigotPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GenericVanishHook extends AbstractVanishHook implements Runnable {
    private final Set<UUID> vanished = new HashSet<>();

    private final BungeeTpSpigotPlugin platform;

    public GenericVanishHook(BungeeTpSpigotPlugin platform) {
        super(platform);
        this.platform = platform;

        platform.scheduleAsync(this, 0L, 1000L);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final BungeeTpPlayer bungeeTpPlayer = this.platform.getBungeeTpPlayer(player);
            final boolean isVanished = this.isVanished(player);

            if (isVanished && this.vanished.add(player.getUniqueId())) {
                this.onVanishEvent(bungeeTpPlayer, true);
                continue;
            }

            if (!isVanished && this.vanished.remove(player.getUniqueId())) {
                this.onVanishEvent(bungeeTpPlayer, false);
            }
        }
    }

    @Override
    public boolean isVanished(BungeeTpPlayer player) {
        return this.isVanished(((SpigotPlayer) player).getHandle());
    }

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) {
                return true;
            }
        }
        return false;
    }
}
