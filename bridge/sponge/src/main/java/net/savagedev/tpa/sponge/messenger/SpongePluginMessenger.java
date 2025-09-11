package net.savagedev.tpa.sponge.messenger;

import net.savagedev.tpa.bridge.messenger.BungeeTpBridgeMessenger;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.sponge.BungeeTpSpongePlugin;
import net.savagedev.tpa.sponge.model.SpongePlayer;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.network.EngineConnectionSide;
import org.spongepowered.api.network.ServerSideConnection;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.api.network.channel.raw.play.RawPlayDataHandler;

import java.util.Optional;

public class SpongePluginMessenger extends BungeeTpBridgeMessenger<BungeeTpPlayer> implements RawPlayDataHandler<ServerSideConnection> {
    private static final ResourceKey CHANNEL_KEY = ResourceKey.resolve(ChannelConstants.CHANNEL_NAME);

    private final BungeeTpSpongePlugin plugin;

    private RawDataChannel channel;

    public SpongePluginMessenger(BungeeTpSpongePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
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
    public void sendData(BungeeTpPlayer recipient, Message message) {
        Optional.ofNullable(recipient)
                .or(this.plugin::getAnyBungeeTpPlayer)
                .ifPresentOrElse(
                        player -> this.channel.play().sendTo(
                                ((SpongePlayer) player).getHandle(),
                                buf -> buf.writeString(message.serialize())
                        ),
                        () -> super.queueMessage(message)
                );
    }

    @Override
    public void handlePayload(ChannelBuf data, ServerSideConnection connection) {
        super.handleIncomingMessage(connection.profile().hasName() ?
                        connection.profile().name().get() :
                        connection.profile().uuid().toString(),
                CHANNEL_KEY.asString(),
                data.readBytes(data.available()));
    }
}
