package com.pivotal.example.xd.sqlfire;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

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
	JdbcTemplate _template;
	
	
	static Logger logger = Logger.getLogger(SqlfireOrderController.class);
	
	@RequestMapping(value = "/orders/{id}", method = RequestMethod.GET)
	public @ResponseBody Order orders(Long id) {
		return _template.queryForObject("SELECT * FROM REALTIME_ORDERS WHERE ORDER_ID = " + id, new OrderRowMapper());
	}
	
	@RequestMapping(value = "/orders", method = RequestMethod.GET)
	public @ResponseBody List<Order> allOrders() {	
		logger.warn("RUNNING SQL!");
		List<Order> orders = _template.query("SELECT * FROM REALTIME_ORDERS ORDER BY ORDER_AMOUNT DESC, STORE_ID", new OrderRowMapper());
		
		return orders;
	}
	
	public class OrderRowMapper implements RowMapper<Order> {

		@Override
		public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
			Order order = new Order();
			order.setOrderId(rs.getInt("ORDER_ID"));
			order.setOrderAmount(rs.getDouble("ORDER_AMOUNT"));
			order.setCustomerId(rs.getInt("CUSTOMER_ID"));
			order.setStoreId(rs.getInt("STORE_ID"));
			order.setNumItems(rs.getInt("NUM_ITEMS"));
			
			return order;
		}
		
	}
}
