package com.demo.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;

/**
 * @ClassName: SessionCheckHandler
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 16:03
 * @Version 1.0
 */
@Component
public class SessionCheckHandler implements Handler<RoutingContext> {

	private final Logger logger = LoggerFactory.getLogger(SessionCheckHandler.class);

	@Autowired
	private FreeMarkerTemplateEngine templateEngine;

	@Override
	public void handle(RoutingContext event) {

		String requestUrl = event.request().path();
		logger.info("进入 Session 拦截器, 请求路径：{}", requestUrl);
		if ("/user/login".equals(requestUrl)) {
			logger.info("登录请求，不做session验证");

			// 继续下一个路由
			event.next();
		} else {
			Session session = event.session();
			String userId = session.get("userId");
			if (StringUtils.isBlank(userId)) {
				JsonObject data = new JsonObject();
				templateEngine.render(data, "templates/login", res -> {
					if (res.succeeded()) {
						event.response().end(res.result());
					} else {
						event.fail(res.cause());
					}
				});

				logger.error("session验证失败，跳转登录");
			} else {
				logger.info("session验证通过");

				String loginName = session.get("loginName");
				MDC.put("userid", userId);
				MDC.put("loginName", loginName);

				// 继续下一个路由
				event.next();
			}
		}
	}
}
