package com.demo.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.service.BaseAsyncService;
import com.demo.service.ProxyAsyncService;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.impl.JsonUtil;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;

/**
 * @ClassName: ProxyAsyncServiceImpl
 * @Description: 服务代理业务接口实现类
 * @Author wangpeng
 * @Date 2020-08-18 18:11
 * @Version 1.0
 */
@Component
public class ProxyAsyncServiceImpl implements ProxyAsyncService, BaseAsyncService {

	private final Logger logger = LoggerFactory.getLogger(ProxyAsyncServiceImpl.class);

	@Autowired
	private JDBCClient jdbcClient;

	@Override
	public void close() {

		logger.info("关闭代理实例");
	}

	@Override
	public void queryTag(Handler<AsyncResult<JsonArray>> resultHandler) {

		try {
			String sql = "select * from tag";
			// 构造参数
			JsonArray params = new JsonArray();
			// 执行查询
			jdbcClient.queryWithParams(sql, params, res -> {
				try {
					if (res.succeeded()) {
						// 获取到查询的结果，Vert.x对ResultSet进行了封装
						ResultSet resultSet = res.result();
						// 把ResultSet转为List<JsonObject>形式
						List<JsonObject> rows = resultSet.getRows();

						// 输出结果
						JsonArray jsonArray = (JsonArray) JsonUtil.wrapJsonValue(rows);

						Future.succeededFuture(jsonArray).onComplete(resultHandler);
					} else {
						logger.error("查询失败：{}", res.cause().getMessage());
						resultHandler.handle(Future.failedFuture(res.cause()));
					}
				} catch (Exception re) {
					logger.error("查询结果回调错误：{}", re.getMessage());
					resultHandler.handle(Future.failedFuture(re));
				}
			});
		} catch (Exception e) {
			logger.error("查询异常：{}", e.getMessage());
			resultHandler.handle(Future.failedFuture(e));
		}
	}
}
