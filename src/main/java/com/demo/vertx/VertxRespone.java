package com.demo.vertx;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demo.model.response.PageResponeWrapper;
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
		// httpServerResponse.putHeader("Access-Control-Allow-Origin", "*");
		// httpServerResponse.putHeader("Access-Control-Allow-Credentials", "true");
		// httpServerResponse.putHeader("Content-Disposition", "attachment");

		try {
			// 转换为JSON 字符串
			httpServerResponse.end(JsonUtils.objectToJson(responeWrapper));
		} catch (JsonProcessingException e) {
			logger.error("serialize object to json fail wrapper: [{}]", responeWrapper);
			e.printStackTrace();
		}
	}

	public void responePage(PageResponeWrapper pageResponeWrapper) {

		HttpServerResponse httpServerResponse = routingContext.response();
		httpServerResponse.putHeader("Content-Type", "text/json;charset=utf-8");
		// httpServerResponse.putHeader("Access-Control-Allow-Origin", "*");
		// httpServerResponse.putHeader("Access-Control-Allow-Credentials", "true");
		// httpServerResponse.putHeader("Content-Disposition", "attachment");

		try {
			// 转换为JSON 字符串
			httpServerResponse.end(JsonUtils.objectToJson(pageResponeWrapper));
		} catch (JsonProcessingException e) {
			logger.error("serialize object to json fail wrapper: [{}]", pageResponeWrapper);
			e.printStackTrace();
		}
	}

	public void responseFile(String fileName, String filePath, String contentType) {

		if (StringUtils.isBlank(contentType)) {
			contentType = "application/octet-stream";
		}

		String downloadFileName = null;
		try {
			downloadFileName = new String(fileName.getBytes("gb2312"), "ISO_8859_1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		File file = new File(filePath);

		// 设置响应头，把文件名字设置好
		routingContext.response().putHeader("Content-Disposition",
				"attachment; filename=" + downloadFileName);
		// 设置文件长度
		routingContext.response().putHeader("Content-Length", String.valueOf(file.length()));
		// 解决编码问题
		routingContext.response().putHeader("Content-Type", contentType);

		routingContext.response().sendFile(filePath);
	}

	public void responeSuccess(Object data) {

		respone(new ResponeWrapper(HTTP_OK, data, "操作成功"));
	}

	public void responePageSuccess(PageResponeWrapper pageResponeWrapper) {

		pageResponeWrapper.setCode(HTTP_OK);
		responePage(pageResponeWrapper);
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
