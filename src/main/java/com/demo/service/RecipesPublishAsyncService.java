package com.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.demo.model.response.PageResponeWrapper;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @ClassName: RecipesPublishAsyncService
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:05
 * @Version 1.0
 */
public interface RecipesPublishAsyncService {

	/**
	 * @Author wangpeng
	 * @Description 直接使用jdbcClient的查询方法，框架会自动关闭连接
	 * @Date 11:24
	 * @Param
	 * @return
	 */
	void queryRecipesPublishPage(int pageNo, int pageSize,
			Handler<AsyncResult<PageResponeWrapper>> resultHandler);

	void publishRecipes(String id, Handler<AsyncResult<Integer>> resultHandler);

	void getRecipesPublishNew(Handler<AsyncResult<JSONObject>> resultHandler);
}
