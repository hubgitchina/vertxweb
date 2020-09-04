package com.demo.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * @ClassName: UserAsyncService
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:05
 * @Version 1.0
 */
@ProxyGen
public interface UserAsyncService {

	/**
	 * @Author wangpeng
	 * @Description 使用SQLConnection查询，需要手动关闭连接
	 * @Date 11:23
	 * @Param
	 * @return
	 */
	void getAllUserClose(Handler<AsyncResult<JsonArray>> resultHandler);

	/**
	 * @Author wangpeng
	 * @Description 直接使用jdbcClient的查询方法，框架会自动关闭连接
	 * @Date 11:24
	 * @Param
	 * @return
	 */
	void getAllUser(Handler<AsyncResult<JsonArray>> resultHandler);

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
	 * @Description Redis通过key获取对应值
	 * @Date 17:47
	 * @Param
	 * @return
	 */
	void getRedisValue(String key, Handler<AsyncResult<String>> resultHandler);
}
