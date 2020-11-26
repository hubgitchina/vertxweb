package com.demo.controller;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.demo.annotation.RequestBody;
import com.demo.annotation.RequestMapping;
import com.demo.base.ControllerHandler;
import com.demo.enums.RequestMethod;
import com.demo.model.response.PageResponeWrapper;
import com.demo.service.RecipesPublishAsyncService;
import com.demo.util.DateUtil;

import io.vertx.core.Handler;
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
