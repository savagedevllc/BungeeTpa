package net.savagedev.tpa.common.messaging.messages;

import com.google.gson.JsonObject;

public abstract class Message {
    private String serverId;

    public void setServerId(String serverId) {
        if (this.serverId != null) {
            throw new IllegalStateException("Server name already set");
        }
        this.serverId = serverId;
    }

    public String getServerId() {
        return this.serverId;
    }

    protected abstract JsonObject asJsonObject();

    public String serialize() {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty("message_type", this.getClass().getSimpleName());
        wrapper.add("message", this.asJsonObject());
        return wrapper.toString();
    }
}
