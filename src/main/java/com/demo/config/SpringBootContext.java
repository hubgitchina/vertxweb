package com.demo.config;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * @ClassName: SpringBootContext
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:12
 * @Version 1.0
 */
public class SpringBootContext {

	private static ConfigurableApplicationContext applicationContext;

	public static ConfigurableApplicationContext getApplicationContext() {

		return applicationContext;
	}

	public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {

		SpringBootContext.applicationContext = applicationContext;
	}
}
