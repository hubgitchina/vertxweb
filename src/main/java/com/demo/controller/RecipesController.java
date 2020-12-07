package com.demo.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.demo.annotation.RequestBody;
import com.demo.annotation.RequestMapping;
import com.demo.base.ControllerHandler;
import com.demo.enums.RequestMethod;
import com.demo.model.response.PageResponeWrapper;
import com.demo.service.RecipesPublishAsyncService;
import com.demo.util.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.hutool.core.util.IdUtil;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

/**
 * @ClassName: RecipesController
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:03
 * @Version 1.0
 */
@Component
@RequestMapping("/recipes")
public class RecipesController {

	private final Logger logger = LoggerFactory.getLogger(RecipesController.class);

	@Autowired
	private FreeMarkerTemplateEngine templateEngine;

	@Autowired
	private RecipesPublishAsyncService recipesPublishAsyncService;

	@RequestMapping("/publish")
	public Handler<RoutingContext> publish() {

		return routingContext -> {

			LocalDate[] weekDate = DateUtil.getBeginAndEndOfTheWeek(0);

			JsonObject data = new JsonObject();

			data.put("msg", "本周菜谱");

			data.put("monday", weekDate[0].toString("yyyy-MM-dd"));
			data.put("tuesday", weekDate[1].toString("yyyy-MM-dd"));
			data.put("wednesday", weekDate[2].toString("yyyy-MM-dd"));
			data.put("thursday", weekDate[3].toString("yyyy-MM-dd"));
			data.put("friday", weekDate[4].toString("yyyy-MM-dd"));
			data.put("saturday", weekDate[5].toString("yyyy-MM-dd"));
			data.put("sunday", weekDate[6].toString("yyyy-MM-dd"));

			templateEngine.render(data, "templates/publish", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/queryRecipesPublishPage", method = RequestMethod.POST)
	public ControllerHandler queryRecipesPublishPage() {

		return vertxRequest -> {
			int page = vertxRequest.getParamToInt("page").get();
			int limit = vertxRequest.getParamToInt("limit").get();

			logger.info("pageNo为 {} ，pageSize为 {}", page, limit);

			recipesPublishAsyncService.queryRecipesPublishPage(page, limit, result -> {
				if (result.succeeded()) {
					PageResponeWrapper pageRespone = result.result();

					vertxRequest.buildVertxRespone().responePageSuccess(pageRespone);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/publishRecipes", method = RequestMethod.POST)
	public ControllerHandler publishRecipes() {

		return vertxRequest -> {
			JSONObject param = vertxRequest.getBodyJsonToBean(JSONObject.class);
			String id = param.getString("id");
			logger.info("参数 {}", param);

			recipesPublishAsyncService.publishRecipes(id, result -> {
				if (result.succeeded()) {
					int count = result.result();

					vertxRequest.buildVertxRespone().responeSuccess(count);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	@RequestMapping("/addRecipes")
	public Handler<RoutingContext> addRecipes() {

		return routingContext -> {

			recipesPublishAsyncService.getRecipesPublishNew(res -> {
				DateTime dateTime;
				if (res.succeeded()) {
					JSONObject jsonObject = res.result();
					if (jsonObject.isEmpty()) {
						dateTime = new DateTime();
					} else {
						dateTime = new DateTime(jsonObject.getString("endDate"));
					}

					LocalDate[] weekDate = DateUtil.getNextWeekByDate(dateTime);

					JsonObject data = new JsonObject();
					data.put("monday", weekDate[0].toString("yyyy-MM-dd"));
					data.put("tuesday", weekDate[1].toString("yyyy-MM-dd"));
					data.put("wednesday", weekDate[2].toString("yyyy-MM-dd"));
					data.put("thursday", weekDate[3].toString("yyyy-MM-dd"));
					data.put("friday", weekDate[4].toString("yyyy-MM-dd"));
					data.put("saturday", weekDate[5].toString("yyyy-MM-dd"));
					data.put("sunday", weekDate[6].toString("yyyy-MM-dd"));

					templateEngine.render(data, "templates/add_recipes", res2 -> {
						if (res2.succeeded()) {
							routingContext.response().end(res2.result());
						} else {
							routingContext.fail(res2.cause());
						}
					});
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestMapping("/addRecipesFood")
	public Handler<RoutingContext> addRecipesFood() {

		return routingContext -> {

			String cellIndex = routingContext.request().getParam("cellIndex");
			String rowIndex = routingContext.request().getParam("rowIndex");

			JsonObject data = new JsonObject();
			data.put("cellIndex", cellIndex);
			data.put("rowIndex", rowIndex);

			templateEngine.render(data, "templates/add_recipes_food", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestMapping("/addFood")
	public Handler<RoutingContext> addFood() {

		return routingContext -> {

			String id = routingContext.request().getParam("id");

			JsonObject data = new JsonObject();
			data.put("oldId", id);

			templateEngine.render(data, "templates/add_food", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestMapping("/lookRecipes")
	public Handler<RoutingContext> lookRecipes() {

		return routingContext -> {

			String id = routingContext.request().getParam("id");
			String startDate = routingContext.request().getParam("startDate");
			String endDate = routingContext.request().getParam("endDate");

			DateTime date = new DateTime(startDate);

			JsonObject data = new JsonObject();
			data.put("recipesId", id);
			data.put("monday", date.toString("yyyy-MM-dd"));
			data.put("tuesday",date.plusDays(1).toString("yyyy-MM-dd"));
			data.put("wednesday", date.plusDays(2).toString("yyyy-MM-dd"));
			data.put("thursday", date.plusDays(3).toString("yyyy-MM-dd"));
			data.put("friday", date.plusDays(4).toString("yyyy-MM-dd"));
			data.put("saturday", date.plusDays(5).toString("yyyy-MM-dd"));
			data.put("sunday", date.plusDays(6).toString("yyyy-MM-dd"));

			templateEngine.render(data, "templates/look_recipes", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/saveRecipes", method = RequestMethod.POST)
	public ControllerHandler saveRecipes() {

		return vertxRequest -> {
			JSONObject param = vertxRequest.getBodyJsonToBean(JSONObject.class);
			// JSONObject param = JSON.parseObject(
			// "{\"endDate\":\"2020-12-06\",\"recipesList\":[{\"id\":\"7712\",\"type\":\"早餐\",\"monday\":[{\"id\":\"cc3b\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"8\",\"food\":[{\"id\":\"a56d\",\"dishName\":\"油条\",\"category\":\"1\"},{\"id\":\"da71\",\"dishName\":\"豆浆\",\"category\":\"1\"}]},{\"id\":\"c63c\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"7\",\"food\":[{\"id\":\"9b6a\",\"dishName\":\"面包\",\"category\":\"2\"},{\"id\":\"a812\",\"dishName\":\"牛奶\",\"category\":\"2\"}]}],\"tuesday\":[{\"id\":\"6fe9\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"7\",\"food\":[{\"id\":\"efca\",\"dishName\":\"馒头\",\"category\":\"1\"},{\"id\":\"b2c5\",\"dishName\":\"白粥\",\"category\":\"2\"}]},{\"id\":\"3f67\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"8\",\"food\":[{\"id\":\"5dfc\",\"dishName\":\"肉包\",\"category\":\"1\"},{\"id\":\"0927\",\"dishName\":\"豆浆\",\"category\":\"2\"}]}],\"wednesday\":[{\"id\":\"7413\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"9\",\"food\":[{\"id\":\"9928\",\"dishName\":\"酸辣粉\",\"category\":\"2\"},{\"id\":\"eba3\",\"dishName\":\"酸奶\",\"category\":\"3\"}]},{\"id\":\"8624\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"8\",\"food\":[{\"id\":\"5b62\",\"dishName\":\"热干面\",\"category\":\"1\"},{\"id\":\"669d\",\"dishName\":\"蛋米酒\",\"category\":\"1\"}]}],\"thursday\":[{\"id\":\"8327\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"5\",\"food\":[{\"id\":\"b0c5\",\"dishName\":\"花卷\",\"category\":\"1\"},{\"id\":\"a604\",\"dishName\":\"豆浆\",\"category\":\"1\"}]},{\"id\":\"f6e5\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"7\",\"food\":[{\"id\":\"737b\",\"dishName\":\"肉粽\",\"category\":\"1\"},{\"id\":\"4903\",\"dishName\":\"白粥\",\"category\":\"1\"}]}],\"friday\":[{\"id\":\"668b\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"6\",\"food\":[{\"id\":\"8d72\",\"dishName\":\"鸡蛋肠粉\",\"category\":\"1\"},{\"id\":\"c097\",\"dishName\":\"豆浆\",\"category\":\"2\"}]},{\"id\":\"daee\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"7\",\"food\":[{\"id\":\"e630\",\"dishName\":\"猪肠粉\",\"category\":\"1\"},{\"id\":\"babb\",\"dishName\":\"牛奶\",\"category\":\"2\"}]}],\"saturday\":[],\"sunday\":[],\"LAY_TABLE_INDEX\":0},{\"id\":\"2661\",\"type\":\"午餐\",\"monday\":[{\"id\":\"3ac7\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"15\",\"food\":[{\"id\":\"99dd\",\"dishName\":\"红烧牛肉\",\"category\":\"1\"},{\"id\":\"3439\",\"dishName\":\"白灼菜心\",\"category\":\"2\"}]},{\"id\":\"23ae\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"17\",\"food\":[{\"id\":\"7ec5\",\"dishName\":\"水煮鱼\",\"category\":\"1\"},{\"id\":\"0191\",\"dishName\":\"手撕包菜\",\"category\":\"2\"}]}],\"tuesday\":[{\"id\":\"99ae\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"16\",\"food\":[{\"id\":\"7182\",\"dishName\":\"青椒肉丝\",\"category\":\"2\"},{\"id\":\"28f0\",\"dishName\":\"酸辣土豆丝\",\"category\":\"2\"}]},{\"id\":\"30df\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"17\",\"food\":[{\"id\":\"d6a7\",\"dishName\":\"清蒸鲈鱼\",\"category\":\"2\"},{\"id\":\"39be\",\"dishName\":\"炒生菜\",\"category\":\"2\"}]}],\"wednesday\":[{\"id\":\"e2c5\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"16\",\"food\":[{\"id\":\"5d72\",\"dishName\":\"烧鸭腿\",\"category\":\"1\"},{\"id\":\"b5e4\",\"dishName\":\"麻婆豆腐\",\"category\":\"1\"}]},{\"id\":\"8aaf\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"17\",\"food\":[{\"id\":\"2b05\",\"dishName\":\"烧排骨\",\"category\":\"2\"},{\"id\":\"ed85\",\"dishName\":\"蒜蓉油麦菜\",\"category\":\"1\"}]}],\"thursday\":[{\"id\":\"2445\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"18\",\"food\":[{\"id\":\"26ac\",\"dishName\":\"乳鸽\",\"category\":\"2\"},{\"id\":\"8489\",\"dishName\":\"清炒小白菜\",\"category\":\"1\"}]},{\"id\":\"628d\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"17\",\"food\":[{\"id\":\"0af3\",\"dishName\":\"蒸熊掌\",\"category\":\"1\"},{\"id\":\"b2bf\",\"dishName\":\"地三鲜\",\"category\":\"2\"}]}],\"friday\":[{\"id\":\"b6d9\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"15\",\"food\":[{\"id\":\"7483\",\"dishName\":\"叫花鸡\",\"category\":\"1\"},{\"id\":\"ab80\",\"dishName\":\"红烧茄子\",\"category\":\"1\"}]},{\"id\":\"41b6\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"17\",\"food\":[{\"id\":\"522d\",\"dishName\":\"白切鸡\",\"category\":\"1\"},{\"id\":\"e92c\",\"dishName\":\"炒秋葵\",\"category\":\"1\"}]}],\"saturday\":[],\"sunday\":[],\"LAY_TABLE_INDEX\":1},{\"id\":\"d146\",\"type\":\"晚餐\",\"monday\":[{\"id\":\"eee7\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"17\",\"food\":[{\"id\":\"3aa1\",\"dishName\":\"清蒸石斑鱼\",\"category\":\"1\"},{\"id\":\"087c\",\"dishName\":\"番茄炒蛋\",\"category\":\"1\"}]},{\"id\":\"8138\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"18\",\"food\":[{\"id\":\"2591\",\"dishName\":\"红烧鱼块\",\"category\":\"1\"},{\"id\":\"cf60\",\"dishName\":\"油淋茄子\",\"category\":\"1\"}]}],\"tuesday\":[{\"id\":\"010b\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"18\",\"food\":[{\"id\":\"714e\",\"dishName\":\"卤水鸡腿\",\"category\":\"1\"},{\"id\":\"4879\",\"dishName\":\"腐乳炒通菜\",\"category\":\"1\"}]},{\"id\":\"c188\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"17\",\"food\":[{\"id\":\"6147\",\"dishName\":\"酸菜鱼\",\"category\":\"1\"},{\"id\":\"d3e8\",\"dishName\":\"炒蘑菇\",\"category\":\"1\"}]}],\"wednesday\":[{\"id\":\"35b7\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"15\",\"food\":[{\"id\":\"a4d5\",\"dishName\":\"红菜苔炒腊肉\",\"category\":\"1\"},{\"id\":\"8d96\",\"dishName\":\"炒莴笋\",\"category\":\"2\"}]},{\"id\":\"af4f\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"16\",\"food\":[{\"id\":\"c3bc\",\"dishName\":\"牛肉丸\",\"category\":\"1\"},{\"id\":\"520a\",\"dishName\":\"烤地瓜\",\"category\":\"1\"}]}],\"thursday\":[{\"id\":\"6917\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"15\",\"food\":[{\"id\":\"ad21\",\"dishName\":\"土豆回锅肉\",\"category\":\"1\"},{\"id\":\"7767\",\"dishName\":\"炒苦瓜\",\"category\":\"1\"}]},{\"id\":\"43df\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"18\",\"food\":[{\"id\":\"eb23\",\"dishName\":\"红烧带鱼\",\"category\":\"2\"},{\"id\":\"dcdb\",\"dishName\":\"炒地菜\",\"category\":\"2\"}]}],\"friday\":[{\"id\":\"cd60\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐A\",\"price\":\"17\",\"food\":[{\"id\":\"3297\",\"dishName\":\"烤羊腿\",\"category\":\"2\"},{\"id\":\"4913\",\"dishName\":\"炒豆角\",\"category\":\"2\"}]},{\"id\":\"bcdc\",\"isSetMeal\":\"1\",\"setMealName\":\"套餐B\",\"price\":\"15\",\"food\":[{\"id\":\"821e\",\"dishName\":\"纸包鱼\",\"category\":\"2\"},{\"id\":\"ab99\",\"dishName\":\"韭菜炒蛋\",\"category\":\"2\"}]}],\"saturday\":[],\"sunday\":[],\"LAY_TABLE_INDEX\":2}],\"startDate\":\"2020-11-30\"}");
			LocalDate startDate = new LocalDate(param.getString("startDate"));
			LocalDate endDate = new LocalDate(param.getString("endDate"));
			JSONArray recipesList = param.getJSONArray("recipesList");
			logger.info("参数 {}", param);

			String recipesId = IdUtil.simpleUUID();

			String userId = vertxRequest.getRoutingContext().user().principal().getString("userId");

			List<JsonArray> batchSetMealList = Lists.newArrayList();
			List<JsonArray> batchFoodList = Lists.newArrayList();
			for (int i = 0; i < recipesList.size(); i++) {
				JSONObject tempJson = recipesList.getJSONObject(i);
				JSONArray tempMondy = tempJson.getJSONArray("monday");
				JSONArray tempTuesday = tempJson.getJSONArray("tuesday");
				JSONArray tempWednesday = tempJson.getJSONArray("wednesday");
				JSONArray tempThursday = tempJson.getJSONArray("thursday");
				JSONArray tempFriday = tempJson.getJSONArray("friday");
				JSONArray tempSaturday = tempJson.getJSONArray("saturday");
				JSONArray tempSunday = tempJson.getJSONArray("sunday");

				if (CollectionUtils.isNotEmpty(tempMondy)) {
					Map<String, Object> map = this.setMealDetail(tempMondy, recipesId, i + 1,
							userId, startDate, "1");
					this.setBatchList(map, batchSetMealList, batchFoodList);
				}

				if (CollectionUtils.isNotEmpty(tempTuesday)) {
					Map<String, Object> map = this.setMealDetail(tempTuesday, recipesId, i + 1,
							userId, startDate.plusDays(1), "2");
					this.setBatchList(map, batchSetMealList, batchFoodList);
				}

				if (CollectionUtils.isNotEmpty(tempWednesday)) {
					Map<String, Object> map = this.setMealDetail(tempWednesday, recipesId, i + 1,
							userId, startDate.plusDays(2), "3");
					this.setBatchList(map, batchSetMealList, batchFoodList);
				}

				if (CollectionUtils.isNotEmpty(tempThursday)) {
					Map<String, Object> map = this.setMealDetail(tempThursday, recipesId, i + 1,
							userId, startDate.plusDays(3), "4");
					this.setBatchList(map, batchSetMealList, batchFoodList);
				}

				if (CollectionUtils.isNotEmpty(tempFriday)) {
					Map<String, Object> map = this.setMealDetail(tempFriday, recipesId, i + 1,
							userId, startDate.plusDays(4), "5");
					this.setBatchList(map, batchSetMealList, batchFoodList);
				}

				if (CollectionUtils.isNotEmpty(tempSaturday)) {
					Map<String, Object> map = this.setMealDetail(tempSaturday, recipesId, i + 1,
							userId, startDate.plusDays(5), "6");
					this.setBatchList(map, batchSetMealList, batchFoodList);
				}

				if (CollectionUtils.isNotEmpty(tempSunday)) {
					Map<String, Object> map = this.setMealDetail(tempSunday, recipesId, i + 1,
							userId, startDate.plusDays(6), "7");
					this.setBatchList(map, batchSetMealList, batchFoodList);
				}
			}

			JsonArray recipesJson = new JsonArray();
			this.setCommonInfo(recipesJson, userId, recipesId);
			recipesJson.add(startDate.toString("yyyy-MM-dd"));
			recipesJson.add(endDate.toString("yyyy-MM-dd"));
			recipesJson.add(0);

			Map<String, Object> insertMap = Maps.newHashMapWithExpectedSize(3);
			insertMap.put("recipes", recipesJson);
			insertMap.put("batchSetMealList", batchSetMealList);
			insertMap.put("batchFoodList", batchFoodList);

			recipesPublishAsyncService.saveRecipesPublish(insertMap, result -> {
				if (result.succeeded()) {
					String res = result.result();

					vertxRequest.buildVertxRespone().responeSuccess(res);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	private void setBatchList(Map<String, Object> map, List<JsonArray> batchSetMealList,
			List<JsonArray> batchFoodList) {

		List<JsonArray> setMealList = (List<JsonArray>) map.get("setMealList");
		List<JsonArray> foodList = (List<JsonArray>) map.get("foodList");

		batchSetMealList.addAll(setMealList);
		batchFoodList.addAll(foodList);
	}

	private String setCommonInfo(JsonArray jsonArray, String userId, String id) {

		if (StringUtils.isBlank(id)) {
			id = IdUtil.simpleUUID();
		}

		String now = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");

		jsonArray.add(id);
		jsonArray.add(now);
		jsonArray.add(userId);
		jsonArray.add(now);
		jsonArray.add(userId);
		jsonArray.add(0);

		return id;
	}

	private Map<String, Object> setMealDetail(JSONArray jsonArray, String recipesId, int type,
			String userId, LocalDate date, String week) {

		Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);

		int size = jsonArray.size();
		List<JsonArray> setMealList = Lists.newArrayListWithCapacity(size);
		List<JsonArray> foodList = Lists.newArrayList();
		for (int i = 0; i < size; i++) {
			JSONObject tempJson = jsonArray.getJSONObject(i);

			JsonArray setMealJson = new JsonArray();
			String setMealId = this.setCommonInfo(setMealJson, userId, null);

			setMealJson.add(recipesId);
			setMealJson.add(type);
			setMealJson.add(tempJson.getIntValue("isSetMeal"));
			setMealJson.add(tempJson.getString("setMealName"));
			setMealJson.add(date.toString("yyyy-MM-dd"));
			setMealJson.add(week);
			setMealJson.add(tempJson.getBigDecimal("price"));

			setMealList.add(setMealJson);

			JSONArray food = tempJson.getJSONArray("food");

			if (CollectionUtils.isNotEmpty(food)) {
				List<JsonArray> tempFoodList = this.setFoodDetail(food, recipesId, setMealId, type,
						userId, date, week);

				foodList.addAll(tempFoodList);
			}
		}

		map.put("setMealList", setMealList);
		map.put("foodList", foodList);

		return map;
	}

	private List<JsonArray> setFoodDetail(JSONArray jsonArray, String recipesId, String setMealId,
			int type, String userId, LocalDate date, String week) {

		int size = jsonArray.size();
		List<JsonArray> foodList = Lists.newArrayListWithCapacity(size);
		for (int i = 0; i < size; i++) {
			JSONObject tempJson = jsonArray.getJSONObject(i);

			JsonArray foodJson = new JsonArray();
			String foodId = this.setCommonInfo(foodJson, userId, null);

			foodJson.add(recipesId);
			foodJson.add(setMealId);
			foodJson.add(type);
			foodJson.add(tempJson.getIntValue("category"));
			foodJson.add(tempJson.getString("dishName"));
			foodJson.add(date.toString("yyyy-MM-dd"));
			foodJson.add(week);
			foodJson.add(i + 1);

			foodList.add(foodJson);
		}

		return foodList;
	}

	@RequestBody
	@RequestMapping(value = "/getRecipesById", method = RequestMethod.POST)
	public ControllerHandler getRecipesById() {

		return vertxRequest -> {

			int page = vertxRequest.getParamToInt("page").get();
			int limit = vertxRequest.getParamToInt("limit").get();

			String recipesId = vertxRequest.getParam("recipesId").get();

			logger.info("pageNo为 {} ，pageSize为 {}", page, limit);

			recipesPublishAsyncService.getRecipesDetail(recipesId, result -> {
				if (result.succeeded()) {
					List<JSONObject> recipesList = result.result();

					vertxRequest.buildVertxRespone().responeSuccess(recipesList);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	@RequestMapping("/test")
	public Handler<RoutingContext> test() {

		return routingContext -> {

			JsonObject data = new JsonObject();
			templateEngine.render(data, "templates/publish", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}
}
