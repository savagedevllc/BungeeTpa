package net.savagedev.tpa.sponge.model;

import net.kyori.adventure.text.Component;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.common.messaging.messages.Message;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Optional;
import java.util.UUID;

public class SpongePlayer implements BungeeTpPlayer {
    private final ServerPlayer player;

    public SpongePlayer(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public void sendData(Message message) {
        throw new UnsupportedOperationException("not available on this platform");
    }

    @Override
    public void teleportTo(BungeeTpPlayer target) {
        final Optional<ServerPlayer> targetOptional = Sponge.server().player(target.getUniqueId());

        if (!targetOptional.isPresent()) {
            throw new IllegalStateException("Cannot teleport to a null player");
        }

        this.player.setLocation(ServerLocation.of(targetOptional.get().world(), targetOptional.get().position()));
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(Component.text(message));
    }

    @Override
    public UUID getUniqueId() {
        return this.player.uniqueId();
    }

    public ServerPlayer getHandle() {
        return this.player;
    }
}
