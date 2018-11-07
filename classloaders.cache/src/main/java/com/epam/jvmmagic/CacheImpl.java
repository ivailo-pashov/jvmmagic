package com.epam.jvmmagic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class CacheImpl {

    private static final CacheImpl INSTANCE = new CacheImpl();

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    private CacheImpl() {

    }

    public static CacheImpl getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(String key, Function<String, T> compute) {
        return (T) cache.computeIfAbsent(key, compute);
    }
}
