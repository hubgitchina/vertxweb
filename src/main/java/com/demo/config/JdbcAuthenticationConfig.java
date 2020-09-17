package com.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.ext.auth.jdbc.JDBCAuthentication;
import io.vertx.ext.auth.jdbc.JDBCAuthenticationOptions;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * @ClassName: JDBCAuthenticationConfig
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-24 15:13
 * @Version 1.0
 */
@Configuration
public class JdbcAuthenticationConfig {

	@Autowired
	private JDBCClient jdbcClient;

	@Bean
	public JDBCAuthentication getJDBCAuthentication() {

		/**
		 * 使用vertx生成密码，加密盐为【fb721a736266e434】，密码为【1】，生成密码【$sha1$fb721a736266e434$NWoZK3kTsExUV00Ywo1G5jlUKKs】
		 */
		// HashingStrategy hashingStrategy = new HashingStrategyImpl();
		// HashingAlgorithm hashingAlgorithm = new SHA1();
		// ((HashingStrategyImpl) hashingStrategy).add(hashingAlgorithm);
		// String demoPassword = hashingStrategy.hash("sha1", null, "fb721a736266e434",
		// "1");
		// logger.info("生成示例密码：{}", demoPassword);

		JDBCAuthenticationOptions jdbcAuthenticationOptions = new JDBCAuthenticationOptions();
		jdbcAuthenticationOptions
				.setAuthenticationQuery("SELECT password, salt FROM user WHERE login_name = ?");
		JDBCAuthentication authenticationProvider = JDBCAuthentication.create(jdbcClient,
				jdbcAuthenticationOptions);

		return authenticationProvider;
	}

}
