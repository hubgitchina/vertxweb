package com.demo.service.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.demo.service.BaseAsyncService;
import com.demo.service.CommentRecipesAsyncService;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;

/**
 * @ClassName: CommentRecipesAsyncServiceImpl
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 18:11
 * @Version 1.0
 */
@Component
public class CommentRecipesAsyncServiceImpl
		implements CommentRecipesAsyncService, BaseAsyncService {

	private final Logger logger = LoggerFactory.getLogger(CommentRecipesAsyncServiceImpl.class);

	@Autowired
	private JDBCClient jdbcClient;

	@Override
	public void saveRecipesComment(JsonArray jsonArray,
			Handler<AsyncResult<Integer>> resultHandler) {

		String commentSql = "insert into recipes_comment (id,create_date,create_by,update_date,update_by,is_del,recipes_publish_id,root_comment_id,reply_comment_id,reply_comment_user_id,reply_user_id,reply_content,reply_status,reply_time,fabulous_num) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		jdbcClient.updateWithParams(commentSql, jsonArray, res -> {
			if (res.succeeded()) {
				UpdateResult result = res.result();
				int count = result.getUpdated();

				logger.info("菜谱评论插入成功【{}】条", count);

				Future.succeededFuture(count).onComplete(resultHandler);
			} else {
				logger.error("菜谱评论插入失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	@Override
	public void getRecipesCommentTotal(String recipesId,
			Handler<AsyncResult<Integer>> resultHandler) {

		String countSql = "select count(*) from recipes_comment where is_del = 0 and reply_status = 0 and root_comment_id = '0' and recipes_publish_id = ?";

		// 构造参数
		JsonArray params = new JsonArray();
		params.add(recipesId);

		// 执行查询
		jdbcClient.queryWithParams(countSql, params, countRes -> {
			if (countRes.succeeded()) {
				// 把ResultSet转为List<JsonObject>形式
				List<JsonObject> countResult = countRes.result().getRows();

				int count = countResult.get(0).getInteger("count(*)");
				logger.info("记录总数为：{}", count);

				Future.succeededFuture(count).onComplete(resultHandler);
			} else {
				logger.error("查询失败：{}", countRes.cause().getMessage());
				resultHandler.handle(Future.failedFuture(countRes.cause()));
			}
		});
	}

	@Override
	public void queryRecipesCommentRootList(int pageNo, int pageSize, String recipesId,
			Handler<AsyncResult<List<JSONObject>>> resultHandler) {

		String sql = "select c.*, u.login_name from recipes_comment c left join user u on u.id = c.reply_user_id where c.is_del = 0 and c.reply_status = 0 and c.root_comment_id = '0' and c.recipes_publish_id = ? order by c.create_date asc limit ?,?";

		// 构造参数
		JsonArray params = new JsonArray();
		params.add(recipesId);
		params.add((pageNo - 1) * pageSize);
		params.add(pageSize);

		// 执行查询
		jdbcClient.queryWithParams(sql, params, res -> {
			if (res.succeeded()) {
				// 获取到查询的结果，Vert.x对ResultSet进行了封装
				ResultSet resultSet = res.result();
				// 把ResultSet转为List<JsonObject>形式
				List<JsonObject> rows = resultSet.getRows();
				List<JSONObject> list = Lists.newArrayListWithCapacity(rows.size());
				if (CollectionUtils.isNotEmpty(rows)) {
					List<String> commentIdList = Lists.newArrayListWithCapacity(rows.size());
					for (JsonObject jsonObject : rows) {
						JSONObject fastObject = jsonObject.mapTo(JSONObject.class);

						DateTime replyTime = new DateTime(fastObject.getString("reply_time"));
						fastObject.put("reply_time", replyTime.toString("yyyy-MM-dd HH:mm:ss"));

						list.add(fastObject);

						commentIdList.add(fastObject.getString("id"));
					}

					this.getRecipesCommentChildTotal(commentIdList, countRes -> {
						if (countRes.succeeded()) {
							List<JSONObject> countList = countRes.result();
							if (CollectionUtils.isNotEmpty(countList)) {
								List<String> rootCommentIdList = Lists
										.newArrayListWithCapacity(countList.size());
								for (JSONObject jsonObject : list) {
									String commentId = jsonObject.getString("id");

									for (JSONObject childJson : countList) {
										String rootCommentId = childJson
												.getString("root_comment_id");
										int count = childJson.getIntValue("count(*)");
										if (count > 0) {
											rootCommentIdList.add(rootCommentId);
										}
										if (commentId.equals(rootCommentId)) {
											jsonObject.put("childTotal", count);
											continue;
										}
									}
								}

								// this.queryRecipesCommentChildList(pageNo, pageSize,
								// rootCommentIdList, result -> {
								// if (result.succeeded()) {
								// List<JSONObject> childList = result.result();
								// List<JSONObject> childReplyList;
								// if (CollectionUtils.isNotEmpty(childList)) {
								// for (JSONObject jsonObject : list) {
								// String commentId = jsonObject.getString("id");
								//
								// childReplyList = Lists.newArrayList();
								// for (JSONObject childJson : childList) {
								// String rootCommentId = childJson.getString("root_comment_id");
								// if (commentId.equals(rootCommentId)) {
								// childReplyList.add(childJson);
								// }
								// }
								// jsonObject.put("childReply", childReplyList);
								// }
								// }
								// Future.succeededFuture(list).onComplete(resultHandler);
								// } else {
								// logger.error("查询评论回复失败：{}", result.cause().getMessage());
								// resultHandler.handle(Future.failedFuture(result.cause()));
								// }
								// });
							} else {
								for (JSONObject jsonObject : list) {
									jsonObject.put("childTotal", 0);
								}
							}
							Future.succeededFuture(list).onComplete(resultHandler);
						} else {
							logger.error("查询评论回复总数失败：{}", countRes.cause().getMessage());
							resultHandler.handle(Future.failedFuture(countRes.cause()));
						}
					});
				} else {
					Future.succeededFuture(list).onComplete(resultHandler);
				}
			} else {
				logger.error("查询评论失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	@Override
	public void getRecipesCommentChildTotal(List<String> commentIdList,
			Handler<AsyncResult<List<JSONObject>>> resultHandler) {

		String[] inSql = new String[commentIdList.size()];
		Arrays.fill(inSql, "?");

		String countSql = "select root_comment_id, count(*) from recipes_comment where is_del = 0 and reply_status = 0"
				+ " and root_comment_id in (" + Joiner.on(",").join(inSql)
				+ ") group by root_comment_id";

		// 构造参数
		JsonArray params = new JsonArray(commentIdList);

		// 执行查询
		jdbcClient.queryWithParams(countSql, params, countRes -> {
			if (countRes.succeeded()) {
				// 把ResultSet转为List<JsonObject>形式
				List<JsonObject> countResult = countRes.result().getRows();
				List<JSONObject> list = Lists.newArrayListWithCapacity(countResult.size());

				for (JsonObject jsonObject : countResult) {
					JSONObject fastObject = jsonObject.mapTo(JSONObject.class);

					list.add(fastObject);
				}

				Future.succeededFuture(list).onComplete(resultHandler);
			} else {
				logger.error("查询失败：{}", countRes.cause().getMessage());
				resultHandler.handle(Future.failedFuture(countRes.cause()));
			}
		});
	}

	@Override
	public void queryRecipesCommentChildList(int pageNo, int pageSize, String commentId,
			Handler<AsyncResult<List<JSONObject>>> resultHandler) {

		String sql = "select c.*, u.login_name as loginName, ru.login_name as replyLoginName from recipes_comment c left join user u on u.id = c.reply_user_id left join user ru on ru.id = c.reply_comment_user_id where c.is_del = 0 and c.reply_status = 0"
				+ " and c.root_comment_id = ? order by c.root_comment_id, c.create_date asc limit ?,?";

		// 构造参数
		JsonArray params = new JsonArray();
		params.add(commentId);
		params.add((pageNo - 1) * pageSize);
		params.add(pageSize);

		// 执行查询
		jdbcClient.queryWithParams(sql, params, res -> {
			if (res.succeeded()) {
				// 获取到查询的结果，Vert.x对ResultSet进行了封装
				ResultSet resultSet = res.result();
				// 把ResultSet转为List<JsonObject>形式
				List<JsonObject> rows = resultSet.getRows();
				List<JSONObject> list = Lists.newArrayListWithCapacity(rows.size());

				for (JsonObject jsonObject : rows) {
					JSONObject fastObject = jsonObject.mapTo(JSONObject.class);

					DateTime replyTime = new DateTime(fastObject.getString("reply_time"));
					fastObject.put("reply_time", replyTime.toString("yyyy-MM-dd HH:mm:ss"));

					list.add(fastObject);
				}

				Future.succeededFuture(list).onComplete(resultHandler);
			} else {
				logger.error("查询失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	@Override
	public void deleteRecipesComment(JsonArray jsonArray,
			Handler<AsyncResult<Integer>> resultHandler) {

		String deleteSql = "update recipes_comment set is_del = 1, update_date = ?, update_by = ? where id = ?";

		jdbcClient.updateWithParams(deleteSql, jsonArray, res -> {
			if (res.succeeded()) {
				UpdateResult result = res.result();
				int count = result.getUpdated();

				logger.info("菜谱评论删除成功【{}】条，ID为【{}】", count, jsonArray.getString(2));

				Future.succeededFuture(count).onComplete(resultHandler);
			} else {
				logger.error("菜谱评论删除失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	@Override
	public void clickFabulous(JsonArray jsonArray, int type,
			Handler<AsyncResult<Integer>> resultHandler) {

		String updateSql;
		if (type == 1) {
			updateSql = "update recipes_comment set fabulous_num = fabulous_num + 1, update_date = ?, update_by = ? where id = ?";
		} else {
			updateSql = "update recipes_comment set fabulous_num = fabulous_num - 1, update_date = ?, update_by = ? where id = ?";
		}

		jdbcClient.updateWithParams(updateSql, jsonArray, res -> {
			if (res.succeeded()) {
				UpdateResult result = res.result();
				int count = result.getUpdated();

				logger.info("菜谱评论点赞数更新成功【{}】条，ID为【{}】", count, jsonArray.getString(2));

				Future.succeededFuture(count).onComplete(resultHandler);
			} else {
				logger.error("菜谱评论点赞数更新失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}

	@Override
	public void getFabulous(String commentId, Handler<AsyncResult<Integer>> resultHandler) {

		String fabulousSql = "select fabulous_num from recipes_comment where id = ?";

		// 构造参数
		JsonArray params = new JsonArray();
		params.add(commentId);

		// 执行查询
		jdbcClient.queryWithParams(fabulousSql, params, res -> {
			if (res.succeeded()) {
				ResultSet resultSet = res.result();
				// 把ResultSet转为List<JsonObject>形式
				List<JsonObject> rows = resultSet.getRows();
				int count = 0;
				if (CollectionUtils.isNotEmpty(rows)) {
					count = rows.get(0).getInteger("fabulous_num");
				}

				logger.info("ID为【{}】的评论，点赞数为【{}】", commentId, count);

				Future.succeededFuture(count).onComplete(resultHandler);
			} else {
				logger.error("查询失败：{}", res.cause().getMessage());
				resultHandler.handle(Future.failedFuture(res.cause()));
			}
		});
	}
}
