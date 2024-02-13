package net.savagedev.tpa.plugin.config.loader;

import java.util.List;

public interface ConfigurationNode {
    ConfigurationNode node(String path);

    void set(Object value);

    boolean noChild(String name);

    boolean getBoolean();

    int getInt();

    long getLong();

    float getFloat();

    double getDouble();

    String getString(String def);

    String getString();

    <T> List<T> getList(Class<T> t);
}
