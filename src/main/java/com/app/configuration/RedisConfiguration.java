package com.app.configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class RedisConfiguration {
	
	@Value(value = "${spring.cache.redis.key-prefix:}")
    private String springCacheRedisKeyPrefix;

    @Value("${spring.cache.redis.use-key-prefix:false}")
    private boolean springCacheRedisUseKeyPrefix;

    private transient CacheKeyPrefix cacheKeyPrefix;
    
    @PostConstruct
    private void onPostConstruct() {
        if (springCacheRedisKeyPrefix != null) {
            springCacheRedisKeyPrefix = springCacheRedisKeyPrefix.trim();
        }
        if (springCacheRedisUseKeyPrefix && springCacheRedisKeyPrefix != null
            && !springCacheRedisKeyPrefix.isEmpty()) {
            cacheKeyPrefix = cacheName -> springCacheRedisKeyPrefix + ":" + cacheName + "::";
        } else {
            cacheKeyPrefix = CacheKeyPrefix.simple();
        }
    }

	@Bean (name="cacheManager")
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

	    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<String, RedisCacheConfiguration>();
	    cacheConfigurations.put("ApiConfiguration", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    cacheConfigurations.put("Client", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    cacheConfigurations.put("CommonConfiguration", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    cacheConfigurations.put("EmailConfiguration", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    cacheConfigurations.put("ApiConfiguration", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    cacheConfigurations.put("SchedulerConfiguration", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    cacheConfigurations.put("SideBarConfiguration", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    cacheConfigurations.put("SmsConfiguration", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    
	    cacheConfigurations.put("Permission", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    cacheConfigurations.put("Role", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    cacheConfigurations.put("User", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofMinutes(15)));
	    cacheConfigurations.put("UserSideBar", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofMinutes(15)));
	    cacheConfigurations.put("LoginAttempt", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    
	    cacheConfigurations.put("Store", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));
	    cacheConfigurations.put("Product", RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheKeyPrefix)
	            .entryTtl(Duration.ofDays(1)));

	    return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory)
	            .withInitialCacheConfigurations(cacheConfigurations).build();
	}

}
