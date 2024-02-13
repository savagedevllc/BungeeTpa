package net.savagedev.tpa.velocity.functions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.function.Function;

public class VelocityChatFormattingFunction implements Function<String, Component> {
    @Override
    public Component apply(String message) {
        try {
            return GsonComponentSerializer.gson().deserialize(message);
        } catch (Exception ignored) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        }
    }
}
