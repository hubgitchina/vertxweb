package com.demo.controller;

import java.util.Map;

import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.annotation.RequestBlockingHandler;
import com.demo.annotation.RequestBody;
import com.demo.annotation.RequestMapping;
import com.demo.base.ControllerHandler;
import com.demo.enums.RequestMethod;
import com.demo.handler.UserHandler;
import com.demo.model.LoginModel;
import com.demo.model.response.ResponeWrapper;
import com.demo.service.UserAsyncService;
import com.demo.util.EventBusConstants;
import com.google.common.collect.Maps;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

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

	@Autowired
	private FreeMarkerTemplateEngine templateEngine;

	@RequestMapping("/list")
	public Handler<RoutingContext> list() {

		return routingContext -> {

			JsonObject data = new JsonObject();
			data.put("msg", "FreeMarker后台变量");
			templateEngine.render(data, "templates/free", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}
}
