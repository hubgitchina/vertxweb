package com.demo.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import cn.hutool.core.util.IdUtil;
import io.vertx.core.json.JsonArray;

/**
 * @ClassName: JdbcCommonUtil
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-12-30 10:43
 * @Version 1.0
 */
public class JdbcCommonUtil {

	/**
	 * @Author wangpeng
	 * @Description 设置表通用字段默认值
	 * @Date 10:44
	 * @Param
	 * @return
	 */
	public static String setCommonInfo(JsonArray jsonArray, String userId, String id) {

		if (StringUtils.isBlank(id)) {
			id = IdUtil.simpleUUID();
		}

		String now = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");

		jsonArray.add(id);
		jsonArray.add(now);
		jsonArray.add(userId);
		jsonArray.add(now);
		jsonArray.add(userId);
		jsonArray.add(0);

		return id;
	}
}
