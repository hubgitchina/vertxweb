package com.demo.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.service.RedisService;
import com.google.common.collect.Lists;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;

/**
 * @ClassName: RedisServiceImpl
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:11
 * @Version 1.0
 */
@Component
public class RedisServiceImpl implements RedisService {

	private final Logger logger = LoggerFactory.getLogger(RedisServiceImpl.class);

	@Autowired
	private RedisAPI redisAPI;

	@Override
	public void setRedisKey(String key, String value, Handler<AsyncResult<Boolean>> resultHandler) {

		List<String> param = Lists.newArrayListWithCapacity(2);
		param.add(key);
		param.add(value);
		redisAPI.set(param, result -> {
			if (result.succeeded()) {
				logger.info("Redis设置值成功");
				redisAPI.expire(key, "60", handler -> {
					if (handler.succeeded()) {
						logger.info("Redis设置值【{}】过期时间为【{}】秒", key, 60);
					} else {
						logger.error("Redis设置值【{}】过期时间失败", key);
					}
				});
				resultHandler.handle(Future.succeededFuture(true));
			} else {
				logger.error("Redis设置值失败：{}", result.cause().getMessage());
				resultHandler.handle(Future.failedFuture(result.cause()));
			}
		});
	}

	public void getSetRedisKey(String key, String value,
			Handler<AsyncResult<Boolean>> resultHandler) {

		redisAPI.getset(key, value, result -> {
			if (result.succeeded()) {
				logger.info("Redis设置值成功");
				resultHandler.handle(Future.succeededFuture(true));
			} else {
				logger.error("Redis设置值失败：{}", result.cause().getMessage());
				resultHandler.handle(Future.failedFuture(result.cause()));
			}
		});
	}

	@Override
	public void getRedisValue(String key, Handler<AsyncResult<String>> resultHandler) {

		redisAPI.get(key, result -> {
			if (result.succeeded()) {
				Response response = result.result();
				if (response != null) {
					String value = response.toString();
					logger.info("Redis获取值成功，{}", value);
					resultHandler.handle(Future.succeededFuture(value));
				} else {
					logger.error("Redis获取值失败，未找到数据");
					resultHandler.handle(Future.failedFuture("Redis获取值失败，未找到数据"));
				}
			} else {
				logger.error("Redis获取值失败：{}", result.cause().getMessage());
				resultHandler.handle(Future.failedFuture(result.cause()));
			}
		});
	}
}
