package com.demo.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.demo.service.BaseAsyncService;
import com.demo.service.CommentFabulousRecordAsyncService;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;

/**
 * @ClassName: CommentFabulousRecordAsyncServiceImpl
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:11
 * @Version 1.0
 */
@Component
public class CommentFabulousRecordAsyncServiceImpl
		implements CommentFabulousRecordAsyncService, BaseAsyncService {

	private final Logger logger = LoggerFactory
			.getLogger(CommentFabulousRecordAsyncServiceImpl.class);

	@Autowired
	private JDBCClient jdbcClient;

	@Override
	public void saveCommentFabulousRecord(JsonArray jsonArray,
			Handler<AsyncResult<Integer>> resultHandler) {

		String commentSql = "insert into comment_fabulous_record (id,create_date,create_by,update_date,update_by,is_del,comment_id,fabulous_user_id,fabulous_type,fabulous_time,fabulous_num) values (?,?,?,?,?,?,?,?,?,?,?)";

		jdbcClient.updateWithParams(commentSql, jsonArray, res -> {
			if (res.succeeded()) {
				UpdateResult result = res.result();
				int count = result.getUpdated();

				int type = jsonArray.getInteger(8);
				if (type == 1) {
					logger.info("评论点赞记录插入成功【{}】条，操作类型为【点赞】", count);
				} else {
					logger.info("评论点赞记录插入成功【{}】条，操作类型为【取消赞】", count);
				}

				Future.succeededFuture(count).onComplete(resultHandler);
			} else {
				logger.error("评论点赞记录插入失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	@Override
	public void getLatelyOneFabulousRecordByUserId(String commentId, String userId,
			Handler<AsyncResult<JSONObject>> resultHandler) {

		/** MAX函数只会返回对应最大字段值，而非对应整条记录 */
		// String sql = "select max(create_date) as createDate, comment_id as commentId,
		// fabulous_type as type from comment_fabulous_record where comment_id = ? and
		// fabulous_user_id = ?";

		String sql = "select comment_id as commentId, fabulous_type as type from comment_fabulous_record where comment_id = ? and fabulous_user_id = ? order by create_date desc limit 1";

		// 构造参数
		JsonArray params = new JsonArray();
		params.add(commentId);
		params.add(userId);

		// 执行查询
		jdbcClient.queryWithParams(sql, params, res -> {
			if (res.succeeded()) {
				ResultSet resultSet = res.result();
				List<JsonObject> rows = resultSet.getRows();

				if (CollectionUtils.isNotEmpty(rows)) {
					JSONObject fastObject = rows.get(0).mapTo(JSONObject.class);

					Future.succeededFuture(fastObject).onComplete(resultHandler);
				} else {
					Future.succeededFuture(new JSONObject()).onComplete(resultHandler);
				}
			} else {
				logger.error("查询失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	@Override
	public Future<JSONObject> getLatelyOneFabulousRecordByUserId(String commentId, String userId) {

		Promise<JSONObject> promise = Promise.promise();

		/** MAX函数只会返回对应最大字段值，而非对应整条记录 */
		// String sql = "select max(create_date) as createDate, comment_id as commentId,
		// fabulous_type as type from comment_fabulous_record where comment_id = ? and
		// fabulous_user_id = ?";

		String sql = "select comment_id as commentId, fabulous_type as type from comment_fabulous_record where comment_id = ? and fabulous_user_id = ? order by create_date desc limit 1";

		// 构造参数
		JsonArray params = new JsonArray();
		params.add(commentId);
		params.add(userId);

		// 执行查询
		jdbcClient.queryWithParams(sql, params, res -> {
			if (res.succeeded()) {
				ResultSet resultSet = res.result();
				List<JsonObject> rows = resultSet.getRows();

				if (CollectionUtils.isNotEmpty(rows)) {
					JSONObject fastObject = rows.get(0).mapTo(JSONObject.class);
					promise.complete(fastObject);
				} else {
					promise.complete();
				}
			} else {
				logger.error("查询失败：{}", res.cause().getMessage());
				promise.fail(res.cause());
			}
		});

		return promise.future();
	}
}
