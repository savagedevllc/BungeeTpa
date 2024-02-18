package net.savagedev.tpa.spigot.hook.vanish;

import de.myzelyam.api.vanish.PlayerHideEvent;
import de.myzelyam.api.vanish.VanishAPI;
import net.savagedev.tpa.bridge.hook.vanish.AbstractVanishHook;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.spigot.BungeeTpSpigotPlugin;
import net.savagedev.tpa.spigot.model.SpigotPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SuperVanishPluginHook extends AbstractVanishHook implements Listener {
    private final BungeeTpSpigotPlugin plugin;

    public SuperVanishPluginHook(BungeeTpSpigotPlugin platform) {
        super(platform);

        this.plugin = platform;

        platform.getServer().getPluginManager().registerEvents(this, platform);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerHideEvent event) {
        super.onVanishEvent(this.plugin.getBungeeTpPlayer(event.getPlayer().getUniqueId()), event.isSilent());
    }

    @Override
    public boolean isVanished(BungeeTpPlayer player) {
        return VanishAPI.isInvisible(((SpigotPlayer) player).getHandle());
    }
}
