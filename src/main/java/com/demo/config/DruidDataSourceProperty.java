package com.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName: DruidDataSourceProperty
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-28 15:11
 * @Version 1.0
 */
@ConfigurationProperties(value = "first.spring.datasource")
public class DruidDataSourceProperty {

	private String provider_class;

	private String url;

	private String username;

	private String password;

	private String driverClassName;

	private Integer initialSize;

	private Integer minIdle;

	private Integer maxActive;

	private String validationQuery;

	private Boolean testWhileIdle;

	private Boolean testOnBorrow;

	private Boolean testOnReturn;

	private Boolean poolPreparedStatements;

	private Integer maxPoolPreparedStatementPerConnectionSize;

	private String filters;

	private String connectionProperties;

	public String getProvider_class() {

		return provider_class;
	}

	public void setProvider_class(String provider_class) {

		this.provider_class = provider_class;
	}

	public String getUrl() {

		return url;
	}

	public void setUrl(String url) {

		this.url = url;
	}

	public String getUsername() {

		return username;
	}

	public void setUsername(String username) {

		this.username = username;
	}

	public String getPassword() {

		return password;
	}

	public void setPassword(String password) {

		this.password = password;
	}

	public String getDriverClassName() {

		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {

		this.driverClassName = driverClassName;
	}

	public Integer getInitialSize() {

		return initialSize;
	}

	public void setInitialSize(Integer initialSize) {

		this.initialSize = initialSize;
	}

	public Integer getMinIdle() {

		return minIdle;
	}

	public void setMinIdle(Integer minIdle) {

		this.minIdle = minIdle;
	}

	public Integer getMaxActive() {

		return maxActive;
	}

	public void setMaxActive(Integer maxActive) {

		this.maxActive = maxActive;
	}

	public String getValidationQuery() {

		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {

		this.validationQuery = validationQuery;
	}

	public Boolean getTestWhileIdle() {

		return testWhileIdle;
	}

	public void setTestWhileIdle(Boolean testWhileIdle) {

		this.testWhileIdle = testWhileIdle;
	}

	public Boolean getTestOnBorrow() {

		return testOnBorrow;
	}

	public void setTestOnBorrow(Boolean testOnBorrow) {

		this.testOnBorrow = testOnBorrow;
	}

	public Boolean getTestOnReturn() {

		return testOnReturn;
	}

	public void setTestOnReturn(Boolean testOnReturn) {

		this.testOnReturn = testOnReturn;
	}

	public Boolean getPoolPreparedStatements() {

		return poolPreparedStatements;
	}

	public void setPoolPreparedStatements(Boolean poolPreparedStatements) {

		this.poolPreparedStatements = poolPreparedStatements;
	}

	public Integer getMaxPoolPreparedStatementPerConnectionSize() {

		return maxPoolPreparedStatementPerConnectionSize;
	}

	public void setMaxPoolPreparedStatementPerConnectionSize(
			Integer maxPoolPreparedStatementPerConnectionSize) {

		this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
	}

	public String getFilters() {

		return filters;
	}

	public void setFilters(String filters) {

		this.filters = filters;
	}

	public String getConnectionProperties() {

		return connectionProperties;
	}

	public void setConnectionProperties(String connectionProperties) {

		this.connectionProperties = connectionProperties;
	}
}
