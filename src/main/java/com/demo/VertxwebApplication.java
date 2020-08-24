package com.demo;

import io.vertx.core.DeploymentOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import com.demo.config.SpringBootContext;
import com.demo.verticle.VerticleMain;

import io.vertx.core.Vertx;

@SpringBootApplication
@ComponentScan(value = { "com.demo.verticle", "com.demo.controller", "com.demo.handler", "com.demo.service" })
public class VertxwebApplication {

	private final Logger logger = LoggerFactory.getLogger(VertxwebApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(VertxwebApplication.class, args);
	}

	@Bean
	public Vertx getVertx() {

		return Vertx.vertx();
	}

	/**
	 * 监听SpringBoot 启动完毕 开始部署Vertx
	 *
	 * @param event
	 */
	@EventListener
	public void deployVertx(ApplicationReadyEvent event) {

		ConfigurableApplicationContext applicationContext = event.getApplicationContext();
		SpringBootContext.setApplicationContext(applicationContext);

		VerticleMain verticleMain = applicationContext.getBean(VerticleMain.class);

//		Vertx vertx = Vertx.vertx();
		Vertx vertx = getVertx();

		// 部署vertx
//		vertx.deployVerticle(verticleMain, handler -> {
//			logger.info("vertx deploy state [{}]", handler.succeeded());
//		});

		DeploymentOptions options = new DeploymentOptions().setWorker(true);
		vertx.deployVerticle(verticleMain, options);
	}
}
