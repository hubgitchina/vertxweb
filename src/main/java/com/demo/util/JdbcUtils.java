package com.demo.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @ClassName: JdbcUtils
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-19 15:27
 * @Version 1.0
 */
public class JdbcUtils {

	/**
	 * 用于操作数据库的客户端
	 */
	private JDBCClient dbClient;

	/**
	 * 提供一个公共方法来获取客户端
	 */
	public JDBCClient getDbClient() {

		return dbClient;
	}

	public JdbcUtils(Vertx vertx) {

		// 构造数据库的连接信息
		JsonObject dbConfig = new JsonObject();
		dbConfig.put("url",
				"jdbc:mysql://172.16.51.43:3306/huafasj?useUnicode=true&characterEncoding=UTF-8&useSSL=false&tinyInt1isBit=true&zeroDateTimeBehavior=convertToNull");
		// dbConfig.put("driver_class", "com.mysql.jdbc.Driver");
		/** 升级后驱动类位置改变 */
		dbConfig.put("driver_class", "com.mysql.cj.jdbc.Driver");
		dbConfig.put("user", "huafawy");
		dbConfig.put("password", "Huafagroup.wy2018");
		dbConfig.put("max_pool_size", 30);

		// 创建客户端
		dbClient = JDBCClient.createShared(vertx, dbConfig);
	}

}
