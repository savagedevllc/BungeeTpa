package net.savagedev.tpa.velocity.model.player;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.model.player.AbstractProxyPlayer;
import net.savagedev.tpa.plugin.model.server.Server;
import net.savagedev.tpa.velocity.BungeeTpVelocityPlugin;
import net.savagedev.tpa.velocity.model.server.VelocityServer;

import java.util.UUID;

public class VelocityPlayer extends AbstractProxyPlayer<Player, Component> {
    private final Player player;

    public VelocityPlayer(Player player, BungeeTpPlugin plugin) {
        super(plugin, player, BungeeTpVelocityPlugin.CHAT_MESSAGE_FORMATTING_FUNCTION);
        this.player = player;
    }

    @Override
    public void sendMessage(Component message) {
        this.player.sendMessage(message);
    }

    @Override
    public boolean connect(Server<?> server) {
        this.player.createConnectionRequest(((ServerConnection) server.getHandle()).getServer()).connect();
        return true;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.player.hasPermission(permission);
    }

    @Override
    public boolean isConnected() {
        return this.player.isActive();
    }

    @Override
    public Server<?> getCurrentServer() {
        return this.player.getCurrentServer()
                .map(connection ->
                        new VelocityServer(connection.getServerInfo().getName(), connection.getServer())
                )
                .orElse(null);
    }

    @Override
    protected String getCurrentServerId() {
        return this.player.getCurrentServer()
                .orElseThrow(() -> new IllegalStateException("Player not loaded.")).getServerInfo().getName();
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public String getName() {
        return this.player.getUsername();
    }
}
