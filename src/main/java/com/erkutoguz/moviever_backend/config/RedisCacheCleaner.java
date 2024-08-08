package com.erkutoguz.moviever_backend.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheCleaner {

    private final CacheManager cacheManager;

    public RedisCacheCleaner(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent readyEvent) {
        cacheManager.getCacheNames().parallelStream().forEach(c -> cacheManager.getCache(c).clear());
    }
}
