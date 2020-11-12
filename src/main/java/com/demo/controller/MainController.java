package com.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.demo.annotation.RequestBody;
import com.demo.annotation.RequestMapping;
import com.demo.base.ControllerHandler;
import com.demo.enums.RequestMethod;
import com.demo.model.response.PageResponeWrapper;
import com.google.common.collect.Lists;

/**
 * @ClassName: MainController
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:03
 * @Version 1.0
 */
@Component
@RequestMapping("/main")
public class MainController {

	private final Logger logger = LoggerFactory.getLogger(MainController.class);

	@RequestBody
	@RequestMapping(value = "/getList", method = RequestMethod.POST)
	public ControllerHandler getList() {

		return vertxRequest -> {

			JSONObject data1 = new JSONObject();
			data1.put("id", "111");
			data1.put("type", "早餐");
			data1.put("sunday", "");
			data1.put("monday", "豆浆");
			data1.put("tuesday", "油条");
			data1.put("wednesday", "牛奶");
			data1.put("thursday", "面窝");
			data1.put("friday", "白粥");
			data1.put("saturday", "");

			/** 设置是否点餐 */
			data1.put("isMonday", 1);
			data1.put("isTuesday", 0);
			data1.put("isWednesday", 1);
			data1.put("isThursday", 0);
			data1.put("isFriday", 1);

			JSONObject data2 = new JSONObject();
			data2.put("id", "222");
			data2.put("type", "午餐");
			data2.put("sunday", "");
			data2.put("monday", "青椒肉丝");
			data2.put("tuesday", "白切鸡");
			data2.put("wednesday", "卤水鸡腿");
			data2.put("thursday", "红烧鱼块");
			data2.put("friday", "清蒸石斑鱼");
			data2.put("saturday", "");

			/** 设置是否点餐 */
			data2.put("isMonday", 0);
			data2.put("isTuesday", 1);
			data2.put("isWednesday", 0);
			data2.put("isThursday", 1);
			data2.put("isFriday", 0);

			JSONObject data3 = new JSONObject();
			data3.put("id", "333");
			data3.put("type", "晚餐");
			data3.put("sunday", "");
			data3.put("monday", "清炒小白菜");
			data3.put("tuesday", "蒜蓉油麦菜");
			data3.put("wednesday", "酸辣土豆丝");
			data3.put("thursday", "白灼菜心");
			data3.put("friday", "手撕包菜");
			data3.put("saturday", "");

			/** 设置是否点餐 */
			data3.put("isMonday", 1);
			data3.put("isTuesday", 0);
			data3.put("isWednesday", 1);
			data3.put("isThursday", 0);
			data3.put("isFriday", 1);

			List<JSONObject> result = Lists.newArrayListWithCapacity(3);
			result.add(data1);
			result.add(data2);
			result.add(data3);

			PageResponeWrapper pageResponeWrapper = new PageResponeWrapper(result, 1, 10,
					result.size());

			// pageResponeWrapper.setCode(200);
			// vertxRequest.getRoutingContext().response().putHeader("Content-Type",
			// "text/json;charset=utf-8");
			// vertxRequest.getRoutingContext().response().end(JSON.toJSONString(pageResponeWrapper));

			vertxRequest.buildVertxRespone().responePageSuccess(pageResponeWrapper);
		};
	}
}
