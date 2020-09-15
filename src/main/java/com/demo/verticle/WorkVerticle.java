package com.demo.verticle;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.service.ProxyAsyncService;
import com.demo.service.UserAsyncService;
import com.demo.util.EventBusConstants;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * @ClassName: WorkVerticle
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-25 17:38
 * @Version 1.0
 */
@Component
public class WorkVerticle extends AbstractVerticle {

	private final Logger logger = LoggerFactory.getLogger(WorkVerticle.class);

	@Autowired
	private UserAsyncService userAsyncService;

	@Autowired
	private JDBCClient jdbcClient;

	@Override
	public void start() throws Exception {

		super.start();

		// 每个Vertx实例默认是单例
		EventBus eb = vertx.eventBus();

		// 注册处理器,消费com.demo发送的消息
		eb.consumer(EventBusConstants.QUERY_ALL_USER, message -> {
			logger.info("消费者，收到查询参数：{}", message.body());

			try {
				userAsyncService.getAllUser(result -> {
					if (result.succeeded()) {
						JsonArray rows = result.result();
						Future.succeededFuture(rows);
						message.replyAndRequest(rows);
					} else {
						Future.failedFuture(result.cause());
						message.fail(HTTP_INTERNAL_ERROR, result.cause().getMessage());
					}
				});
			} catch (Exception e) {
				Future.failedFuture(e);
				message.fail(HTTP_INTERNAL_ERROR, e.getMessage());
			}
		}).completionHandler(res -> {
			// 注册完成后通知事件,适用于集群中比较慢的情况下
			logger.info("注册处理器结果：{}", res.succeeded());
		});

		/** 注册代理服务 */
		ProxyAsyncService proxyService = ProxyAsyncService.create(jdbcClient);
		MessageConsumer<JsonObject> consumerProxy = new ServiceBinder(vertx)
				.setAddress(ProxyAsyncService.SERVICE_ADDRESS)
				.register(ProxyAsyncService.class, proxyService);

		// 撤销处理器
		// consumer.unregister();
	}
}
