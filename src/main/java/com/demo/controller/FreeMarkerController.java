package com.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.util.DateUtil;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.demo.annotation.RequestMapping;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

import java.util.List;

/**
 * @ClassName: FreeMarkerController
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:03
 * @Version 1.0
 */
@Component
@RequestMapping("/freeMarker")
public class FreeMarkerController {

	private final Logger logger = LoggerFactory.getLogger(FreeMarkerController.class);

	@Value("${file.staticAccessPath}")
	private String staticAccessPath;

	@Autowired
	private FreeMarkerTemplateEngine templateEngine;

	@RequestMapping("/login")
	public Handler<RoutingContext> login() {

		return routingContext -> {

			JsonObject data = new JsonObject();
			templateEngine.render(data, "templates/login", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestMapping("/free")
	public Handler<RoutingContext> demo() {

		return routingContext -> {

			JsonObject data = new JsonObject();
			data.put("msg", "FreeMarker后台变量");
			data.put("path", staticAccessPath + "测试图片.jpg");
			templateEngine.render(data, "templates/free", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestMapping("/list")
	public Handler<RoutingContext> list() {

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

			templateEngine.render(data, "templates/list", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestMapping("/system/about")
	public Handler<RoutingContext> about() {

		return routingContext -> {

			JsonObject data = new JsonObject();
			data.put("msg", "FreeMarker后台变量");
			templateEngine.render(data, "templates/about", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}
}
