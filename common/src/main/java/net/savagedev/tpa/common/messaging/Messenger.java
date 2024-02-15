package net.savagedev.tpa.common.messaging;

import com.google.gson.JsonObject;
import net.savagedev.tpa.common.messaging.messages.Message;

public interface Messenger<T> {
    void init();

    void shutdown();

    void handleIncomingMessage(String channel, byte[] bytes);

    void sendData(T t, Message message);

    void sendData(Message message);

    default JsonObject wrapMessage(Message message) {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty("message_type", message.getClass().getSimpleName());
        wrapper.add("message", message.serialize());
        return wrapper;
    }
}
