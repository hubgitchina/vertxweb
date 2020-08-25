package com.demo.service.impl;

import java.util.List;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.annotation.AsyncServiceHandler;
import com.demo.service.BaseAsyncService;
import com.demo.service.UserAsyncService;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @ClassName: UserAsyncServiceImpl
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:11
 * @Version 1.0
 */
@Component
@AsyncServiceHandler
public class UserAsyncServiceImpl implements UserAsyncService, BaseAsyncService {

	private final Logger logger = LoggerFactory.getLogger(UserAsyncServiceImpl.class);

	@Autowired
	private JDBCClient jdbcClient;

	@Autowired
	private Vertx vertx;

	@Override
	public void getAllUser(Handler<AsyncResult<JsonArray>> resultHandler) {

		try {
			EventBus eb = vertx.eventBus();

//			eb.publish("com.demo", "query");

			JsonObject json = new JsonObject().put("id", 1);
			eb.request("com.demo", json, res -> {
				if (res.succeeded()) {
					JsonArray result = (JsonArray) res.result().body();
					Future.succeededFuture(result).onComplete(resultHandler);
					logger.info("查询成功：{}", result.size());
				} else {
					logger.error("查询失败：{}", res.cause().getMessage());
					resultHandler.handle(Future.failedFuture(res.cause()));
				}
			});
		} catch (Exception e) {
			resultHandler.handle(Future.failedFuture(e));
		}

		// try {
		// jdbcClient.getConnection(res -> {
		// // String name = Thread.currentThread().getName();
		// // System.out.println(name);
		// if (res.succeeded()) {
		// SQLConnection connection = res.result();
		// connection.query("select * from user", res2 -> {
		// if (res2.succeeded()) {
		// ResultSet rs = res2.result();
		//
		// List<JsonObject> rows = rs.getRows();
		//
		// logger.info("查询成功：{}", rows.size());
		//
		// Future.succeededFuture(rows).onComplete(resultHandler);
		// } else {
		// logger.error("查询失败：{}", res2.cause().getMessage());
		// resultHandler.handle(Future.failedFuture(res2.cause()));
		// }
		// connection.close();
		// });
		// } else {
		// logger.error("连接失败：{}", res.cause());
		// resultHandler.handle(Future.failedFuture(res.cause()));
		// }
		// });
		// } catch (Exception e) {
		// resultHandler.handle(Future.failedFuture(e));
		// }
	}
}
