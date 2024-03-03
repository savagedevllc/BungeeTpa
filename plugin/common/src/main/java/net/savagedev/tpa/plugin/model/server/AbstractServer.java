package net.savagedev.tpa.plugin.model.server;

import net.savagedev.tpa.plugin.config.Setting;
import net.savagedev.tpa.plugin.model.player.ProxyPlayer;

public abstract class AbstractServer<T> implements Server<T> {
    private final String id;
    private final T handle;

    private boolean sentBasicInfo;
    private boolean economySupport;

    private String bridgeVersion;

    public AbstractServer(String id, T handle) {
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
    public boolean hasEconomySupport() {
        return this.economySupport;
    }

    @Override
    public String getBridgeVersion() {
        return this.bridgeVersion;
    }

    @Override
    public boolean isAccessibleTo(ProxyPlayer<?, ?> player) {
        return Setting.BLACKLIST.asStringList().stream()
                .noneMatch(server -> server.equalsIgnoreCase(this.getId())) && this.canAccess(player);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Server) && ((Server<?>) obj).getId().equals(this.getId());
    }
}
