package com.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import auth.MyJDBCAuth;
import io.vertx.core.Vertx;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @ClassName: MyJdbcAuthConfig
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-24 15:13
 * @Version 1.0
 */
@Configuration
public class MyJdbcAuthConfig {

	@Autowired
	private Vertx vertx;

	@Autowired
	private JDBCClient jdbcClient;

	@Bean
	public MyJDBCAuth getMyJDBCAuth() {

		return MyJDBCAuth.create(vertx, jdbcClient);
	}

}
