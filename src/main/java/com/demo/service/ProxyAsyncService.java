package com.demo.service;

import com.demo.service.impl.ProxyAsyncServiceImpl;

import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @ClassName: ProxyAsyncService
 * @Description: 服务代理业务接口
 * @Author wangpeng
 * @Date 2020-08-18 18:05
 * @Version 1.0
 */
@ProxyGen
@VertxGen
public interface ProxyAsyncService {

	String SERVICE_ADDRESS = "test-service-proxy-address";

	static ProxyAsyncService create(JDBCClient jdbcClient) {

		return new ProxyAsyncServiceImpl(jdbcClient);
	}

	static ProxyAsyncService createProxy(Vertx vertx, String address) {

		return new ProxyAsyncServiceVertxEBProxy(vertx, address);
		// return new
		// ServiceProxyBuilder(vertx).setAddress(address).build(ProxyAsyncService.class);
	}

	/**
	 * @Author wangpeng
	 * @Description 直接使用jdbcClient的查询方法，框架会自动关闭连接
	 * @Date 11:24
	 * @Param
	 * @return
	 */
	// @Fluent
	void queryTag(Handler<AsyncResult<JsonArray>> resultHandler);

	@ProxyClose
	void close();

}
