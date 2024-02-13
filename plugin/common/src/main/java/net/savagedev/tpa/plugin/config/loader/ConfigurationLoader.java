package net.savagedev.tpa.plugin.config.loader;

public interface ConfigurationLoader {
    void save(ConfigurationNode node);

    ConfigurationNode load();
}
