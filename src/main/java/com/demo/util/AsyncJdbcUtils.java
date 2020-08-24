package com.demo.util;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;

/**
 * @ClassName: AsyncJdbcUtils
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-20 11:47
 * @Version 1.0
 */
public class AsyncJdbcUtils {

	private AsyncSQLClient asyncSQLClient;

	public AsyncSQLClient getAsyncSQLClient() {

		return this.asyncSQLClient;
	}

	/**
	 * @Author wangpeng
	 * @Description 新版本 MySQL 8 身份验证方式改变为caching_sha2_password，会导致报错
	 * @Date 15:27
	 * @Param
	 * @return
	 */
	public AsyncJdbcUtils(Vertx vertx) {

		// 构造数据库的连接信息
		JsonObject dbConfig = new JsonObject();
		dbConfig.put("host", "120.79.87.129");
		dbConfig.put("port", 3306);
		dbConfig.put("username", "root");
		dbConfig.put("password", "Huafa@!2017com");
		dbConfig.put("database", "huafaboot");
		// dbConfig.put("maxPoolSize", 30);
		// dbConfig.put("charset", "UTF-8");
		// dbConfig.put("queryTimeout", 10000);

		// 创建客户端
		asyncSQLClient = MySQLClient.createShared(vertx, dbConfig);
	}
}
