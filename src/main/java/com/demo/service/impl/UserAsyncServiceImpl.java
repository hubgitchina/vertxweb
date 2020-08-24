package com.demo.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.annotation.AsyncServiceHandler;
import com.demo.service.BaseAsyncService;
import com.demo.service.UserAsyncService;
import com.demo.util.JdbcUtils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

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

	// @Autowired
	// private UserService userService;
	//
	// @Override
	// public void listUsers(User user, Handler<AsyncResult<List<User>>>
	// resultHandler) {
	// try {
	// List<User> userList = userService.list(new QueryWrapper<>(user));
	// Future.succeededFuture(userList).setHandler(resultHandler);
	// } catch (Exception e) {
	// resultHandler.handle(Future.failedFuture(e));
	// }
	// }

	@Autowired
	private Vertx vertx;

	@Override
	public void getAllUser(Handler<AsyncResult<List<JsonObject>>> resultHandler) {

		try {
			// 获取到数据库连接的客户端
			JDBCClient jdbcClient = new JdbcUtils(vertx).getDbClient();
			jdbcClient.getConnection(res -> {
				if (res.succeeded()) {
					SQLConnection connection = res.result();
					connection.query("select * from user", res2 -> {
						if (res2.succeeded()) {
							ResultSet rs = res2.result();

							List<JsonObject> rows = rs.getRows();

							logger.info("查询成功：{}", rows.size());

							Future.succeededFuture(rows).onComplete(resultHandler);
						} else {
							logger.error("查询失败：{}", res2.cause().getMessage());
							resultHandler.handle(Future.failedFuture(res2.cause()));
						}
					});
				} else {
					// Failed to get connection - deal with it
					logger.error("连接失败：{}", res.cause());
					resultHandler.handle(Future.failedFuture(res.cause()));
				}
			});
		} catch (Exception e) {
			resultHandler.handle(Future.failedFuture(e));
		}
	}
}
