package com.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import io.vertx.core.Vertx;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

/**
 * @ClassName: ThymeleafTemplateConfig
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-27 18:11
 * @Version 1.0
 */
@Configuration
public class ThymeleafTemplateConfig {

	@Autowired
	private Vertx vertx;

	@Bean
	public ClassLoaderTemplateResolver templateResolver() {

		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setPrefix("webroot/templates");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		return resolver;
	}

	@Bean
	public ThymeleafTemplateEngine templateEngine() {

		ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create(vertx);
		engine.getThymeleafTemplateEngine().setTemplateResolver(templateResolver());
		return engine;
	}
}
