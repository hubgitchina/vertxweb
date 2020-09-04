package com.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisClientType;
import io.vertx.redis.client.RedisOptions;

/**
 * @ClassName: RedisConfig
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-24 15:13
 * @Version 1.0
 */
@Configuration
public class RedisConfig {

	@Autowired
	private Vertx vertx;

	@Bean
	public RedisConfigProperty getRedisConfig() {

		return new RedisConfigProperty();
	}

	@Bean
	public RedisAPI getRedisClient() {

		RedisConfigProperty redisConfig = getRedisConfig();

		RedisOptions redisOptions = new RedisOptions();
		redisOptions.setEndpoints(redisConfig.getNodeAddresses());
		/** 因配置Redis为集群模式，这里也需设置Type为 CLUSTER（集群模式） */
		redisOptions.setType(RedisClientType.CLUSTER);
		Redis redis = Redis.createClient(vertx, redisOptions);

		return RedisAPI.api(redis);
	}

}
