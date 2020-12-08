package com.demo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
import com.demo.service.OrderRecipesAsyncService;
import com.google.common.collect.Lists;

import cn.hutool.core.util.IdUtil;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

/**
 * @ClassName: OrderRecipesController
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:03
 * @Version 1.0
 */
@Component
@RequestMapping("/order")
public class OrderRecipesController {

	private final Logger logger = LoggerFactory.getLogger(OrderRecipesController.class);

	@Autowired
	private FreeMarkerTemplateEngine templateEngine;

	@Autowired
	private OrderRecipesAsyncService orderRecipesAsyncService;

	@RequestMapping("/recipes")
	public Handler<RoutingContext> recipes() {

		return routingContext -> {

			JsonObject data = new JsonObject();

			templateEngine.render(data, "templates/order_recipes_list", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/queryOrderRecipesPage", method = RequestMethod.POST)
	public ControllerHandler queryOrderRecipesPage() {

		return vertxRequest -> {
			int page = vertxRequest.getParamToInt("page").get();
			int limit = vertxRequest.getParamToInt("limit").get();

			logger.info("pageNo为 {} ，pageSize为 {}", page, limit);

			orderRecipesAsyncService.queryOrderRecipesPage(page, limit, result -> {
				if (result.succeeded()) {
					PageResponeWrapper pageRespone = result.result();

					vertxRequest.buildVertxRespone().responePageSuccess(pageRespone);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	@RequestMapping("/orderRecipes")
	public Handler<RoutingContext> orderRecipes() {

		return routingContext -> {

			String id = routingContext.request().getParam("id");
			String startDate = routingContext.request().getParam("startDate");
			String endDate = routingContext.request().getParam("endDate");

			DateTime date = new DateTime(startDate);

			JsonObject data = new JsonObject();
			data.put("recipesId", id);
			data.put("monday", date.toString("yyyy-MM-dd"));
			data.put("tuesday", date.plusDays(1).toString("yyyy-MM-dd"));
			data.put("wednesday", date.plusDays(2).toString("yyyy-MM-dd"));
			data.put("thursday", date.plusDays(3).toString("yyyy-MM-dd"));
			data.put("friday", date.plusDays(4).toString("yyyy-MM-dd"));
			data.put("saturday", date.plusDays(5).toString("yyyy-MM-dd"));
			data.put("sunday", date.plusDays(6).toString("yyyy-MM-dd"));

			String userId = routingContext.user().principal().getString("userId");

			orderRecipesAsyncService.queryOrderRecipesList(id, userId, null, null, result -> {
				if (result.succeeded()) {
					List<JSONObject> orderList = result.result();

					data.put("orderList", orderList);

					templateEngine.render(data, "templates/order_recipes", res -> {
						if (res.succeeded()) {
							routingContext.response().end(res.result());
						} else {
							routingContext.fail(res.cause());
						}
					});
				} else {
					routingContext.fail(result.cause());
				}
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/saveOrderRecipes", method = RequestMethod.POST)
	public ControllerHandler saveOrderRecipes() {

		return vertxRequest -> {
			JSONArray params = vertxRequest.getBodyJsonToBean(JSONArray.class);
			logger.info("参数 {}", params);

			String userId = vertxRequest.getRoutingContext().user().principal().getString("userId");

			List<JsonArray> orderList = Lists.newArrayListWithCapacity(params.size());
			for (int i = 0; i < params.size(); i++) {
				JSONObject tempJson = params.getJSONObject(i);
				String recipesId = tempJson.getString("recipesId");
				String setMealId = tempJson.getString("setMealId");
				BigDecimal price = tempJson.getBigDecimal("price");
				int type = tempJson.getIntValue("type");

				JsonArray orderJson = new JsonArray();
				this.setCommonInfo(orderJson, userId, null);
				orderJson.add(recipesId);
				orderJson.add(setMealId);
				orderJson.add(type);
				orderJson.add(userId);
				orderJson.add(1);
				orderJson.add(orderJson.getString(1));
				orderJson.add(price);

				orderList.add(orderJson);
			}

			orderRecipesAsyncService.saveOrderRecipes(orderList, result -> {
				if (result.succeeded()) {
					int count = result.result();

					vertxRequest.buildVertxRespone().responeSuccess(count);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	@RequestMapping("/lookOrderRecipes")
	public Handler<RoutingContext> lookRecipes() {

		return routingContext -> {

			String id = routingContext.request().getParam("id");
			String startDate = routingContext.request().getParam("startDate");
			String endDate = routingContext.request().getParam("endDate");

			DateTime date = new DateTime(startDate);

			JsonObject data = new JsonObject();
			data.put("recipesId", id);
			data.put("monday", date.toString("yyyy-MM-dd"));
			data.put("tuesday", date.plusDays(1).toString("yyyy-MM-dd"));
			data.put("wednesday", date.plusDays(2).toString("yyyy-MM-dd"));
			data.put("thursday", date.plusDays(3).toString("yyyy-MM-dd"));
			data.put("friday", date.plusDays(4).toString("yyyy-MM-dd"));
			data.put("saturday", date.plusDays(5).toString("yyyy-MM-dd"));
			data.put("sunday", date.plusDays(6).toString("yyyy-MM-dd"));

			String userId = routingContext.user().principal().getString("userId");

			orderRecipesAsyncService.queryOrderRecipesList(id, userId, null, null, result -> {
				if (result.succeeded()) {
					List<JSONObject> orderList = result.result();

					data.put("orderList", orderList);

					templateEngine.render(data, "templates/look_order_recipes", res -> {
						if (res.succeeded()) {
							routingContext.response().end(res.result());
						} else {
							routingContext.fail(res.cause());
						}
					});
				} else {
					routingContext.fail(result.cause());
				}
			});
		};
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
}
