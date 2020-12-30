package com.demo.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * @ClassName: CommentRecipesAsyncService
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:05
 * @Version 1.0
 */
public interface CommentRecipesAsyncService {

	void saveRecipesComment(JsonArray jsonArray, Handler<AsyncResult<Integer>> resultHandler);

	void getRecipesCommentTotal(String recipesId, Handler<AsyncResult<Integer>> resultHandler);

	void queryRecipesCommentList(int pageNo, int pageSize, String recipesId,
			Handler<AsyncResult<List<JSONObject>>> resultHandler);
}
