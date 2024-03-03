package net.savagedev.tpa.plugin.config;

import net.savagedev.tpa.plugin.config.loader.ConfigurationLoader;
import net.savagedev.tpa.plugin.config.loader.ConfigurationNode;
import net.savagedev.tpa.plugin.utils.TimeUtils.TimeLengthFormat;

import java.util.List;
import java.util.Locale;

public enum Setting {
    DELAY("delay"),
    TP_REQUEST_EXPIRE("expire"),
    TIME_FORMAT("time-format"),
    BLACKLIST("blacklist"),
    TELEPORT_COST("teleport-cost");

    private static ConfigurationLoader s_Loader;
    private static ConfigurationNode s_RootNode;

    public static void load(ConfigurationLoader loader) {
        s_Loader = loader;
        reload();
    }

    public static void reload() {
        if (s_Loader == null) {
            throw new IllegalStateException("loader");
        }
        s_RootNode = s_Loader.load();
    }

    private final String key;

    Setting(String key) {
        this.key = key;
    }

    public void save() {
        s_Loader.save(s_RootNode);
    }

    public boolean notExists() {
        return s_RootNode.noChild(this.key);
    }

    public Setting set(Object value) {
        s_RootNode.node(this.key).set(value);
        return this;
    }

    public List<String> asStringList() {
        return s_RootNode.node(this.key).getList(String.class);
    }

    public TimeLengthFormat asTimeLengthFormat() {
        try {
            return TimeLengthFormat.valueOf(s_RootNode.node(this.key).getString("LONG").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return TimeLengthFormat.LONG;
        }
    }

    public String asString() {
        return s_RootNode.node(this.key).getString();
    }

    public boolean asBoolean() {
        return s_RootNode.node(this.key).getBoolean();
    }

    public long asLong() {
        return s_RootNode.node(this.key).getLong();
    }

    public float asFloat() {
        return s_RootNode.node(this.key).getFloat();
    }

    public int asInt() {
        return s_RootNode.node(this.key).getInt();
    }
}
