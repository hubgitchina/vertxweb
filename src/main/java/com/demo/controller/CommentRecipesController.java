package com.demo.controller;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.demo.annotation.RequestBody;
import com.demo.annotation.RequestMapping;
import com.demo.base.ControllerHandler;
import com.demo.enums.RequestMethod;
import com.demo.service.CommentFabulousRecordAsyncService;
import com.demo.service.CommentRecipesAsyncService;
import com.demo.util.JdbcCommonUtil;
import com.google.common.collect.Lists;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;

/**
 * @ClassName: CommentRecipesController
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:03
 * @Version 1.0
 */
@Component
@RequestMapping("/comment")
public class CommentRecipesController {

	private final Logger logger = LoggerFactory.getLogger(CommentRecipesController.class);

	@Autowired
	private CommentRecipesAsyncService commentRecipesAsyncService;

	@Autowired
	private CommentFabulousRecordAsyncService fabulousRecordAsyncService;

	@RequestBody
	@RequestMapping(value = "/saveRecipesComment", method = RequestMethod.POST)
	public ControllerHandler saveRecipesComment() {

		return vertxRequest -> {
			JSONObject params = vertxRequest.getBodyJsonToBean(JSONObject.class);
			logger.info("参数 {}", params);

			String userId = vertxRequest.getRoutingContext().user().principal().getString("userId");
			String loginName = vertxRequest.getRoutingContext().user().principal()
					.getString("userName");

			String recipesId = params.getString("recipesId");
			String replyContent = params.getString("replyContent");
			String rootCommentId = params.getString("rootCommentId");
			;

			String replyCommentId = params.getString("replyCommentId");
			String replyCommentUserId = params.getString("replyCommentUserId");

			int type = params.getIntValue("type");

			JsonArray commentJson = new JsonArray();
			String commentId = JdbcCommonUtil.setCommonInfo(commentJson, userId, null);
			commentJson.add(recipesId);
			commentJson.add(rootCommentId);

			commentJson.add(replyCommentId);
			commentJson.add(replyCommentUserId);

			commentJson.add(userId);
			commentJson.add(replyContent);

			commentJson.add(0);

			String nowTimeStr = commentJson.getString(1);
			commentJson.add(nowTimeStr);
			commentJson.add(0);

			commentRecipesAsyncService.saveRecipesComment(commentJson, result -> {
				if (result.succeeded()) {
					int count = result.result();
					if (count > 0) {
						if (type == 1) {
							commentRecipesAsyncService.getRecipesCommentTotal(recipesId,
									totalRes -> {
										if (totalRes.succeeded()) {
											int total = totalRes.result();
											vertxRequest.buildVertxRespone().responeSuccess(total);
										} else {
											vertxRequest.buildVertxRespone()
													.responseFail(totalRes.cause().getMessage());
										}
									});
						} else {
							List<String> commentIdList = Lists.newArrayListWithCapacity(1);
							commentIdList.add(rootCommentId);
							commentRecipesAsyncService.getRecipesCommentChildTotal(commentIdList,
									totalRes -> {
										if (totalRes.succeeded()) {
											List<JSONObject> totalList = totalRes.result();
											int total = totalList.get(0).getIntValue("count(*)");

											// Map<String, Object> map =
											// Maps.newHashMapWithExpectedSize(5);
											// map.put("commentId", commentId);
											// map.put("commentUserId", userId);
											// map.put("commentUserName", loginName);
											// map.put("commentTime", nowTimeStr);
											// map.put("fabulousNum", 0);
											// vertxRequest.buildVertxRespone().responeSuccess(map);

											vertxRequest.buildVertxRespone().responeSuccess(total);
										} else {
											vertxRequest.buildVertxRespone()
													.responseFail(totalRes.cause().getMessage());
										}
									});
						}
					} else {
						vertxRequest.buildVertxRespone().responseFail("保存失败");
					}
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/getRecipesCommentTotal", method = RequestMethod.POST)
	public ControllerHandler getRecipesCommentTotal() {

		return vertxRequest -> {
			JSONObject params = vertxRequest.getBodyJsonToBean(JSONObject.class);
			logger.info("参数 {}", params);

			String recipesId = params.getString("recipesId");

			commentRecipesAsyncService.getRecipesCommentTotal(recipesId, result -> {
				if (result.succeeded()) {
					int total = result.result();

					vertxRequest.buildVertxRespone().responeSuccess(total);
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/queryRecipesCommentRootList", method = RequestMethod.POST)
	public ControllerHandler queryRecipesCommentRootList() {

		return vertxRequest -> {
			JSONObject params = vertxRequest.getBodyJsonToBean(JSONObject.class);
			logger.info("参数 {}", params);

			int page = params.getIntValue("page");
			int limit = params.getIntValue("limit");
			String recipesId = params.getString("recipesId");

			logger.info("pageNo为 {} ，pageSize为 {} ，recipesId为 {}", page, limit, recipesId);

			commentRecipesAsyncService.queryRecipesCommentRootList(page, limit, recipesId,
					result -> {
						if (result.succeeded()) {
							List<JSONObject> list = result.result();

							if (CollectionUtils.isNotEmpty(list)) {
								String userId = vertxRequest.getRoutingContext().user().principal()
										.getString("userId");
								for (JSONObject jsonObject : list) {
									if (userId.equals(jsonObject.getString("reply_user_id"))) {
										jsonObject.put("canDel", 1);
									} else {
										jsonObject.put("canDel", 0);
									}
								}
							}

							vertxRequest.buildVertxRespone().responeSuccess(list);
						} else {
							vertxRequest.buildVertxRespone()
									.responseFail(result.cause().getMessage());
						}
					});
		};
	}

	@RequestBody
	@RequestMapping(value = "/queryRecipesCommentChildList", method = RequestMethod.POST)
	public ControllerHandler queryRecipesCommentChildList() {

		return vertxRequest -> {
			JSONObject params = vertxRequest.getBodyJsonToBean(JSONObject.class);
			logger.info("参数 {}", params);

			int page = params.getIntValue("page");
			int limit = params.getIntValue("limit");
			String rootCommentId = params.getString("rootCommentId");

			logger.info("pageNo为 {} ，pageSize为 {} ，rootCommentId为 {}", page, limit, rootCommentId);

			commentRecipesAsyncService.queryRecipesCommentChildList(page, limit, rootCommentId,
					result -> {
						if (result.succeeded()) {
							List<JSONObject> list = result.result();

							if (CollectionUtils.isNotEmpty(list)) {
								String userId = vertxRequest.getRoutingContext().user().principal()
										.getString("userId");

								List<Future> futureList = Lists
										.newArrayListWithCapacity(list.size());

								for (JSONObject jsonObject : list) {
									if (userId.equals(jsonObject.getString("reply_user_id"))) {
										jsonObject.put("canDel", 1);
									} else {
										jsonObject.put("canDel", 0);
									}

									String commnetId = jsonObject.getString("id");

									Future<JSONObject> future = fabulousRecordAsyncService
											.getLatelyOneFabulousRecordByUserId(commnetId, userId);
									futureList.add(future);
								}

								CompositeFuture.all(futureList).onComplete(allRes -> {
									if (allRes.succeeded()) {
										List<JSONObject> fabulousList = allRes.result().list();
										if (CollectionUtils.isNotEmpty(fabulousList)) {
											for (JSONObject jsonObject : list) {
												String commnetId = jsonObject.getString("id");
												for (JSONObject fabulousJson : fabulousList) {
													if (commnetId.equals(
															fabulousJson.getString("commentId"))
															&& 1 == fabulousJson
																	.getIntValue("type")) {
														jsonObject.put("isFabulous", 1);
													}
												}
											}
										}
										vertxRequest.buildVertxRespone().responeSuccess(list);
									} else {
										vertxRequest.buildVertxRespone().responeSuccess(list);
									}
								});
							}
							// vertxRequest.buildVertxRespone().responeSuccess(list);
						} else {
							vertxRequest.buildVertxRespone()
									.responseFail(result.cause().getMessage());
						}
					});
		};
	}

	@RequestBody
	@RequestMapping(value = "/deleteRecipesComment", method = RequestMethod.POST)
	public ControllerHandler deleteRecipesComment() {

		return vertxRequest -> {
			JSONObject params = vertxRequest.getBodyJsonToBean(JSONObject.class);
			logger.info("参数 {}", params);

			String userId = vertxRequest.getRoutingContext().user().principal().getString("userId");

			String recipesId = params.getString("recipesId");
			String commentId = params.getString("commentId");
			String rootCommentId = params.getString("rootCommentId");

			int type = params.getIntValue("type");

			JsonArray commentJson = new JsonArray();

			JdbcCommonUtil.setUpdateCommonInfo(commentJson, userId);

			commentJson.add(commentId);

			commentRecipesAsyncService.deleteRecipesComment(commentJson, result -> {
				if (result.succeeded()) {
					int count = result.result();
					if (count > 0) {
						if (type == 1) {
							commentRecipesAsyncService.getRecipesCommentTotal(recipesId,
									totalRes -> {
										if (totalRes.succeeded()) {
											int total = totalRes.result();
											vertxRequest.buildVertxRespone().responeSuccess(total);
										} else {
											vertxRequest.buildVertxRespone()
													.responseFail(totalRes.cause().getMessage());
										}
									});
						} else {
							List<String> commentIdList = Lists.newArrayListWithCapacity(1);
							commentIdList.add(rootCommentId);
							commentRecipesAsyncService.getRecipesCommentChildTotal(commentIdList,
									totalRes -> {
										if (totalRes.succeeded()) {
											List<JSONObject> totalList = totalRes.result();
											int total = totalList.get(0).getIntValue("count(*)");

											vertxRequest.buildVertxRespone().responeSuccess(total);
										} else {
											vertxRequest.buildVertxRespone()
													.responseFail(totalRes.cause().getMessage());
										}
									});
						}
					} else {
						vertxRequest.buildVertxRespone().responseFail("删除失败");
					}
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/clickFabulous", method = RequestMethod.POST)
	public ControllerHandler clickFabulous() {

		return vertxRequest -> {
			JSONObject params = vertxRequest.getBodyJsonToBean(JSONObject.class);
			logger.info("参数 {}", params);

			String userId = vertxRequest.getRoutingContext().user().principal().getString("userId");

			String commentId = params.getString("commentId");
			int type = params.getIntValue("type");

			JsonArray commentJson = new JsonArray();

			JdbcCommonUtil.setUpdateCommonInfo(commentJson, userId);

			commentJson.add(commentId);

			commentRecipesAsyncService.clickFabulous(commentJson, type, result -> {
				if (result.succeeded()) {
					int count = result.result();
					if (count > 0) {
						commentRecipesAsyncService.getFabulous(commentId, numRes -> {
							if (numRes.succeeded()) {
								int total = numRes.result();

								JsonArray fabulousRecordJson = new JsonArray();
								JdbcCommonUtil.setCommonInfo(fabulousRecordJson, userId, null);
								fabulousRecordJson.add(commentId);
								fabulousRecordJson.add(userId);
								fabulousRecordJson.add(type);

								String nowTimeStr = fabulousRecordJson.getString(1);
								fabulousRecordJson.add(nowTimeStr);

								fabulousRecordJson.add(total);

								fabulousRecordAsyncService.saveCommentFabulousRecord(
										fabulousRecordJson, recordRes -> {
											if (recordRes.succeeded()) {
												vertxRequest.buildVertxRespone()
														.responeSuccess(total);
											} else {
												vertxRequest.buildVertxRespone().responseFail(
														recordRes.cause().getMessage());
											}
										});
							} else {
								vertxRequest.buildVertxRespone()
										.responseFail(numRes.cause().getMessage());
							}
						});
					} else {
						String errorMsg;
						if (type == 1) {
							errorMsg = "点赞失败";
						} else {
							errorMsg = "取消赞失败";
						}
						vertxRequest.buildVertxRespone().responseFail(errorMsg);
					}
				} else {
					vertxRequest.buildVertxRespone().responseFail(result.cause().getMessage());
				}
			});
		};
	}
}
