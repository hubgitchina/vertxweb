package com.demo.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName: RedisConfigProperty
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-28 15:11
 * @Version 1.0
 */
@ConfigurationProperties(value = "spring.redisson")
public class RedisConfigProperty {

	private List<String> nodeAddresses;

	// private String password;
	//
	// private String readMode;
	//
	// private Integer idleConnectionTimeout;
	//
	// private Integer retryAttempts;
	//
	// private Integer slaveConnectionMinimumIdleSize;
	//
	// private Integer masterConnectionMinimumIdleSize;
	//
	// private Integer maxConnectionSize;
	//
	// private String keyprefix;

	public List<String> getNodeAddresses() {

		return nodeAddresses;
	}

	public void setNodeAddresses(List<String> nodeAddresses) {

		this.nodeAddresses = nodeAddresses;
	}
}
