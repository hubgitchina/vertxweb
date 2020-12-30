package com.demo.service.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.demo.service.BaseAsyncService;
import com.demo.service.CommentRecipesAsyncService;
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

		String countSql = "select count(*) from recipes_comment where is_del = 0 and reply_status = 0";

		// 执行查询
		jdbcClient.queryWithParams(countSql, new JsonArray(), countRes -> {
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
	public void queryRecipesCommentList(int pageNo, int pageSize, String recipesId,
			Handler<AsyncResult<List<JSONObject>>> resultHandler) {

		String sql = "select c.*, u.login_name from recipes_comment c left join user u on u.id = c.reply_user_id where c.is_del = 0 and c.reply_status = 0 and c.root_comment_id = '0' order by c.create_date asc limit ?,?";

		// 构造参数
		JsonArray params = new JsonArray();
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

}
