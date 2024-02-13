package net.savagedev.tpa.bridge.messenger;

import net.savagedev.tpa.bridge.BungeeTpBridgePlatform;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport;
import net.savagedev.tpa.common.messaging.messages.MessageRequestTeleport.Type;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public abstract class AbstractMessenger implements Messenger {
    protected static final String CHANNEL = ChannelConstants.CHANNEL_NAME;

    private final BungeeTpBridgePlatform platform;

    public AbstractMessenger(BungeeTpBridgePlatform platform) {
        this.platform = platform;
    }

    protected void handleIncomingMessage(String channel, byte[] bytes) {
        if (!channel.equals(CHANNEL)) {
            return;
        }

        try (final ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
             final DataInputStream dataStream = new DataInputStream(byteStream)) {
            final MessageRequestTeleport request = MessageRequestTeleport.deserialize(dataStream.readUTF());

            if (request == null) {
                return;
            }

            final UUID requesterId = request.getRequester();
            final BungeeTpPlayer receiver = this.platform.getBungeeTpPlayer(request.getReceiver());

            if (request.getType() == Type.INSTANT) {
                final BungeeTpPlayer requester = this.platform.getBungeeTpPlayer(requesterId);
                requester.teleportTo(receiver);
            } else {
                this.platform.getTpCache().put(requesterId, receiver.getUniqueId());
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
