package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

public interface Message {
    JsonObject serialize();
}
