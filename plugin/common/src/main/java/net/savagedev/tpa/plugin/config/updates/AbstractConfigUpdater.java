package net.savagedev.tpa.plugin.config.updates;

public abstract class AbstractConfigUpdater {
    public AbstractConfigUpdater() {
        this.applyLangUpdates();
        this.applySettingUpdates();
    }


    protected abstract void applyLangUpdates();

    protected abstract void applySettingUpdates();
}
