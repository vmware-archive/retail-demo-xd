package com.pivotal.example.xd.controller;

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
	public @ResponseBody OrderAnalytics orderAnalyticsFromXd() {
		
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		
		OrderAnalytics oa = new OrderAnalytics((String)redisTemplate.opsForValue().get("richgauges.order_gauge"));
		return oa;
	}
	
	@RequestMapping(value = "/xdanalytics/fraudorders", method = RequestMethod.GET)
	public @ResponseBody OrderAnalytics fraudOrderAnalyticsFromXd() {
		
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		
		OrderAnalytics oa = new OrderAnalytics((String)redisTemplate.opsForValue().get("richgauges.fraud_order_gauge"));
		return oa;
	}
	
}
