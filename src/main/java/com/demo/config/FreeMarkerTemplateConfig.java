package com.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

/**
 * @ClassName: FreeMarkerTemplateConfig
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-27 18:11
 * @Version 1.0
 */
@Configuration
public class FreeMarkerTemplateConfig {

	@Autowired
	private Vertx vertx;

	@Bean
	public FreeMarkerTemplateEngine freeMarkerTemplateEngine() {

		FreeMarkerTemplateEngine engine = FreeMarkerTemplateEngine.create(vertx);
		return engine;
	}
}
