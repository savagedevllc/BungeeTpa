package net.savagedev.tpa.common.messaging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.savagedev.tpa.common.messaging.messages.Message;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractMessenger<T> implements Messenger<T> {
    private final Map<String, Function<JsonObject, Message>> decoderFunctions;

    public AbstractMessenger(Map<String, Function<JsonObject, Message>> decoderFunctions) {
        this.decoderFunctions = decoderFunctions;
    }

    @Override
    public void handleIncomingMessage(String serverId, String channel, byte[] bytes) {
        if (!channel.equals(ChannelConstants.CHANNEL_NAME)) {
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

            final Message message = this.decoderFunctions.get(messageType).apply(messageObject);
            if (message == null) {
                return;
            }

            message.setServerId(serverId);

            this.handleIncomingMessage(message);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void sendData(Message message) {
        this.sendData(null, message);
    }

    protected abstract void handleIncomingMessage(Message message);
}
