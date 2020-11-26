package com.demo.controller;

import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.demo.annotation.RequestBody;
import com.demo.annotation.RequestMapping;
import com.demo.base.ControllerHandler;
import com.demo.enums.RequestMethod;
import com.demo.model.LoginModel;
import com.demo.util.DateUtil;
import com.google.common.collect.Lists;

import auth.MyJDBCAuth;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.jdbc.JDBCAuthentication;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

/**
 * @ClassName: LoginController
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:03
 * @Version 1.0
 */
@Component
@RequestMapping("/")
public class LoginController {

	private final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@RequestBody
	@RequestMapping(value = "/userLogin", method = RequestMethod.POST)
	public ControllerHandler userLogin() {

		return vertxRequest -> {
			LoginModel loginModel = vertxRequest.getBodyJsonToBean(LoginModel.class);
			vertxRequest.buildVertxRespone().responeSuccess(loginModel);
		};
	}

	@Autowired
	private JDBCAuthentication authenticationProvider;

	@Autowired
	private MyJDBCAuth myJDBCAuth;

	@Autowired
	private FreeMarkerTemplateEngine templateEngine;

	@RequestMapping("/login")
	public Handler<RoutingContext> loginPage() {

		return routingContext -> {

			logger.info("进入登录页");

			JsonObject data = new JsonObject();
			templateEngine.render(data, "templates/login", res -> {
				if (res.succeeded()) {
					routingContext.response().end(res.result());
				} else {
					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestMapping(value = "/login2", method = RequestMethod.POST)
	public Handler<RoutingContext> loginMethod() {

		return routingContext -> {
			String userName = routingContext.request().getParam("username");
			String password = routingContext.request().getParam("password");

			UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(
					userName, password);

			myJDBCAuth.authenticate(usernamePasswordCredentials, res -> {
				if (res.succeeded()) {
					// 获取到授权接口
					logger.info("认证成功");

					User user = res.result();

					Session session = routingContext.session();
					if (session != null) {
						session.regenerateId(); // 更新session id
					}

					session.put("loginName", user.principal().getString("userName"));
					session.put("userId", user.principal().getString("userId"));

					routingContext.setUser(user);

					JSONObject data = new JSONObject();

					// routingContext.redirect("/freeMarker/list");

					templateEngine.render(data, "templates/index", res2 -> {
						if (res2.succeeded()) {
							routingContext.response().end(res2.result());
						} else {
							routingContext.fail(res2.cause());
						}
					});
				} else {
					// 认证失败
					logger.error("认证失败：{}", res.cause().getMessage());

					routingContext.fail(res.cause());
				}
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/login3", method = RequestMethod.POST)
	public ControllerHandler loginMethod3() {

		return vertxRequest -> {
			String userName = vertxRequest.getParam("username").get();
			String password = vertxRequest.getParam("password").get();

			UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(
					userName, password);

			myJDBCAuth.authenticate(usernamePasswordCredentials, res -> {
				if (res.succeeded()) {
					// 获取到授权接口
					logger.info("认证成功");

					User user = res.result();

					Session session = vertxRequest.getRoutingContext().session();
					if (session != null) {
						session.regenerateId(); // 更新session id
					}

					session.put("loginName", user.principal().getString("userName"));
					session.put("userId", user.principal().getString("userId"));

					vertxRequest.getRoutingContext().setUser(user);

					vertxRequest.buildVertxRespone().responeSuccess(user.principal());
				} else {
					// 认证失败
					logger.error("认证失败：{}", res.cause().getMessage());

					vertxRequest.buildVertxRespone().responseFail(res.cause().getMessage());
				}
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/loginJDBCAuthentication", method = RequestMethod.POST)
	public ControllerHandler loginJDBCAuthentication() {

		return vertxRequest -> {
			String userName = vertxRequest.getParam("username").get();
			String password = vertxRequest.getParam("password").get();

			/**
			 * 使用vertx生成密码，加密盐为【fb721a736266e434】，密码为【1】，生成密码【$sha1$fb721a736266e434$NWoZK3kTsExUV00Ywo1G5jlUKKs】
			 */
			String demoPassword = authenticationProvider.hash("sha1", "fb721a736266e434", "1");
			logger.info("生成示例密码：{}", demoPassword);

			// UsernamePasswordCredentials usernamePasswordCredentials = new
			// UsernamePasswordCredentials(
			// "test", "1");

			UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(
					userName, password);

			authenticationProvider.authenticate(usernamePasswordCredentials, userAsyncResult -> {
				if (userAsyncResult.succeeded()) {
					logger.info("认证成功");

					User user = userAsyncResult.result();

					Session session = vertxRequest.getRoutingContext().session();
					if (session != null) {
						session.regenerateId(); // 更新session id
					}

					session.put("loginName", user.principal().getString("username"));
					session.put("userId", "111");

					vertxRequest.getRoutingContext().setUser(user);

					vertxRequest.buildVertxRespone().responeSuccess(user);

				} else {
					logger.error("认证失败：{}", userAsyncResult.cause().getMessage());

					vertxRequest.buildVertxRespone()
							.responseFail(userAsyncResult.cause().getMessage());
				}
			});
		};
	}
}
