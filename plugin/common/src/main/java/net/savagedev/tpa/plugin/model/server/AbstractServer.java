package net.savagedev.tpa.plugin.model.server;

import net.savagedev.tpa.common.messaging.messages.MessageCurrencyFormatRequest;
import net.savagedev.tpa.plugin.BungeeTpPlugin;
import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractServer<T> implements Server<T> {
    private final BungeeTpPlugin plugin;

    private final String id;
    private final T handle;

    private boolean sentBasicInfo;
    private boolean economySupport;

    private String serverSoftware;
    private String bridgeVersion;

    public AbstractServer(String id, T handle, BungeeTpPlugin plugin) {
        this.plugin = plugin;
        this.id = id;
        this.handle = handle;
    }

    @Override
    public void setSentBasicInfo(boolean sentBasicInfo) {
        this.sentBasicInfo = sentBasicInfo;
    }

    @Override
    public void setEconomySupport(boolean economySupport) {
        this.economySupport = economySupport;
    }

    @Override
    public void setServerSoftware(String software) {
        this.serverSoftware = software;
    }

    @Override
    public void setBridgeVersion(String bridgeVersion) {
        this.bridgeVersion = bridgeVersion;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public T getHandle() {
        return this.handle;
    }

    protected abstract boolean canAccess(ProxyPlayer<?, ?> player);

    @Override
    public boolean hasSentBasicInfo() {
        return this.sentBasicInfo;
    }

    @Override
    public boolean hasNoEconomy() {
        return !this.economySupport;
    }

    @Override
    public CompletableFuture<String> formatCurrency(double amount) {
        final CompletableFuture<String> future = new CompletableFuture<>();
        this.plugin.getPlatform().getMessenger().sendData(this, new MessageCurrencyFormatRequest(Setting.TELEPORT_COST.asFloat()));
        this.plugin.getServerManager().addAwaitingCurrencyFormat(this.getId(), future);
        return future;
    }

    @Override
    public String getServerSoftware() {
        return this.serverSoftware;
    }

    @Override
    public String getBridgeVersion() {
        return this.bridgeVersion;
    }

    @Override
    public boolean isAccessibleTo(ProxyPlayer<?, ?> player) {
        return (player.hasPermission("bungeetp.bypass.blacklist") || Setting.BLACKLIST.asStringList().stream()
                .noneMatch(server -> server.equalsIgnoreCase(this.getId()))) && this.canAccess(player);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Server) && ((Server<?>) obj).getId().equals(this.getId());
    }
}
