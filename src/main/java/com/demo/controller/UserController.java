package com.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.annotation.RequestBlockingHandler;
import com.demo.annotation.RequestBody;
import com.demo.annotation.RequestMapping;
import com.demo.base.ControllerHandler;
import com.demo.enums.RequestMethod;
import com.demo.handler.UserHandler;
import com.demo.model.LoginModel;
import com.demo.model.response.ResponeWrapper;
import com.demo.service.UserAsyncService;
import com.google.common.collect.Maps;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * @ClassName: UserController
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:03
 * @Version 1.0
 */
@Component
@RequestMapping("/user")
public class UserController {

	@RequestMapping("/index")
	public StaticHandler index() {

		// 这里不要写代码 不然这里的代码 只会在注册路由的时候 被调用一次
		return StaticHandler.create();
	}

	@RequestMapping("/userInfo")
	public ControllerHandler userInfo() {

		// 这里不要写代码 不然这里的代码 只会在注册路由的时候 被调用一次
		return vertxRequest -> {
			// 接口所执行的逻辑代码一定要写到这里
			Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
			map.put("name", "李四");
			map.put("age", "18");
			vertxRequest.buildVertxRespone().responeSuccess(map);
		};
	}

	@RequestBlockingHandler
	@RequestMapping("/findGirlFriend")
	public ControllerHandler findGirlFriend() {

		return vertxRequest -> {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			vertxRequest.buildVertxRespone().respone(new ResponeWrapper(10001, null, "未找到女盆友"));
		};
	}

	@RequestBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ControllerHandler userLogin() {

		return vertxRequest -> {
			LoginModel loginModel = vertxRequest.getBodyJsonToBean(LoginModel.class);
			vertxRequest.buildVertxRespone().responeSuccess(loginModel);
		};
	}

	@Autowired
	private UserAsyncService userAsyncService;

	@RequestBody
	@RequestMapping(value = "/getAllUser1")
	public ControllerHandler getAllUser1() {

		return vertxRequest -> {
			UserHandler.getAllUser(vertxRequest, userAsyncService);
		};
	}

	@RequestBlockingHandler
	@RequestBody
	@RequestMapping(value = "/getAllUser2")
	public ControllerHandler getAllUser2() {

		return vertxRequest -> {
			userAsyncService.getAllUser(result -> {
				if (result.succeeded()) {
					List<JsonObject> rows = result.result();
					vertxRequest.buildVertxRespone().responeSuccess(rows);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}
}
