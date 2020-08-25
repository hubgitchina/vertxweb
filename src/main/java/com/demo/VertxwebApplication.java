package com.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import com.demo.config.SpringBootContext;
import com.demo.verticle.WorkVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

@SpringBootApplication
@ComponentScan(value = { "com.demo.verticle", "com.demo.controller", "com.demo.handler",
		"com.demo.service", "com.demo.config" })
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

		// VerticleMain verticleMain = applicationContext.getBean(VerticleMain.class);

		// Vertx vertx = Vertx.vertx();
		Vertx vertx = getVertx();

		// 部署vertx
		// vertx.deployVerticle(verticleMain, res -> {
		// if (res.succeeded()) {
		// logger.info("Deployment id is [{}]", res.result());
		// } else {
		// logger.info("Deployment failed!");
		// }
		// });

		/** 使用verticle名称,指定verticle实例数量，部署多个实例可以充分利用所有的核心 */
		DeploymentOptions options = new DeploymentOptions().setInstances(24);

		// 部署vertx
		vertx.deployVerticle("com.demo.verticle.VerticleMain", options, res -> {
			if (res.succeeded()) {
				logger.info("Deployment id is [{}]", res.result());
			} else {
				logger.info("Deployment failed!");
			}
		});

		WorkVerticle workVerticle = applicationContext.getBean(WorkVerticle.class);

		DeploymentOptions options2 = new DeploymentOptions().setWorker(true);
		vertx.deployVerticle(workVerticle, options2, res -> {
			if (res.succeeded()) {
				logger.info("Work Verticle Deployment id is [{}]", res.result());
			} else {
				logger.info("Work Verticle Deployment failed!");
			}
		});
	}
}
