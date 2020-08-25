package com.demo.verticle;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.impl.JsonUtil;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

import java.util.List;

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
    private JDBCClient jdbcClient;

	@Override
	public void start() throws Exception {

		super.start();

		// 每个Vertx实例默认是单例
		EventBus eb = vertx.eventBus();

		// 注册处理器,消费com.demo发送的消息
		eb.consumer("com.demo", message -> {
			logger.info("普通消费者：{}", message.body());

			try {
				jdbcClient.getConnection(res -> {
					// String name = Thread.currentThread().getName();
					// System.out.println(name);
					if (res.succeeded()) {
						SQLConnection connection = res.result();
						connection.query("select * from user", res2 -> {
							if (res2.succeeded()) {
								ResultSet rs = res2.result();

								List<JsonObject> rows = rs.getRows();

								logger.info("WorkVerticle 查询成功：{}", rows.size());

                                message.reply(JsonUtil.wrapJsonValue(rows));

//								Future.succeededFuture(rows).onComplete(resultHandler);
							} else {
								logger.error("查询失败：{}", res2.cause().getMessage());
//								resultHandler.handle(Future.failedFuture(res2.cause()));
							}
							connection.close();
						});
					} else {
						logger.error("连接失败：{}", res.cause());
//						resultHandler.handle(Future.failedFuture(res.cause()));
					}
				});
			} catch (Exception e) {
//				resultHandler.handle(Future.failedFuture(e));
			}

			// 回复生产者,send才能接受
//			message.reply("收到了!");

		}).completionHandler(res -> {
			// 注册完成后通知事件,适用于集群中比较慢的情况下
			logger.info("注册处理器结果：{}", res.succeeded());
		});

		// 撤销处理器
		// consumer.unregister();
	}
}
