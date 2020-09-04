package com.demo.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.service.BaseAsyncService;
import com.demo.service.UserAsyncService;
import com.google.common.collect.Lists;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.impl.JsonUtil;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;

/**
 * @ClassName: UserAsyncServiceImpl
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:11
 * @Version 1.0
 */
@Component
public class UserAsyncServiceImpl implements UserAsyncService, BaseAsyncService {

	private final Logger logger = LoggerFactory.getLogger(UserAsyncServiceImpl.class);

	@Autowired
	private JDBCClient jdbcClient;

	@Override
	public void getAllUserClose(Handler<AsyncResult<JsonArray>> resultHandler) {

		try {
			jdbcClient.getConnection(res -> {
				if (res.succeeded()) {
					SQLConnection connection = res.result();
					connection.query("select * from user", res2 -> {
						if (res2.succeeded()) {
							ResultSet rs = res2.result();

							List<JsonObject> rows = rs.getRows();

							JsonArray jsonArray = (JsonArray) JsonUtil.wrapJsonValue(rows);

							Future.succeededFuture(jsonArray).onComplete(resultHandler);
						} else {
							logger.error("查询失败：{}", res2.cause().getMessage());
							resultHandler.handle(Future.failedFuture(res2.cause()));
						}
						connection.close(done -> {
							if (done.failed()) {
								// throw new VertxException(done.cause());
								logger.error("数据库连接关闭失败：{}", done.cause().getMessage());
								resultHandler.handle(Future.failedFuture(done.cause()));
							}
						});
					});
				} else {
					logger.error("连接失败：{}", res.cause().getMessage());
					resultHandler.handle(Future.failedFuture(res.cause()));
				}
			});
		} catch (Exception e) {
			logger.error("查询异常：{}", e.getMessage());
			resultHandler.handle(Future.failedFuture(e));
		}
	}

	@Override
	public void getAllUser(Handler<AsyncResult<JsonArray>> resultHandler) {

		try {
			String sql = "select * from user";
			// 构造参数
			JsonArray params = new JsonArray();
			// 执行查询
			jdbcClient.queryWithParams(sql, params, res -> {
				try {
					if (res.succeeded()) {
						// 获取到查询的结果，Vert.x对ResultSet进行了封装
						ResultSet resultSet = res.result();
						// 把ResultSet转为List<JsonObject>形式
						List<JsonObject> rows = resultSet.getRows();

						// 输出结果
						JsonArray jsonArray = (JsonArray) JsonUtil.wrapJsonValue(rows);

						Future.succeededFuture(jsonArray).onComplete(resultHandler);
					} else {
						logger.error("查询失败：{}", res.cause().getMessage());
						resultHandler.handle(Future.failedFuture(res.cause()));
					}
				} catch (Exception re) {
					logger.error("查询结果回调错误：{}", re.getMessage());
					resultHandler.handle(Future.failedFuture(re));
				}
			});
		} catch (Exception e) {
			logger.error("查询异常：{}", e.getMessage());
			resultHandler.handle(Future.failedFuture(e));
		}
	}

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
				resultHandler.handle(Future.succeededFuture(true));
			} else {
				logger.error("Redis设置值失败：{}", result.cause().getMessage());
				resultHandler.handle(Future.failedFuture(result.cause()));
			}
		});

		// redisAPI.getset(key, value, result -> {
		// if (result.succeeded()) {
		// logger.info("Redis设置值成功");
		// } else {
		// logger.error("Redis设置值失败：{}", result.cause().getMessage());
		// }
		// });
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
