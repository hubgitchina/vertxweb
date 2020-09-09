package com.demo.controller;

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

	@RequestMapping("/list")
	public Handler<RoutingContext> list() {

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
}
