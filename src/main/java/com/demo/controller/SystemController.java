package com.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.annotation.RequestMapping;

import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

/**
 * @ClassName: SystemController
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:03
 * @Version 1.0
 */
@Component
@RequestMapping("/system")
public class SystemController {

	private final Logger logger = LoggerFactory.getLogger(SystemController.class);

	@Autowired
	private FreeMarkerTemplateEngine templateEngine;

	@RequestMapping("/404")
	public StaticHandler notFind() {

		// 这里不要写代码 不然这里的代码 只会在注册路由的时候 被调用一次
		return StaticHandler.create("templates").setIndexPage("404.html");
	}
}
