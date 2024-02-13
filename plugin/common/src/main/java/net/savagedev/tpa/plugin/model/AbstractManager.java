package net.savagedev.tpa.plugin.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractManager<I, T> {
    private final Map<I, T> objects = new HashMap<>();

    private final Function<I, T> loader;

    public AbstractManager(Function<I, T> loader) {
        this.loader = loader;
    }

    public T remove(I id) {
        return this.objects.remove(this.sanitizeId(id));
    }

    public Optional<T> getOrLoad(I id) {
        final I sanitizedId = this.sanitizeId(id);
        if (!this.objects.containsKey(sanitizedId)) {
            this.objects.put(sanitizedId, this.loader.apply(id));
        }
        return Optional.ofNullable(this.objects.get(sanitizedId));
    }

    public Optional<T> get(I id) {
        return Optional.ofNullable(this.objects.get(this.sanitizeId(id)));
    }

    public Map<I, T> getAll() {
        return this.objects;
    }

    protected abstract I sanitizeId(I id);
}
