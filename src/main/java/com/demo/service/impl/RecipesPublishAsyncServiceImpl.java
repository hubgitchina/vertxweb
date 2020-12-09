package com.demo.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
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
									} else {
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
									} else {
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

		// jdbcClient.updateWithParams(sql, params, res -> {
		// if (res.succeeded()) {
		// UpdateResult updateResult = res.result();
		// int count = updateResult.getUpdated();
		//
		// Future.succeededFuture(count).onComplete(resultHandler);
		// } else {
		// logger.error("更新失败：{}", res.cause().getMessage());
		// resultHandler.handle(Future.failedFuture(res.cause()));
		// }
		// });
	}

	@Override
	public void getRecipesPublishNew(Handler<AsyncResult<JSONObject>> resultHandler) {

		String sql = "select max(end_date) as endDate from recipes_publish where is_del = 0";

		// 执行查询
		jdbcClient.queryWithParams(sql, new JsonArray(), res -> {
			if (res.succeeded()) {
				ResultSet resultSet = res.result();
				List<JsonObject> rows = resultSet.getRows();

				if (CollectionUtils.isNotEmpty(rows)) {
					JSONObject fastObject = rows.get(0).mapTo(JSONObject.class);

					Future.succeededFuture(fastObject).onComplete(resultHandler);
				} else {
					Future.succeededFuture(new JSONObject()).onComplete(resultHandler);
				}
			} else {
				logger.error("查询失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	@Override
	public void saveRecipesPublish(Map<String, Object> map,
			Handler<AsyncResult<String>> resultHandler) {

		JsonArray recipes = (JsonArray) map.get("recipes");
		List<JsonArray> batchSetMealList = (List<JsonArray>) map.get("batchSetMealList");
		List<JsonArray> batchFoodList = (List<JsonArray>) map.get("batchFoodList");

		String recipesSql = "insert into recipes_publish (id,create_date,create_by,update_date,update_by,is_del,start_date,end_date,status) values (?,?,?,?,?,?,?,?,?)";
		String setMealSql = "insert into recipes_set_meal (id,create_date,create_by,update_date,update_by,is_del,recipes_publish_id,type,is_set_meal,set_meal_name,date,week,price) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String foodSql = "insert into recipes_publish_set_meal_food (id,create_date,create_by,update_date,update_by,is_del,recipes_publish_id,recipes_set_meal_id,type,category,dish_name,date,week,sort) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		jdbcClient.updateWithParams(recipesSql, recipes, res -> {
			if (res.succeeded()) {
				UpdateResult result = res.result();
				int count = result.getUpdated();

				logger.info("菜谱发布记录插入成功【{}】条", count);

				jdbcClient.getConnection(conRes -> {
					if (conRes.succeeded()) {
						SQLConnection connection = conRes.result();
						connection.batchWithParams(setMealSql, batchSetMealList, batchRes -> {
							if (batchRes.succeeded()) {
								List<Integer> batchResult = batchRes.result();
								logger.info("批量插入菜谱套餐记录结果【{}】", JSON.toJSONString(batchResult));

								connection.batchWithParams(foodSql, batchFoodList, batchFoodRes -> {
									if (batchFoodRes.succeeded()) {
										List<Integer> batchFoodResult = batchFoodRes.result();
										logger.info("批量插入菜谱套餐菜品记录结果【{}】",
												JSON.toJSONString(batchFoodResult));

										Future.succeededFuture("success").onComplete(resultHandler);
									} else {
										logger.error("批量插入菜谱套餐菜品记录失败：{}",
												batchFoodRes.cause().getMessage());
										resultHandler
												.handle(Future.failedFuture(batchFoodRes.cause()));
									}
								});
							} else {
								logger.error("批量插入菜谱套餐记录失败：{}", batchRes.cause().getMessage());
								resultHandler.handle(Future.failedFuture(batchRes.cause()));
							}
						});
					} else {
						logger.error("获取数据库连接失败：{}", conRes.cause().getMessage());
						resultHandler.handle(Future.failedFuture(conRes.cause()));
					}
				});
			} else {
				logger.error("菜谱发布记录插入失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	@Override
	public void getRecipesDetail(String recipesId, String startDate, String endDate,
			Handler<AsyncResult<List<JSONObject>>> resultHandler) {

		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(
				"select r.id, m.id as setMealId, m.set_meal_name, m.price, m.is_set_meal, m.type, m.date, m.week,f.id as foodId, f.category, f.dish_name, f.sort"
						+ " from recipes_publish r left join recipes_set_meal m on m.recipes_publish_id = r.id left join recipes_publish_set_meal_food f on f.recipes_set_meal_id = m.id"
						+ " where r.is_del = 0 and m.is_del = 0 and f.is_del = 0");
		if (StringUtils.isNotBlank(recipesId)) {
			sBuilder.append(" and r.id = ?");
		}
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
			sBuilder.append(" and r.start_date = ? and r.end_date = ?");
		}
		sBuilder.append(" order by m.week, m.type, m.id, f.sort");

		// String sql = "select m.id as setMealId, m.set_meal_name, m.price,
		// m.is_set_meal, m.type, m.date, m.week,f.id as foodId, f.category,
		// f.dish_name, f.sort"
		// + ", case when o.id is null then 0 else 1 end as isOrder"
		// + " from recipes_set_meal m left join recipes_publish_set_meal_food f on
		// f.recipes_set_meal_id = m.id"
		// + " left join order_food_record o on o.recipes_set_meal_id = m.id"
		// + " where m.recipes_publish_id = ? and m.is_del = 0 and f.is_del = 0 order by
		// m.week, m.type, m.id, f.sort";

		JsonArray params = new JsonArray();
		if (StringUtils.isNotBlank(recipesId)) {
			params.add(recipesId);
		}
		if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
			params.add(startDate);
			params.add(endDate);
		}

		// 执行查询
		jdbcClient.queryWithParams(sBuilder.toString(), params, res -> {
			if (res.succeeded()) {
				ResultSet resultSet = res.result();
				List<JsonObject> rows = resultSet.getRows();

				// List<JSONObject> orderList = Lists.newArrayList();

				JSONObject breakfastJson = new JSONObject();
				JSONObject lunchJson = new JSONObject();
				JSONObject dinnerJson = new JSONObject();

				breakfastJson.put("type", "早餐");
				lunchJson.put("type", "午餐");
				dinnerJson.put("type", "晚餐");

				List<JSONObject> monday1 = Lists.newArrayList();
				List<JSONObject> monday2 = Lists.newArrayList();
				List<JSONObject> monday3 = Lists.newArrayList();

				List<JSONObject> tuesday1 = Lists.newArrayList();
				List<JSONObject> tuesday2 = Lists.newArrayList();
				List<JSONObject> tuesday3 = Lists.newArrayList();

				List<JSONObject> wednesday1 = Lists.newArrayList();
				List<JSONObject> wednesday2 = Lists.newArrayList();
				List<JSONObject> wednesday3 = Lists.newArrayList();

				List<JSONObject> thursday1 = Lists.newArrayList();
				List<JSONObject> thursday2 = Lists.newArrayList();
				List<JSONObject> thursday3 = Lists.newArrayList();

				List<JSONObject> friday1 = Lists.newArrayList();
				List<JSONObject> friday2 = Lists.newArrayList();
				List<JSONObject> friday3 = Lists.newArrayList();

				List<JSONObject> saturday1 = Lists.newArrayList();
				List<JSONObject> saturday2 = Lists.newArrayList();
				List<JSONObject> saturday3 = Lists.newArrayList();

				List<JSONObject> sunday1 = Lists.newArrayList();
				List<JSONObject> sunday2 = Lists.newArrayList();
				List<JSONObject> sunday3 = Lists.newArrayList();

				String setMealId = "";
				String setMealName = "";
				String week = "";
				int type = 0;
				// int isOrder = 0;
				BigDecimal price = BigDecimal.ZERO;

				LocalDate localDate = LocalDate.now();
				LocalTime localTime = LocalTime.now();
				LocalTime time12 = new LocalTime(12, 0, 0);

				JSONObject setMealJson;
				List<JSONObject> foodList = null;
				int size = rows.size();
				for (int i = 0; i < size; i++) {
					JsonObject jsonObject = rows.get(i);
					JSONObject tempJson = jsonObject.mapTo(JSONObject.class);
					String tempWeek = tempJson.getString("week");
					int tempType = tempJson.getIntValue("type");
					BigDecimal tempPrice = tempJson.getBigDecimal("price");

					String foodId = tempJson.getString("foodId");
					String dishName = tempJson.getString("dish_name");
					String category = tempJson.getString("category");

					// int tempIsOrder = tempJson.getIntValue("isOrder");

					LocalDate tempDate = new LocalDate(tempJson.getString("date"));

					JSONObject foodJson = new JSONObject();
					foodJson.put("foodId", foodId);
					foodJson.put("dishName", dishName);
					foodJson.put("category", category);

					String tempSetMealName = tempJson.getString("set_meal_name");
					String tempSetMealId = tempJson.getString("setMealId");
					if (i == 0) {
						setMealId = tempSetMealId;
						setMealName = tempSetMealName;
						week = tempWeek;
						type = tempType;
						price = tempPrice;
						// isOrder = tempIsOrder;

						foodList = Lists.newArrayList();
						foodList.add(foodJson);
					} else {
						if (setMealId.equals(tempSetMealId)) {
							foodList.add(foodJson);
						} else {
							setMealJson = new JSONObject();
							setMealJson.put("id", setMealId);
							setMealJson.put("setMealName", setMealName);
							setMealJson.put("price", price);
							setMealJson.put("type", type);
							// setMealJson.put("isChoose", isOrder);

							// if (1 == isOrder) {
							// JSONObject orderJson = new JSONObject(4);
							// orderJson.put("recipesId", recipesId);
							// orderJson.put("setMealId", setMealId);
							// orderJson.put("price", price);
							// orderJson.put("type", type);
							//
							// orderList.add(orderJson);
							// }

							/** 设置已过去日期不能点餐，当前时间超过12点不能对第二天点餐 */
							if (tempDate.isBefore(localDate) || tempDate.isEqual(localDate)) {
								setMealJson.put("readonly", 1);
							} else if (localDate.plusDays(1).isEqual(tempDate)
									&& localTime.isAfter(time12)) {
								setMealJson.put("readonly", 1);
							}

							if (CollectionUtils.isNotEmpty(foodList)) {
								List<JSONObject> tempFoodList = Lists
										.newArrayListWithCapacity(foodList.size());
								tempFoodList.addAll(foodList);

								setMealJson.put("food", tempFoodList);

								foodList.clear();
							}

							addSetMealFood(week, type, setMealJson, monday1, monday2, monday3,
									tuesday1, tuesday2, tuesday3, wednesday1, wednesday2,
									wednesday3, thursday1, thursday2, thursday3, friday1, friday2,
									friday3, saturday1, saturday2, saturday3, sunday1, sunday2,
									sunday3);

							setMealId = tempSetMealId;
							setMealName = tempSetMealName;
							week = tempWeek;
							type = tempType;
							price = tempPrice;
							// isOrder = tempIsOrder;

							foodList.add(foodJson);
						}
					}

					if (i == (size - 1)) {
						setMealJson = new JSONObject();
						setMealJson.put("id", setMealId);
						setMealJson.put("setMealName", setMealName);
						setMealJson.put("price", price);
						setMealJson.put("type", type);
						// setMealJson.put("isChoose", isOrder);
						setMealJson.put("food", foodList);

						// if (1 == isOrder) {
						// JSONObject orderJson = new JSONObject(4);
						// orderJson.put("recipesId", recipesId);
						// orderJson.put("setMealId", setMealId);
						// orderJson.put("price", price);
						// orderJson.put("type", type);
						//
						// orderList.add(orderJson);
						// }

						/** 设置已过去日期不能点餐，当前时间超过12点不能对第二天点餐 */
						if (tempDate.isBefore(localDate) || tempDate.isEqual(localDate)) {
							setMealJson.put("readonly", 1);
						} else if (localDate.plusDays(1).isEqual(tempDate)
								&& localTime.isAfter(time12)) {
							setMealJson.put("readonly", 1);
						}

						addSetMealFood(week, type, setMealJson, monday1, monday2, monday3, tuesday1,
								tuesday2, tuesday3, wednesday1, wednesday2, wednesday3, thursday1,
								thursday2, thursday3, friday1, friday2, friday3, saturday1,
								saturday2, saturday3, sunday1, sunday2, sunday3);
					}
				}

				setRecipesDetail(breakfastJson, monday1, tuesday1, wednesday1, thursday1, friday1,
						saturday1, sunday1);
				setRecipesDetail(lunchJson, monday2, tuesday2, wednesday2, thursday2, friday2,
						saturday2, sunday2);
				setRecipesDetail(dinnerJson, monday3, tuesday3, wednesday3, thursday3, friday3,
						saturday3, sunday3);

				List<JSONObject> recipesList = Lists.newArrayListWithCapacity(3);
				recipesList.add(breakfastJson);
				recipesList.add(lunchJson);
				recipesList.add(dinnerJson);

				Future.succeededFuture(recipesList).onComplete(resultHandler);
			} else {
				logger.error("查询菜谱套餐数据失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	private void setRecipesDetail(JSONObject recipesJson, List<JSONObject> monday,
			List<JSONObject> tuesday, List<JSONObject> wednesday, List<JSONObject> thursday,
			List<JSONObject> friday, List<JSONObject> saturday, List<JSONObject> sunday) {

		recipesJson.put("monday", monday);
		recipesJson.put("tuesday", tuesday);
		recipesJson.put("wednesday", wednesday);
		recipesJson.put("thursday", thursday);
		recipesJson.put("friday", friday);
		recipesJson.put("saturday", saturday);
		recipesJson.put("sunday", sunday);
	}

	private void addSetMealFood(String week, int type, JSONObject setMealJson,
			List<JSONObject> monday1, List<JSONObject> monday2, List<JSONObject> monday3,
			List<JSONObject> tuesday1, List<JSONObject> tuesday2, List<JSONObject> tuesday3,
			List<JSONObject> wednesday1, List<JSONObject> wednesday2, List<JSONObject> wednesday3,
			List<JSONObject> thursday1, List<JSONObject> thursday2, List<JSONObject> thursday3,
			List<JSONObject> friday1, List<JSONObject> friday2, List<JSONObject> friday3,
			List<JSONObject> saturday1, List<JSONObject> saturday2, List<JSONObject> saturday3,
			List<JSONObject> sunday1, List<JSONObject> sunday2, List<JSONObject> sunday3) {

		switch (week) {
		case "1":
			if (1 == type) {
				monday1.add(setMealJson);
			} else if (2 == type) {
				monday2.add(setMealJson);
			} else if (3 == type) {
				monday3.add(setMealJson);
			}
			break;
		case "2":
			if (1 == type) {
				tuesday1.add(setMealJson);
			} else if (2 == type) {
				tuesday2.add(setMealJson);
			} else if (3 == type) {
				tuesday3.add(setMealJson);
			}
			break;
		case "3":
			if (1 == type) {
				wednesday1.add(setMealJson);
			} else if (2 == type) {
				wednesday2.add(setMealJson);
			} else if (3 == type) {
				wednesday3.add(setMealJson);
			}
			break;
		case "4":
			if (1 == type) {
				thursday1.add(setMealJson);
			} else if (2 == type) {
				thursday2.add(setMealJson);
			} else if (3 == type) {
				thursday3.add(setMealJson);
			}
			break;
		case "5":
			if (1 == type) {
				friday1.add(setMealJson);
			} else if (2 == type) {
				friday2.add(setMealJson);
			} else if (3 == type) {
				friday3.add(setMealJson);
			}
			break;
		case "6":
			if (1 == type) {
				saturday1.add(setMealJson);
			} else if (2 == type) {
				saturday2.add(setMealJson);
			} else if (3 == type) {
				saturday3.add(setMealJson);
			}
			break;
		case "7":
			if (1 == type) {
				sunday1.add(setMealJson);
			} else if (2 == type) {
				sunday2.add(setMealJson);
			} else if (3 == type) {
				sunday3.add(setMealJson);
			}
			break;
		default:
			break;
		}
	}

	private void querySetMealFood(String recipesId, String setMealId,
			Handler<AsyncResult<List<JSONObject>>> resultHandler) {

		String sql = "select * from recipes_publish_set_meal_food where is_del = 0 and recipes_publish_id = ? and recipes_set_meal_id = ? order by create_date";

		// 构造参数
		JsonArray params = new JsonArray();
		params.add(recipesId);
		params.add(setMealId);

		// 执行查询
		jdbcClient.queryWithParams(sql, params, res -> {
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
				logger.error("查询失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}
}
