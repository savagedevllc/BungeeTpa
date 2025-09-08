package net.savagedev.tpa.velocity.config;

import net.savagedev.tpa.plugin.config.loader.AbstractConfigurationLoader;
import net.savagedev.tpa.plugin.config.loader.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class VelocityConfigurationLoader extends AbstractConfigurationLoader {
    private final YamlConfigurationLoader loader;

    public VelocityConfigurationLoader(Path path) throws FileNotFoundException {
        super(path);
        this.loader = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(path)
                .build();
    }

    @Override
    public void save(ConfigurationNode node) {
        try {
            this.loader.save(((VelocityConfigurationNode) node).getHandle());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ConfigurationNode load() {
        try {
            return new VelocityConfigurationNode(this.loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
