package com.demo.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.demo.model.response.PageResponeWrapper;
import com.demo.service.BaseAsyncService;
import com.demo.service.OrderRecipesAsyncService;
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

/**
 * @ClassName: OrderRecipesAsyncServiceImpl
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:11
 * @Version 1.0
 */
@Component
public class OrderRecipesAsyncServiceImpl implements OrderRecipesAsyncService, BaseAsyncService {

	private final Logger logger = LoggerFactory.getLogger(OrderRecipesAsyncServiceImpl.class);

	@Autowired
	private JDBCClient jdbcClient;

	@Override
	public void queryOrderRecipesPage(int pageNo, int pageSize, String userId,
			Handler<AsyncResult<PageResponeWrapper>> resultHandler) {

		String countSql = "select count(*) from recipes_publish where is_del = 0 and status = 1";

		// 执行查询
		jdbcClient.queryWithParams(countSql, new JsonArray(), countRes -> {
			if (countRes.succeeded()) {
				// 把ResultSet转为List<JsonObject>形式
				List<JsonObject> countResult = countRes.result().getRows();

				int count = countResult.get(0).getInteger("count(*)");
				logger.info("记录总数为：{}", count);

				if (count > 0) {
					// String sql = "select r.*, case when o.id is null then 0 else 1 end as
					// isOrder"
					// + " from recipes_publish r left join order_food_record o on
					// o.recipes_publish_id = r.id"
					// + " where r.is_del = 0 and r.status = 1 group by r.id order by r.create_date
					// desc limit ?,?";

					String sql = "select r.* from recipes_publish r where r.is_del = 0 and r.status = 1 order by r.create_date desc limit ?,?";

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

							List<String> idList = Lists.newArrayListWithCapacity(list.size());

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

								idList.add(fastObject.getString("id"));
							}

							if (CollectionUtils.isNotEmpty(idList)) {
								String orderSql = "select recipes_publish_id from order_food_record where"
										+ " is_del = 0 and order_status = 1 and recipes_publish_id in (?) and order_user_id = ?"
										+ " group by recipes_publish_id";

								// 构造参数
								JsonArray orderParams = new JsonArray();
								orderParams.add(String.join(",", idList));
								orderParams.add(userId);

								// 执行查询
								jdbcClient.queryWithParams(orderSql, orderParams, orderRes -> {
									if (orderRes.succeeded()) {
										ResultSet orderResultSet = orderRes.result();
										List<JsonObject> orderRows = orderResultSet.getRows();

										for (JSONObject jsonObject : list) {
											for (JsonObject orderRow : orderRows) {
												JSONObject orderJson = orderRow
														.mapTo(JSONObject.class);
												String recipesId = orderJson
														.getString("recipes_publish_id");
												if (recipesId.equals(jsonObject.getString("id"))) {
													jsonObject.put("isOrder", 1);

													break;
												}
											}
										}

										PageResponeWrapper pageRespone = new PageResponeWrapper(
												list, pageNo, pageSize, count);

										Future.succeededFuture(pageRespone)
												.onComplete(resultHandler);
									} else {
										logger.error("查询用户【{}】订餐记录失败：{}", userId,
												orderRes.cause().getMessage());
										resultHandler.handle(Future.failedFuture(orderRes.cause()));
									}
								});
							}
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
	public void saveOrderRecipes(List<JsonArray> orderList,
			Handler<AsyncResult<Integer>> resultHandler) {

		String orderSql = "insert into order_food_record (id,create_date,create_by,update_date,update_by,is_del,recipes_publish_id,recipes_set_meal_id,type,order_user_id,order_status,order_time,price) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";

		jdbcClient.getConnection(conRes -> {
			if (conRes.succeeded()) {
				SQLConnection connection = conRes.result();
				connection.batchWithParams(orderSql, orderList, batchRes -> {
					if (batchRes.succeeded()) {
						List<Integer> batchResult = batchRes.result();
						int successCount = 0;
						int failCount = 0;
						for (Integer result : batchResult) {
							if (result == 1) {
								successCount++;
							} else {
								failCount++;
							}
						}
						logger.info("批量插入订餐记录成功【{}】条，失败【{}】条", successCount, failCount);
						Future.succeededFuture(successCount).onComplete(resultHandler);
					} else {
						logger.error("批量插入订餐记录失败：{}", batchRes.cause().getMessage());
						resultHandler.handle(Future.failedFuture(batchRes.cause()));
					}
				});
			} else {
				logger.error("获取数据库连接失败：{}", conRes.cause().getMessage());
				resultHandler.handle(Future.failedFuture(conRes.cause()));
			}
		});
	}

	@Override
	public void queryOrderRecipesList(String recipesId, String userId, String startDate,
			String endDate, Handler<AsyncResult<List<JSONObject>>> resultHandler) {

		StringBuilder sBuilder = new StringBuilder();
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
			sBuilder.append(
					"select o.* from order_food_record o left join recipes_publish r on r.id = o.recipes_publish_id where o.is_del = 0 and o.order_user_id = ? and r.start_date = ? and r.end_date = ? order by o.create_date");
		} else {
			sBuilder.append(
					"select * from order_food_record where is_del = 0 and order_user_id = ? and recipes_publish_id = ? order by create_date");
		}

		// 构造参数
		JsonArray params = new JsonArray();
		params.add(userId);
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
			params.add(startDate);
			params.add(endDate);
		} else {
			params.add(recipesId);
		}
		// 执行查询
		jdbcClient.queryWithParams(sBuilder.toString(), params, res -> {
			if (res.succeeded()) {
				ResultSet resultSet = res.result();
				List<JsonObject> rows = resultSet.getRows();

				List<JSONObject> list = Lists.newArrayListWithCapacity(rows.size());

				for (JsonObject jsonObject : rows) {
					JSONObject fastObject = jsonObject.mapTo(JSONObject.class);

					list.add(fastObject);
				}

				Future.succeededFuture(list).onComplete(resultHandler);
			} else {
				logger.error("查询订餐记录失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}
}
