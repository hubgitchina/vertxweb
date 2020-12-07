package com.demo.service;

import java.util.List;

import com.demo.model.response.PageResponeWrapper;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * @ClassName: OrderRecipesAsyncService
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:05
 * @Version 1.0
 */
public interface OrderRecipesAsyncService {

	/**
	 * @Author wangpeng
	 * @Description 直接使用jdbcClient的查询方法，框架会自动关闭连接
	 * @Date 11:24
	 * @Param
	 * @return
	 */
	void queryOrderRecipesPage(int pageNo, int pageSize,
			Handler<AsyncResult<PageResponeWrapper>> resultHandler);

	void saveOrderRecipes(List<JsonArray> orderList, Handler<AsyncResult<Integer>> resultHandler);
}
