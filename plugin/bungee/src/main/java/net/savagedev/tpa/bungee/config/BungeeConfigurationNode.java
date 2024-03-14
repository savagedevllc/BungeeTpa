package net.savagedev.tpa.bungee.config;

import net.md_5.bungee.config.Configuration;
import net.savagedev.tpa.plugin.config.loader.ConfigurationNode;

import java.util.List;

public class BungeeConfigurationNode implements ConfigurationNode {
    private final Configuration configuration;

    private final StringBuilder pathBuilder = new StringBuilder();

    public BungeeConfigurationNode(Configuration configuration) {
        this.configuration = configuration;
    }

    Configuration getHandle() {
        return this.configuration;
    }

    private void reset() {
        this.pathBuilder.delete(0, this.pathBuilder.length());
    }

    @Override
    public ConfigurationNode node(String path) {
        if (this.pathBuilder.length() > 0) {
            this.pathBuilder.append(".");
        }
        this.pathBuilder.append(path);
        return this;
    }

    @Override
    public void set(Object value) {
        this.configuration.set(this.pathBuilder.toString(), value);
        this.reset();
    }

    @Override
    public boolean noChild(String name) {
        final boolean contains = this.configuration.contains(this.pathBuilder.length() > 0 ? this.pathBuilder + "." : name);
        this.reset();
        return !contains;
    }

    @Override
    public boolean getBoolean() {
        final boolean value = this.configuration.getBoolean(this.pathBuilder.toString());
        this.reset();
        return value;
    }

    @Override
    public int getInt() {
        final int value = this.configuration.getInt(this.pathBuilder.toString());
        this.reset();
        return value;
    }

    @Override
    public long getLong() {
        final long value = this.configuration.getLong(this.pathBuilder.toString());
        this.reset();
        return value;
    }

    @Override
    public float getFloat() {
        final float value = this.configuration.getFloat(this.pathBuilder.toString());
        this.reset();
        return value;
    }

    @Override
    public double getDouble() {
        final double value = this.configuration.getDouble(this.pathBuilder.toString());
        this.reset();
        return value;
    }

    @Override
    public String getString(String def) {
        final String value = this.configuration.getString(this.pathBuilder.toString());
        this.reset();
        return value == null ? def : value;
    }

    @Override
    public String getString() {
        final String value = this.configuration.getString(this.pathBuilder.toString());
        this.reset();
        return value;
    }

    @Override
    public <T> List<T> getList(Class<T> t) {
        final List<T> value = (List<T>) this.configuration.getList(this.pathBuilder.toString());
        this.reset();
        return value;
    }
}
