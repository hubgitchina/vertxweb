package com.demo.handler;

import java.util.List;

import com.demo.service.UserAsyncService;
import com.demo.vertx.VertxRequest;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @ClassName: UserHandler
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-19 18:15
 * @Version 1.0
 */
public class UserHandler {

	public static void getAllUser(VertxRequest vertxRequest, UserAsyncService userAsyncService) {

		userAsyncService.getAllUserClose(res -> {
			if (res.succeeded()) {
				JsonArray result = res.result();
				vertxRequest.buildVertxRespone().responeSuccess(result);
			} else {
				vertxRequest.buildVertxRespone().responseFail(res.cause().getMessage());
			}
		});
	}
}
