package com.demo.service.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.demo.model.response.PageResponeWrapper;
import com.demo.service.BaseAsyncService;
import com.demo.service.RecipesPublishAsyncService;
import com.demo.util.DateUtil;
import com.google.common.collect.Lists;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;

/**
 * @ClassName: RecipesPublishAsyncServiceImpl
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:11
 * @Version 1.0
 */
@Component
public class RecipesPublishAsyncServiceImpl
		implements RecipesPublishAsyncService, BaseAsyncService {

	private final Logger logger = LoggerFactory.getLogger(RecipesPublishAsyncServiceImpl.class);

	@Autowired
	private JDBCClient jdbcClient;

	@Override
	public void queryRecipesPublishPage(int pageNo, int pageSize,
			Handler<AsyncResult<PageResponeWrapper>> resultHandler) {

		String countSql = "select count(*) from recipes_publish where is_del = 0";

		// 执行查询
		jdbcClient.queryWithParams(countSql, new JsonArray(), countRes -> {
			if (countRes.succeeded()) {
				// 把ResultSet转为List<JsonObject>形式
				List<JsonObject> countResult = countRes.result().getRows();

				int count = countResult.get(0).getInteger("count(*)");
				logger.info("记录总数为：{}", count);

				if (count > 0) {
					String sql = "select * from recipes_publish where is_del = 0 order by create_date desc limit ?,?";

					// 构造参数
					JsonArray params = new JsonArray();
					params.add((pageNo - 1) * pageSize);
					params.add(pageSize);

					// 执行查询
					jdbcClient.queryWithParams(sql, params, res -> {
						if (res.succeeded()) {
							// 获取到查询的结果，Vert.x对ResultSet进行了封装
							ResultSet resultSet = res.result();
							// 把ResultSet转为List<JsonObject>形式
							List<JsonObject> rows = resultSet.getRows();
							List<JSONObject> list = Lists.newArrayListWithCapacity(rows.size());

							DateTime nowDate = new DateTime();
							for (JsonObject jsonObject : rows) {
								JSONObject fastObject = jsonObject.mapTo(JSONObject.class);

								DateTime begin = new DateTime(fastObject.getString("start_date"));
								DateTime end = new DateTime(fastObject.getString("end_date"));
								boolean contained = DateUtil.isExistScope(begin, end, nowDate);
								if (contained) {
									fastObject.put("contained", 0);
								} else {
									if (nowDate.isBefore(begin)) {
										fastObject.put("contained", 1);
									} else {
										fastObject.put("contained", 2);
									}
								}

								list.add(fastObject);
							}

							PageResponeWrapper pageRespone = new PageResponeWrapper(list, 1, 10,
									count);

							Future.succeededFuture(pageRespone).onComplete(resultHandler);
						} else {
							logger.error("查询失败：{}", res.cause().getMessage());
							resultHandler.handle(Future.failedFuture(res.cause()));
						}
					});
				} else {
					// Map<String, Object> mapEmpty = Collections.EMPTY_MAP;
					Future.succeededFuture(new PageResponeWrapper()).onComplete(resultHandler);
				}
			} else {
				logger.error("查询失败：{}", countRes.cause().getMessage());
				resultHandler.handle(Future.failedFuture(countRes.cause()));
			}
		});

	}

	@Override
	public void publishRecipes(String id, Handler<AsyncResult<Integer>> resultHandler) {

		String sql = "update recipes_publish set status = ?, update_date = ?, publish_time = ? where id = ?";

		String now = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
		System.out.println(now);
		JsonArray params = new JsonArray();
		params.add(1);
		params.add(now);
		params.add(now);
		params.add(id);

		jdbcClient.getConnection(conRes -> {
			if (conRes.succeeded()) {
				SQLConnection connection = conRes.result();
				/** 开启事务 */
				connection.setAutoCommit(false, commitRes -> {
					if (commitRes.succeeded()) {
						// 事务开启成功 执行crud操作
						connection.updateWithParams(sql, params, updateRes -> {
							if (updateRes.succeeded()) {
								// 提交事务
								connection.commit(rx -> {
									if (rx.succeeded()) {
										// 事务提交成功
										logger.info("事务提交成功");

										UpdateResult updateResult = updateRes.result();
										int count = updateResult.getUpdated();

										Future.succeededFuture(count).onComplete(resultHandler);
									}else{
										logger.error("事务提交失败：{}", rx.cause().getMessage());
										resultHandler.handle(Future.failedFuture(rx.cause()));
									}
								});
							} else {
								logger.error("更新失败：{}", updateRes.cause().getMessage());

								connection.rollback(rb -> {
									if (rb.succeeded()) {
										// 事务回滚成功
										logger.error("事务回滚成功");
									}else{
										logger.error("事务回滚失败：{}", rb.cause().getMessage());
										resultHandler.handle(Future.failedFuture(rb.cause()));
									}
								});
							}
						});
					} else {
						logger.error("开启事务失败：{}", commitRes.cause().getMessage());
						resultHandler.handle(Future.failedFuture(commitRes.cause()));
					}
				});
			} else {
				logger.error("获取数据库连接失败：{}", conRes.cause().getMessage());
				resultHandler.handle(Future.failedFuture(conRes.cause()));
			}
		});

//		jdbcClient.updateWithParams(sql, params, res -> {
//			if (res.succeeded()) {
//				UpdateResult updateResult = res.result();
//				int count = updateResult.getUpdated();
//
//				Future.succeededFuture(count).onComplete(resultHandler);
//			} else {
//				logger.error("更新失败：{}", res.cause().getMessage());
//				resultHandler.handle(Future.failedFuture(res.cause()));
//			}
//		});
	}
}