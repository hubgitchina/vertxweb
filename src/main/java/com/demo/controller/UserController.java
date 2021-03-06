package com.demo.controller;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.annotation.RequestBlockingHandler;
import com.demo.annotation.RequestBody;
import com.demo.annotation.RequestMapping;
import com.demo.base.ControllerHandler;
import com.demo.handler.UserHandler;
import com.demo.model.response.ResponeWrapper;
import com.demo.service.ProxyAsyncService;
import com.demo.service.RedisService;
import com.demo.service.UserAsyncService;
import com.demo.util.EventBusConstants;
import com.google.common.collect.Maps;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
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

	private final Logger logger = LoggerFactory.getLogger(UserController.class);

	@RequestMapping("/index")
	public StaticHandler index() {

		// 这里不要写代码 不然这里的代码 只会在注册路由的时候 被调用一次
		return StaticHandler.create();
	}

	@RequestMapping("/list")
	public StaticHandler list() {

		// 这里不要写代码 不然这里的代码 只会在注册路由的时候 被调用一次
		return StaticHandler.create("webroot/user").setIndexPage("user.html");
	}

	@RequestMapping("/addUser")
	public StaticHandler addUser() {

		// 这里不要写代码 不然这里的代码 只会在注册路由的时候 被调用一次
		return StaticHandler.create("webroot/user").setIndexPage("add_user.html");
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

	@Autowired
	private UserAsyncService userAsyncService;

	@RequestBody
	@RequestMapping(value = "/getAllUser1")
	public ControllerHandler getAllUser1() {

		return vertxRequest -> {
			UserHandler.getAllUser(vertxRequest, userAsyncService);
		};
	}

	// @RequestBlockingHandler
	@RequestBody
	@RequestMapping(value = "/getAllUser2")
	public ControllerHandler getAllUser2() {

		return vertxRequest -> {
			userAsyncService.getAllUserClose(result -> {
				if (result.succeeded()) {
					JsonArray rows = result.result();
					vertxRequest.buildVertxRespone().responeSuccess(rows);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	@Autowired
	private Vertx vertx;

	@RequestBody
	@RequestMapping(value = "/getAllUser3")
	public ControllerHandler getAllUser3() {

		return vertxRequest -> {
			try {
				EventBus eb = vertx.eventBus();
				JsonObject json = new JsonObject().put("id", 1);
				eb.request(EventBusConstants.QUERY_ALL_USER, json, res -> {
					if (res.succeeded()) {
						JsonArray result = (JsonArray) res.result().body();
						vertxRequest.buildVertxRespone().responeSuccess(result);
					} else {
						vertxRequest.buildVertxRespone().responseFail(res.cause().getMessage());
					}
				});
			} catch (Exception e) {
				vertxRequest.buildVertxRespone().responseFail(e.getMessage());
			}
		};
	}

	/**
	 * @Author wangpeng
	 * @Description Vertx-Jdbc-Client的实现就是在 worker 线程池跑查询，获取结果后在调用的线程(eventloop)回调
	 * @Date 10:13
	 * @Param
	 * @return
	 */
	@RequestBody
	@RequestMapping(value = "/getAllUser")
	public ControllerHandler getAllUser() {

		return vertxRequest -> {
			userAsyncService.getAllUser(result -> {
				if (result.succeeded()) {
					JsonArray rows = result.result();
					vertxRequest.buildVertxRespone().responeSuccess(rows);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	/**
	 * @Author wangpeng
	 * @Description 通过vertx-service-proxy服务代理调用远程服务方法
	 * @Date 14:29
	 * @Param
	 * @return
	 */
	@RequestBody
	@RequestMapping(value = "/getAllTag")
	public ControllerHandler getAllTag() {

		return vertxRequest -> {
			try {
				/** 创建服务代理，代替事件总线，调用远程服务方法 */
				ProxyAsyncService proxyService = ProxyAsyncService.createProxy(vertx,
						ProxyAsyncService.SERVICE_ADDRESS);
				proxyService.queryTag(res -> {
					if (res.succeeded()) {
						JsonArray result = res.result();
						vertxRequest.buildVertxRespone().responeSuccess(result);
					} else {
						vertxRequest.buildVertxRespone().responseFail(res.cause().getMessage());
					}
				});
			} catch (Exception e) {
				vertxRequest.buildVertxRespone().responseFail(e.getMessage());
			}
		};
	}

	@Autowired
	private RedisService redisService;

	@RequestBody
	@RequestMapping(value = "/setRedisKey")
	public ControllerHandler setRedisKey() {

		return vertxRequest -> {
			Optional<String> key = vertxRequest.getParam("key");
			Optional<String> value = vertxRequest.getParam("value");
			logger.info("Key为 {} ，Value为 {}", key.get(), value.get());
			redisService.setRedisKey(key.get(), value.get(), result -> {
				if (result.succeeded()) {
					vertxRequest.buildVertxRespone().responeSuccess(result.result());
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});

			// vertxRequest.buildVertxRespone().responeState(true);
		};
	}

	@RequestBody
	@RequestMapping(value = "/setRedisKeyExpire")
	public ControllerHandler setRedisKeyExpire() {

		return vertxRequest -> {
			Optional<String> key = vertxRequest.getParam("key");
			Optional<String> expire = vertxRequest.getParam("expire");
			Optional<String> value = vertxRequest.getParam("value");
			logger.info("Key为 {} ，Value为 {}，过期时间为 {}", key.get(), value.get(), expire.get());
			redisService.setRedisKeyExpire(key.get(), expire.get(), value.get(), TimeUnit.MINUTES,
					result -> {
						if (result.succeeded()) {
							vertxRequest.buildVertxRespone().responeSuccess(result.result());
						} else {
							vertxRequest.buildVertxRespone()
									.responseFail(result.cause().getMessage());
						}
					});
		};
	}

	@RequestBody
	@RequestMapping(value = "/getRedisValue")
	public ControllerHandler getRedisValue() {

		return vertxRequest -> {
			Optional<String> key = vertxRequest.getParam("key");
			logger.info("Key为 {}", key.get());
			redisService.getRedisValue(key.get(), result -> {
				if (result.succeeded()) {
					String value = result.result();
					vertxRequest.buildVertxRespone().responeSuccess(value);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}
}
