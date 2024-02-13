package net.savagedev.tpa.velocity.config;

import net.savagedev.tpa.plugin.config.loader.ConfigurationNode;

import java.util.List;

public class VelocityConfigurationNode implements ConfigurationNode {
    private final ninja.leaping.configurate.ConfigurationNode node;

    public VelocityConfigurationNode(ninja.leaping.configurate.ConfigurationNode root) {
        this.node = root;
    }

    ninja.leaping.configurate.ConfigurationNode getHandle() {
        return this.node;
    }

    // TODO: This is very lazy: Implement caching of some sort.
    @Override
    public ConfigurationNode node(String path) {
        return new VelocityConfigurationNode(this.node.getNode(path));
    }

    @Override
    public void set(Object value) {
        this.node.setValue(value);
    }

    @Override
    public boolean noChild(String name) {
        return this.node.getNode(name).isEmpty();
    }

    @Override
    public boolean getBoolean() {
        return this.node.getBoolean();
    }

    @Override
    public int getInt() {
        return this.node.getInt();
    }

    @Override
    public long getLong() {
        return this.node.getLong();
    }

    @Override
    public float getFloat() {
        return this.node.getFloat();
    }

    @Override
    public double getDouble() {
        return this.node.getDouble();
    }

    @Override
    public String getString(String def) {
        return this.node.getString(def);
    }

    @Override
    public String getString() {
        return this.node.getString();
    }

    @Override
    public <T> List<T> getList(Class<T> t) {
        return this.node.getList(o -> (T) o);
    }
}
