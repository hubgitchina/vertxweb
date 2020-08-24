package com.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @ClassName: JdbcDbConfig
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-24 15:13
 * @Version 1.0
 */
@Configuration
public class JdbcDbConfig {

	@Autowired
	private Vertx vertx;

	@Bean
	public JDBCClient getJDBCClient() {

		// 构造数据库的连接信息
		JsonObject dbConfig = new JsonObject();
		dbConfig.put("url",
				"jdbc:mysql://172.16.51.43:3306/huafasj?useUnicode=true&characterEncoding=UTF-8&useSSL=false&tinyInt1isBit=true&zeroDateTimeBehavior=convertToNull");
		/** 升级后驱动类位置改变 */
		dbConfig.put("driver_class", "com.mysql.cj.jdbc.Driver");
		dbConfig.put("user", "huafawy");
		dbConfig.put("password", "Huafagroup.wy2018");
		dbConfig.put("max_pool_size", 30);

		// 创建客户端
		return JDBCClient.createShared(vertx, dbConfig);
	}

}
