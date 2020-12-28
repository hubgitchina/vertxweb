package com.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import com.demo.config.SpringBootContext;
import com.demo.verticle.VerticleMain;
import com.demo.verticle.WorkVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;

@SpringBootApplication
@ComponentScan(value = { "com.demo.verticle", "com.demo.controller", "com.demo.handler",
		"com.demo.service", "com.demo.config","com.demo.dijkstra" })
public class VertxwebApplication {

	private final Logger logger = LoggerFactory.getLogger(VertxwebApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(VertxwebApplication.class, args);
	}

	@Value("${vertx.workerPoolSize}")
	private int workerPoolSize;

	@Bean
	public Vertx getVertx() {

		EventBusOptions eventBusOptions = new EventBusOptions();

		VertxOptions vertxOptions = new VertxOptions();
		vertxOptions.setWorkerPoolSize(workerPoolSize);
		return Vertx.vertx(vertxOptions);
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
		int eventLoopPoolSize = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;
		logger.info("CPU Core Number：{}", eventLoopPoolSize);
		DeploymentOptions options = new DeploymentOptions().setInstances(eventLoopPoolSize);

		// 部署vertx
		vertx.deployVerticle(VerticleMain.class, options, res -> {
			if (res.succeeded()) {
				logger.info("Deployment id is [{}]", res.result());
			} else {
				logger.info("Deployment failed: {}", res.cause().getMessage());
			}
		});

		WorkVerticle workVerticle = applicationContext.getBean(WorkVerticle.class);

		DeploymentOptions options2 = new DeploymentOptions();
		options2.setWorker(true);
		vertx.deployVerticle(workVerticle, options2, res -> {
			if (res.succeeded()) {
				logger.info("Work Verticle Deployment id is [{}]", res.result());
			} else {
				logger.info("Work Verticle Deployment failed: {}", res.cause().getMessage());
			}
		});
	}
}
