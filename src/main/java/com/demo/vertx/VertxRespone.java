package com.demo.vertx;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.model.response.ResponeWrapper;
import com.demo.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * @ClassName: VertxRespone
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 11:35
 * @Version 1.0
 */
public class VertxRespone {

	private final Logger logger = LoggerFactory.getLogger(VertxRespone.class);

	private final RoutingContext routingContext;

	private VertxRespone(RoutingContext routingContext) {

		this.routingContext = routingContext;
	}

	public static VertxRespone build(RoutingContext routingContext) {

		return new VertxRespone(routingContext);
	}

	public void respone(ResponeWrapper responeWrapper) {

		HttpServerResponse httpServerResponse = routingContext.response();
		httpServerResponse.putHeader("Content-Type", "text/json;charset=utf-8");
		try {
			// 转换为JSON 字符串
			httpServerResponse.end(JsonUtils.objectToJson(responeWrapper));
		} catch (JsonProcessingException e) {
			logger.error("serialize object to json fail wrapper: [{}]", responeWrapper);
			e.printStackTrace();
		}
	}

	public void responeSuccess(Object data) {

		respone(new ResponeWrapper(HTTP_OK, data, "操作成功"));
	}

	public void responseFail(String message) {

		respone(new ResponeWrapper(HTTP_INTERNAL_ERROR, null, message));
	}

	public void responeState(boolean state) {

		if (state) {
			respone(ResponeWrapper.RESPONE_SUCCESS);
		} else {
			respone(ResponeWrapper.RESPONE_FAIL);
		}
	}
}
