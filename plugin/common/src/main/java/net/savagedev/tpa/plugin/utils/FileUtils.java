package net.savagedev.tpa.plugin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {
    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    public static void copyResource(String resourceName, Path output) throws IOException {
        try (final InputStream stream = FileUtils.getResource(resourceName)) {
            if (stream == null) {
                throw new IOException("Cannot create file. Resource stream null!");
            }
            Files.copy(stream, output);
        }
    }

    private static InputStream getResource(String name) {
        final URL url = FileUtils.class.getClassLoader().getResource(name);
        if (url != null) {
            try {
                final URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
