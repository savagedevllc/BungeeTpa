package net.savagedev.tpa.plugin.config.updates;

import net.savagedev.tpa.plugin.config.Lang;
import net.savagedev.tpa.plugin.config.Setting;

public class ConfigUpdater_v3 extends AbstractConfigUpdater {
    @Override
    protected void applyLangUpdates() {
        boolean anyUpdated = false;

        if (Lang.RESTRICTED_SERVER_OTHER.notExists()) {
            Lang.RESTRICTED_SERVER_OTHER.set("&c%player% does not have permission to connect to this server!");
            anyUpdated = true;
        }

        if (anyUpdated) {
            Lang.saveAll();
        }
    }

    @Override
    protected void applySettingUpdates() {
        if (Setting.TELEPORT_COST.notExists()) {
            Setting.TELEPORT_COST.set(25.0f)
                    .save();
        }
    }
}
