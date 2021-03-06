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

	void queryRecipesCommentRootList(int pageNo, int pageSize, String recipesId,
			Handler<AsyncResult<List<JSONObject>>> resultHandler);

	void getRecipesCommentChildTotal(List<String> commentIdList,
			Handler<AsyncResult<List<JSONObject>>> resultHandler);

	void queryRecipesCommentChildList(int pageNo, int pageSize, String commentId,
			Handler<AsyncResult<List<JSONObject>>> resultHandler);

	void deleteRecipesComment(JsonArray jsonArray, Handler<AsyncResult<Integer>> resultHandler);

	void clickFabulous(JsonArray jsonArray, int type, Handler<AsyncResult<Integer>> resultHandler);

	void getFabulous(String commentId, Handler<AsyncResult<Integer>> resultHandler);
}
