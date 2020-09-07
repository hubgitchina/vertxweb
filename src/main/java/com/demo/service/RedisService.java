package com.demo.service;

import java.util.concurrent.TimeUnit;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @ClassName: RedisService
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:05
 * @Version 1.0
 */
public interface RedisService {

	/**
	 * @Author wangpeng
	 * @Description Redis通过key设置对应value值
	 * @Date 17:46
	 * @Param
	 * @return
	 */
	void setRedisKey(String key, String value, Handler<AsyncResult<Boolean>> resultHandler);

	/**
	 * @Author wangpeng
	 * @Description Redis通过key设置对应value值，并根据时间单位指定过期时间
	 * @Date 11:20
	 * @Param
	 * @return
	 */
	void setRedisKeyExpire(String key, String expire, String value, TimeUnit timeUnit,
			Handler<AsyncResult<Boolean>> resultHandler);

	/**
	 * @Author wangpeng
	 * @Description Redis通过key获取对应值
	 * @Date 17:47
	 * @Param
	 * @return
	 */
	void getRedisValue(String key, Handler<AsyncResult<String>> resultHandler);
}
