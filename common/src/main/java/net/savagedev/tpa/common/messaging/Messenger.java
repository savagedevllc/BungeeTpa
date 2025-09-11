package net.savagedev.tpa.common.messaging;

import net.savagedev.tpa.common.messaging.messages.Message;

public interface Messenger<T> {
    void init();

    void shutdown();

    void flushMessageQueue();

    void handleIncomingMessage(String serverId, String channel, byte[] bytes);

    void sendData(T t, Message message);

    void sendData(Message message);
}
