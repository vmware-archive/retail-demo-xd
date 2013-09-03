package com.pivotal.example.xd.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pivotal.example.xd.OrderAnalytics;

@Controller
public class RedisAnalyticsController {
	@Autowired
	RedisTemplate redisTemplate;
	
	static Logger logger = Logger.getLogger(SqlfireOrderController.class);
	
	@RequestMapping(value = "/xdanalytics/orders", method = RequestMethod.GET)
	public @ResponseBody List<OrderAnalytics> orderAnalyticsFromXd() {
		
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		
		OrderAnalytics oaAll = new OrderAnalytics("richgauges.order_gauge", (String)redisTemplate.opsForValue().get("richgauges.order_gauge"));
		OrderAnalytics oaFraud = new OrderAnalytics("richgauges.fraud_order_gauge", (String)redisTemplate.opsForValue().get("richgauges.fraud_order_gauge"));
		ArrayList<OrderAnalytics> oaList = new ArrayList<OrderAnalytics>();
		oaList.add(oaAll);
		oaList.add(oaFraud);
		return oaList;
	}
	
}
