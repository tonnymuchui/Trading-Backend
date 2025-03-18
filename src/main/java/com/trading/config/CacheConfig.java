package com.trading.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "coinList", "marketChart", "coinDetails", "searchCoin", "top50Coins", "trendingCoins"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(59, TimeUnit.MINUTES) // Cache expires after 59 minutes
                .maximumSize(100)); // Maximum of 100 entries in the cache
        return cacheManager;
    }
}