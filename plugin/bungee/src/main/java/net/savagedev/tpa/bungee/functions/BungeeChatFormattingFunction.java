package net.savagedev.tpa.bungee.functions;

import com.google.gson.JsonParseException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.function.Function;

public class BungeeChatFormattingFunction implements Function<String, BaseComponent[]> {
    @Override
    public BaseComponent[] apply(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);

        BaseComponent[] components;
        try {
            components = ComponentSerializer.parse(message);
        } catch (JsonParseException ignored) { // Message is either not a json message, or the formatting is incorrect. Just send it as text.
            components = new ComponentBuilder(message).create();
        }

        return components;
    }
}
