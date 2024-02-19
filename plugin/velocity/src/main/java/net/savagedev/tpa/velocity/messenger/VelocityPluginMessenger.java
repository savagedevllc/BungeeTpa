package net.savagedev.tpa.velocity.messenger;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.plugin.messenger.BungeeTpMessenger;
import net.savagedev.tpa.plugin.model.server.Server;
import net.savagedev.tpa.velocity.BungeeTpVelocityPlugin;

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
    }
}
