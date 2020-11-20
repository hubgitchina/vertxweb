package com.demo.controller;

import java.util.List;
import java.util.Map;

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
import com.google.common.collect.Maps;

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

			List<Map<String, Object>> breakfastList = Lists.newArrayListWithCapacity(3);
			List<Map<String, Object>> lunchList = Lists.newArrayListWithCapacity(3);
			List<Map<String, Object>> dinnerList = Lists.newArrayListWithCapacity(3);

			Map<String, Object> foodA = Maps.newHashMapWithExpectedSize(3);
			foodA.put("isChoose", 1);
			foodA.put("name", "套餐A");
			foodA.put("food", new String[] { "豆浆", "油条" });

			Map<String, Object> foodB = Maps.newHashMapWithExpectedSize(3);
			foodB.put("isChoose", 0);
			foodB.put("name", "套餐B");
			foodB.put("food", new String[] { "油条", "豆浆" });

			Map<String, Object> foodC = Maps.newHashMapWithExpectedSize(3);
			foodC.put("isChoose", 0);
			foodC.put("name", "套餐C");
			foodC.put("food", new String[] { "面窝", "白粥" });

			breakfastList.add(foodA);
			breakfastList.add(foodB);
			breakfastList.add(foodC);

			Map<String, Object> foodAA = Maps.newHashMapWithExpectedSize(3);
			foodAA.put("isChoose", 0);
			foodAA.put("name", "套餐A");
			foodAA.put("food", new String[] { "青椒肉丝", "红烧鱼块" });

			Map<String, Object> foodBB = Maps.newHashMapWithExpectedSize(3);
			foodBB.put("isChoose", 1);
			foodBB.put("name", "套餐B");
			foodBB.put("food", new String[] { "白切鸡", "卤水鸡腿" });

			lunchList.add(foodAA);
			lunchList.add(foodBB);

			Map<String, Object> foodAAA = Maps.newHashMapWithExpectedSize(3);
			foodAAA.put("isChoose", 1);
			foodAAA.put("name", "套餐A");
			foodAAA.put("food", new String[] { "清炒小白菜", "酸辣土豆丝" });

			Map<String, Object> foodBBB = Maps.newHashMapWithExpectedSize(3);
			foodBBB.put("isChoose", 0);
			foodBBB.put("name", "套餐B");
			foodBBB.put("food", new String[] { "白灼菜心", "手撕包菜" });

			dinnerList.add(foodAAA);
			dinnerList.add(foodBBB);

			JSONObject data1 = new JSONObject();
			data1.put("id", "111");
			data1.put("type", "早餐");
			data1.put("sunday", "");
			data1.put("monday", breakfastList);
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
			data2.put("monday", lunchList);
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
			data3.put("monday", dinnerList);
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

	@RequestBody
	@RequestMapping(value = "/getTimeline", method = RequestMethod.POST)
	public ControllerHandler getTimeline() {

		return vertxRequest -> {

			JSONObject data1 = new JSONObject();
			data1.put("name", "王五");
			data1.put("date", "8月18日");
			data1.put("operation", "订餐");

			JSONObject data2 = new JSONObject();
			data2.put("name", "李四");
			data2.put("date", "8月16日");
			data2.put("operation", "修改菜单");

			JSONObject data3 = new JSONObject();
			data3.put("name", "张三");
			data3.put("date", "8月15日");
			data3.put("operation", "发布菜单");

			List<JSONObject> result = Lists.newArrayListWithCapacity(3);
			result.add(data1);
			result.add(data2);
			result.add(data3);

			vertxRequest.buildVertxRespone().responeSuccess(result);
		};
	}

	@RequestBody
	@RequestMapping(value = "/getMsg", method = RequestMethod.POST)
	public ControllerHandler getMsg() {

		return vertxRequest -> {

			JSONObject msg1 = new JSONObject();
			msg1.put("userName", "张爱玲1");
			msg1.put("msg",
					"于千万人之中遇到你所要遇到的人，于千万年之中，时间的无涯的荒野中，没有早一步，也没有晚一步，刚巧赶上了，那也没有别的话好说，唯有轻轻的问一声：“噢，原来你也在这里？");
			msg1.put("date", "4月11日 09:10");

			JSONObject msg2 = new JSONObject();
			msg2.put("userName", "张爱玲2");
			msg2.put("msg",
					"于千万人之中遇到你所要遇到的人，于千万年之中，时间的无涯的荒野中，没有早一步，也没有晚一步，刚巧赶上了，那也没有别的话好说，唯有轻轻的问一声：“噢，原来你也在这里？");
			msg2.put("date", "5月11日 09:10");

			JSONObject msg3 = new JSONObject();
			msg3.put("userName", "张爱玲3");
			msg3.put("msg",
					"于千万人之中遇到你所要遇到的人，于千万年之中，时间的无涯的荒野中，没有早一步，也没有晚一步，刚巧赶上了，那也没有别的话好说，唯有轻轻的问一声：“噢，原来你也在这里？");
			msg3.put("date", "6月11日 09:10");

			List<JSONObject> msgList = Lists.newArrayListWithCapacity(3);
			msgList.add(msg1);
			msgList.add(msg2);
			msgList.add(msg3);

			vertxRequest.buildVertxRespone().responeSuccess(msgList);
		};
	}
}
