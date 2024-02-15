package net.savagedev.tpa.sponge.messenger;

import net.savagedev.tpa.bridge.messenger.AbstractMessenger;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.sponge.BungeeTpSpongePlugin;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.network.EngineConnectionSide;
import org.spongepowered.api.network.ServerSideConnection;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.api.network.channel.raw.play.RawPlayDataHandler;

public class SpongePluginMessenger extends AbstractMessenger<ChannelBuf> implements RawPlayDataHandler<ServerSideConnection> {
    private static final ResourceKey CHANNEL_KEY = ResourceKey.resolve(CHANNEL);

    private RawDataChannel channel;


    public SpongePluginMessenger(BungeeTpSpongePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        this.channel = Sponge.game().channelManager().ofType(CHANNEL_KEY, RawDataChannel.class);
        this.channel.play().addHandler(EngineConnectionSide.SERVER, this);
    }

    @Override
    public void shutdown() {
        if (this.channel == null) {
            return;
        }
        this.channel.play().removeHandler(this);
    }

    @Override
    public void sendData(ChannelBuf data, String channel, Message message) {
    }

    @Override
    public void handlePayload(ChannelBuf data, ServerSideConnection connection) {
        super.handleIncomingMessage(CHANNEL_KEY.asString(), data.readBytes(data.available()));
    }
}
