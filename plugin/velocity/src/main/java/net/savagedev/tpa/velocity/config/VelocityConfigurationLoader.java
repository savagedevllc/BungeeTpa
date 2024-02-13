package net.savagedev.tpa.velocity.config;

import net.savagedev.tpa.plugin.config.loader.AbstractConfigurationLoader;
import net.savagedev.tpa.plugin.config.loader.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public class VelocityConfigurationLoader extends AbstractConfigurationLoader {
    private final YAMLConfigurationLoader loader;

    public VelocityConfigurationLoader(Path path) throws FileNotFoundException {
        super(path);
        this.loader = YAMLConfigurationLoader.builder()
                .setFlowStyle(FlowStyle.BLOCK)
                .setPath(path)
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
