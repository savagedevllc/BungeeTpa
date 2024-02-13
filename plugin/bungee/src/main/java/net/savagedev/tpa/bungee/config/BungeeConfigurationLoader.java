package net.savagedev.tpa.bungee.config;

import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.savagedev.tpa.plugin.config.loader.AbstractConfigurationLoader;
import net.savagedev.tpa.plugin.config.loader.ConfigurationNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class BungeeConfigurationLoader extends AbstractConfigurationLoader {
    private static final ConfigurationProvider PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);

    public BungeeConfigurationLoader(Path path) throws FileNotFoundException {
        super(path);
    }

    @Override
    public void save(ConfigurationNode node) {
        try (final Writer writer = Files.newBufferedWriter(this.getPath())) {
            PROVIDER.save(((BungeeConfigurationNode) node).getHandle(), writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ConfigurationNode load() {
        try (final Reader reader = Files.newBufferedReader(this.getPath())) {
            return new BungeeConfigurationNode(PROVIDER.load(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
