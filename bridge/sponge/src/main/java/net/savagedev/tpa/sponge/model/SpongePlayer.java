package net.savagedev.tpa.sponge.model;

import net.kyori.adventure.text.Component;
import net.savagedev.tpa.bridge.model.BungeeTpPlayer;
import net.savagedev.tpa.bridge.model.Location;
import net.savagedev.tpa.common.messaging.messages.Message;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

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
    public void teleportTo(Location location) {
        final Optional<String> worldName = location.getWorldName();

        if (worldName.isPresent()) {
            final Optional<ServerWorld> world = Sponge.server().worldManager().world(ResourceKey.minecraft(worldName.get()));

            if (world.isEmpty()) {
                this.player.sendMessage(Component.text("Unknown world '" + worldName.get() + "'"));
                return;
            }

            this.player.setLocation(ServerLocation.of(world.get(), location.getX(),
                    location.getY(), location.getZ()));
        } else {
            this.player.setLocation(ServerLocation.of(this.player.world(), location.getX(),
                    location.getY(), location.getZ()));
        }
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(Component.text(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.player.hasPermission(permission);
    }

    @Override
    public UUID getUniqueId() {
        return this.player.uniqueId();
    }

    public ServerPlayer getHandle() {
        return this.player;
    }

    @Override
    public void teleportHere(BungeeTpPlayer player) {
        if (player == null) {
            throw new IllegalStateException("Cannot teleport a null player");
        }

        player.teleportTo(this);
    }
}
