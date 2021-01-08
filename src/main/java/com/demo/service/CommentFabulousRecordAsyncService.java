package com.demo.service;

import com.alibaba.fastjson.JSONObject;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * @ClassName: CommentFabulousRecordAsyncService
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:05
 * @Version 1.0
 */
public interface CommentFabulousRecordAsyncService {

	void saveCommentFabulousRecord(JsonArray jsonArray,
			Handler<AsyncResult<Integer>> resultHandler);

	void getLatelyOneFabulousRecordByUserId(String commentId, String userId,
			Handler<AsyncResult<JSONObject>> resultHandler);

	Future<JSONObject> getLatelyOneFabulousRecordByUserId(String commentId, String userId);
}
