package net.savagedev.tpa.plugin.config.updates;

import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Setting;

import java.util.Arrays;

public class ConfigUpdater_v1 extends AbstractConfigUpdater {
    @Override
    protected void applyLangUpdates() {
        if (Lang.RESTRICTED_SERVER.notExists()) {
            Lang.RESTRICTED_SERVER.set("&cYou do not have permission to connect to this server!")
                    .save();
        }
    }

    @Override
    protected void applySettingUpdates() {
        if (Setting.BLACKLIST.notExists()) {
            Setting.BLACKLIST.set(Arrays.asList("build", "events"))
                    .save();
        }
    }
}
