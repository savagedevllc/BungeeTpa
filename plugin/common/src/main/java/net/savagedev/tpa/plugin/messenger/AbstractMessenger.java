package net.savagedev.tpa.plugin.messenger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.savagedev.tpa.common.messaging.ChannelConstants;
import net.savagedev.tpa.common.messaging.Messenger;
import net.savagedev.tpa.common.messaging.messages.Message;
import net.savagedev.tpa.common.messaging.messages.MessagePlayerInfo;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractMessenger<T extends ProxyPlayer<?, ?>> implements Messenger<T> {
    private static final Map<String, Function<JsonObject, Message>> DECODER_FUNCTIONS = new HashMap<>();

    static {
        DECODER_FUNCTIONS.put(MessagePlayerInfo.class.getSimpleName(), MessagePlayerInfo::deserialize);
    }

    protected static final String CHANNEL = ChannelConstants.CHANNEL_NAME;

    private final BungeeTpPlugin plugin;

    public AbstractMessenger(BungeeTpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleIncomingMessage(String channel, byte[] bytes) {
        if (!channel.equals(CHANNEL)) {
            return;
        }

        try (final ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
             final DataInputStream dataStream = new DataInputStream(byteStream)) {
            final JsonElement element = JsonParser.parseString(dataStream.readUTF());
            if (element.isJsonNull() || !element.isJsonObject()) {
                return;
            }

            final JsonObject object = element.getAsJsonObject();
            if (object.isJsonNull()) {
                return;
            }

            final String messageType = object.get("message_type").getAsString();
            if (messageType == null) {
                return;
            }

            final JsonObject messageObject = object.get("message").getAsJsonObject();
            if (messageObject.isJsonNull()) {
                return;
            }

            final Message message = DECODER_FUNCTIONS.get(messageType).apply(messageObject);
            if (message == null) {
                return;
            }

            if (message instanceof MessagePlayerInfo) {
                this.handlePlayerInfo((MessagePlayerInfo) message);
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    private void handlePlayerInfo(MessagePlayerInfo playerInfo) {
        if (playerInfo == null) {
            return;
        }

        this.plugin.getPlayer(playerInfo.getUniqueId())
                .ifPresent(player -> player.setHidden(playerInfo.isHidden()));
    }

    @Override
    public void sendData(Message message) {
        this.sendData(null, message);
    }
}
