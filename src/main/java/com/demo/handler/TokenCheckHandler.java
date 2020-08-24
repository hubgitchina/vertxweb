package com.demo.handler;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.demo.model.response.ResponeWrapper;
import com.demo.vertx.VertxRespone;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * @ClassName: TokenCheckHandler
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 16:03
 * @Version 1.0
 */
@Component
public class TokenCheckHandler implements Handler<RoutingContext> {

	@Override
	public void handle(RoutingContext event) {

		HttpServerRequest request = event.request();
		String accesstoken = request.getHeader("accesstoken");
		if (StringUtils.isEmpty(accesstoken)) {
			VertxRespone.build(event).respone(new ResponeWrapper(10002, null, "登录失效，请重新登录！"));
		} else {
			// 继续下一个路由
			event.next();
		}
	}
}
