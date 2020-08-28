package com.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.annotation.RequestMapping;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

/**
 * @ClassName: ThymeleafController
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:03
 * @Version 1.0
 */
@Component
@RequestMapping("/thymeleaf")
public class ThymeleafController {

	private final Logger logger = LoggerFactory.getLogger(ThymeleafController.class);

	@Autowired
	private ThymeleafTemplateEngine templateEngine;

	@RequestMapping("/index")
	public Handler<RoutingContext> index() {

		return routingContext -> {

			JsonObject data = new JsonObject();
			data.put("msg", "Thymeleaf后台变量");
			templateEngine.render(data, "/index", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}
}
