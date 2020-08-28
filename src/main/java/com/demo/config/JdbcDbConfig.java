package com.demo.config;

import com.demo.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
	public DruidDataSourceProperty getDataSource(){
		return new DruidDataSourceProperty();
	}

	@Bean
	public JDBCClient getJDBCClient() {

		// 使用c3p0连接池构造数据库的连接信息
//		JsonObject dbConfig = new JsonObject();
//		dbConfig.put("url",
//				"jdbc:mysql://172.16.51.43:3306/huafasj?useUnicode=true&characterEncoding=UTF-8&useSSL=false&tinyInt1isBit=true&zeroDateTimeBehavior=convertToNull");
//		/** 升级后驱动类位置改变 */
//		dbConfig.put("driver_class", "com.mysql.cj.jdbc.Driver");
//		dbConfig.put("user", "huafawy");
//		dbConfig.put("password", "Huafagroup.wy2018");
//		dbConfig.put("max_pool_size", 30);

		// 使用Druid连接池构造数据库的连接信息
//		JsonObject dbConfig = new JsonObject();
//		dbConfig.put("provider_class", "com.demo.config.DruidDataSourceProvider");
//		dbConfig.put("url",
//				"jdbc:mysql://172.16.51.43:3306/huafasj?useUnicode=true&characterEncoding=UTF-8&useSSL=false&tinyInt1isBit=true&zeroDateTimeBehavior=convertToNull");
//		/** 升级后驱动类位置改变 */
//		dbConfig.put("driverClassName", "com.mysql.cj.jdbc.Driver");
//		dbConfig.put("username", "huafawy");
//		dbConfig.put("password", "Huafagroup.wy2018");

		// 创建客户端
		DruidDataSourceProperty dataSource = getDataSource();
		JsonObject druidConfig = JsonObject.mapFrom(dataSource);
//		System.out.println(druidConfig);
		return JDBCClient.createShared(vertx, druidConfig);
	}

}
