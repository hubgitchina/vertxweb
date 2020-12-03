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
import io.vertx.ext.web.handler.StaticHandler;
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

	@RequestMapping("/404")
	public StaticHandler notFind() {

		// 这里不要写代码 不然这里的代码 只会在注册路由的时候 被调用一次
		return StaticHandler.create("templates").setIndexPage("404.html");
	}

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

			LocalDate[] weekDate = DateUtil.getNextWeekByDate(new DateTime(startDate));

			JsonObject data = new JsonObject();
			data.put("recipesId", id);
			data.put("monday", weekDate[0].toString("yyyy-MM-dd"));
			data.put("tuesday", weekDate[1].toString("yyyy-MM-dd"));
			data.put("wednesday", weekDate[2].toString("yyyy-MM-dd"));
			data.put("thursday", weekDate[3].toString("yyyy-MM-dd"));
			data.put("friday", weekDate[4].toString("yyyy-MM-dd"));
			data.put("saturday", weekDate[5].toString("yyyy-MM-dd"));
			data.put("sunday", weekDate[6].toString("yyyy-MM-dd"));

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
				List<JsonArray> foodList = this.setFoodDetail(food, recipesId, setMealId, type,
						userId, date, week);

				map.put("foodList", foodList);
			}
		}
		map.put("setMealList", setMealList);

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

			recipesPublishAsyncService.queryRecipesDetailPage(recipesId, result -> {
				if (result.succeeded()) {
					PageResponeWrapper pageRespone = result.result();

					vertxRequest.buildVertxRespone().responePageSuccess(pageRespone);
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
