package com.demo.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.vertx.VertxRequest;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * @ClassName: ControllerHandler
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 11:29
 * @Version 1.0
 */
public interface ControllerHandler extends Handler<RoutingContext> {

	Logger logger = LoggerFactory.getLogger(ControllerHandler.class);

	@Override
	default void handle(RoutingContext event) {

		logger.info("进入 Handler 处理器, 请求路径：{}", event.request().path());
		handle(VertxRequest.build(event));
	}

	void handle(VertxRequest vertxRequest);
}
