package net.savagedev.tpa.spigot.hook;

import com.earth2me.essentials.Essentials;
import net.ess3.api.events.VanishStatusChangeEvent;
import net.savagedev.tpa.bridge.hook.vanish.AbstractVanishProvider;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.spigot.BungeeTpSpigotPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EssentialsVanishHook extends AbstractVanishProvider implements Listener {
    private final BungeeTpSpigotPlugin plugin;

    private final Essentials essentials;

    public EssentialsVanishHook(BungeeTpSpigotPlugin platform) {
        super(platform);

        this.essentials = Essentials.getPlugin(Essentials.class);
        this.plugin = platform;

        platform.getServer().getPluginManager().registerEvents(this, platform);
    }

    @EventHandler
    public void on(VanishStatusChangeEvent event) {
        super.onVanishEvent(this.plugin.getBungeeTpPlayer(event.getAffected().getUUID()), event.getValue());
    }

    @Override
    public boolean isVanished(BungeeTpPlayer player) {
        return this.essentials.getUser(player.getUniqueId()).isVanished();
    }
}
