package net.savagedev.tpa.plugin.config.loader;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractConfigurationLoader implements ConfigurationLoader {
    protected final Path path;

    public AbstractConfigurationLoader(Path path) throws FileNotFoundException {
        if (path == null || Files.notExists(path)) {
            throw new FileNotFoundException();
        }
        this.path = path;
    }

    protected Path getPath() {
        return this.path;
    }
}
