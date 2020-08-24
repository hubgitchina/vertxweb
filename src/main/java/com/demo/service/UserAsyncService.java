package com.demo.service;

import java.util.List;

import com.demo.model.User;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * @ClassName: UserAsyncService
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:05
 * @Version 1.0
 */
@ProxyGen
public interface UserAsyncService {

	void getAllUser(Handler<AsyncResult<List<JsonObject>>> resultHandler);

}
