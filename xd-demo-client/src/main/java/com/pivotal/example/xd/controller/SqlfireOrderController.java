package com.pivotal.example.xd.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pivotal.example.xd.Order;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SqlfireOrderController {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	static Logger logger = Logger.getLogger(SqlfireOrderController.class);
	
	@RequestMapping(value = "/orders/{id}", method = RequestMethod.GET)
	public @ResponseBody Order orders(Long id) {
		return jdbcTemplate.queryForObject("SELECT * FROM realtime_orders WHERE order_id = " + id, new OrderRowMapper());
	}
	
	@RequestMapping(value = "/orders", method = RequestMethod.GET)
	public @ResponseBody List<Order> allOrders() {	
		logger.warn("RUNNING SQL!");
		List<Order> orders = jdbcTemplate.query("SELECT * FROM realtime_orders ORDER BY order_amount DESC, STORE_ID", new OrderRowMapper());
		
		return orders;
	}
	
	@RequestMapping(value = "/ordersumbystate", method = RequestMethod.GET)
	public @ResponseBody List<Order> orderSumByStore() {	
		logger.warn("RUNNING ORDER BY SUM SQL!");
		List<Order> orders = jdbcTemplate.query("select distinct(store_id), sum(order_amount) as order_amount " +
				"from app.realtime_orders group by store_id order by store_id asc;", new StoreRowMapper());
		
		return sumOrdersByState(orders);
	}
	
	/**
	 * Tricky method that traverses faudulent orders and sums by state code.
	 * Tricky because we derive store stateId by interrogating StoreId
	 * @param orders
	 * @return
	 */
	private List<Order> sumOrdersByState(List<Order> orders)
	{
		List<Order> stateOrders = new ArrayList<Order>();
		HashMap<String,Double> sumMap = new HashMap<String,Double>();
		
		for (Order o : orders)
		{
			double newOrderAmount;
			if (sumMap.get(o.getStateId()) != null)
			{
				newOrderAmount = sumMap.get(o.getStateId()).doubleValue() + o.getOrderAmount();
			}
			else
			{
				newOrderAmount = o.getOrderAmount();
			}
			sumMap.put(o.getStateId(), newOrderAmount);
		}
		Iterator it = sumMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        Order o = new Order();
	        o.setOrderAmount((Double)pairs.getValue());
	        o.setStateId((String)pairs.getKey());
	        it.remove(); // avoids a ConcurrentModificationException
	        stateOrders.add(o);
	    }
	    return stateOrders;
	}
	
	public class OrderRowMapper implements RowMapper<Order> {

		@Override
		public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
			Order order = new Order();
			order.setOrderId(rs.getInt("order_id"));
			order.setOrderAmount(rs.getDouble("order_amount"));
			order.setCustomerId(rs.getInt("customer_id"));
			order.setStoreId(rs.getString("store_id").replaceAll("\"", ""));
			order.setNumItems(rs.getInt("num_items"));
			
			return order;
		}
		
	}
	
	public class StoreRowMapper implements RowMapper<Order> {

		@Override
		public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
			Order order = new Order();
			order.setStoreId(rs.getString("store_id").replaceAll("\"", ""));
			order.setOrderAmount(rs.getDouble("order_amount"));
			order.setStateId(order.getStoreId().substring(0, 2));
			order.setCityId(order.getStoreId().substring(2, 4));
		
			return order;
		}
		
	}
}
