package com.demo.vertx;

import java.io.IOException;
import java.util.Optional;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.util.JsonUtils;

import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * @ClassName: VertxRequest
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 11:33
 * @Version 1.0
 */
public class VertxRequest {

	private final Logger logger = LoggerFactory.getLogger(VertxRequest.class);

	private final RoutingContext routingContext;

	private VertxRequest(RoutingContext routingContext) {

		this.routingContext = routingContext;
	}

	public static VertxRequest build(RoutingContext routingContext) {

		return new VertxRequest(routingContext);
	}

	/**
	 * 构建 VertxRespone
	 *
	 * @return VertxRespone
	 */
	public VertxRespone buildVertxRespone() {

		return VertxRespone.build(routingContext);
	}

	/**
	 * 获取AccessToken 令牌
	 *
	 * @return
	 */
	public Optional<String> getAccessToken() {

		HttpServerRequest request = routingContext.request();
		String accesstoken = null;
		if ((accesstoken = request.getHeader("accesstoken")) != null) {
		} else if ((accesstoken = request.getParam("accesstoken")) != null) {
		} else {
			Cookie cookie = request.getCookie("accesstoken");
			if (cookie != null) {
				accesstoken = cookie.getValue();
			}
		}
		return Optional.ofNullable(accesstoken);
	}

	/**
	 * 获取参数 -String
	 *
	 * @param key
	 * @return
	 */
	public Optional<String> getParam(String key) {

		HttpServerRequest request = routingContext.request();
		return Optional.ofNullable(request.getParam(key));
	}

	/**
	 * 获取参数 -Integer
	 *
	 * @param key
	 * @return
	 */
	public Optional<Integer> getParamToInt(String key) {

		Integer value = null;
		Optional<String> param = getParam(key);
		if (param.isPresent()) {
			value = Integer.valueOf(param.get());
		}
		return Optional.ofNullable(value);
	}

	/**
	 * 获取参数 -Byte
	 *
	 * @param key
	 * @return
	 */
	public Optional<Byte> getParamToByte(String key) {

		Byte value = null;
		Optional<String> param = getParam(key);
		if (param.isPresent()) {
			value = Byte.valueOf(param.get());
		}
		return Optional.ofNullable(value);
	}

	/**
	 * 获取参数 -Boolean
	 *
	 * @param key
	 * @return
	 */
	public Optional<Boolean> getParamToBoolean(String key) {

		Boolean value = null;
		Optional<String> param = getParam(key);
		if (param.isPresent()) {
			value = Boolean.valueOf(param.get());
		}
		return Optional.ofNullable(value);
	}

	/**
	 * 获取参数 - JavaBean
	 *
	 * @param <T>
	 * @param paramClass
	 * @return
	 */
	public <T> T getBodyJsonToBean(Class<?> paramClass) {

		String bodyAsString = routingContext.getBodyAsString();
		T param = null;
		try {
			param = (T) JsonUtils.jsonToObject(bodyAsString, paramClass);
		} catch (IOException e) {
			logger.warn("getParamBean json to object fial body as string: [{}]", bodyAsString, e);
			throw new ValidationException("json to object param fail");
		}
		return param;
	}
}
