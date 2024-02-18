package net.savagedev.tpa.plugin.model.server.manager;

import net.savagedev.tpa.plugin.model.AbstractManager;
import net.savagedev.tpa.plugin.model.server.Server;

import java.util.Locale;
import java.util.function.Function;

public class ServerManager extends AbstractManager<String, Server<?>> {
    public ServerManager(Function<String, Server<?>> loader) {
        super(loader);
    }

    @Override
    protected String sanitizeId(String id) {
        return id.toLowerCase(Locale.ROOT);
    }
}
