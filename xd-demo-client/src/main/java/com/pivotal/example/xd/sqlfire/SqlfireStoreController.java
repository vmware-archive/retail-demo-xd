package com.pivotal.example.xd.sqlfire;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pivotal.example.xd.Store;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SqlfireStoreController {
	
	@Autowired
	JdbcTemplate _template;
	
	@RequestMapping(value = "/stores/{id}", method = RequestMethod.GET)
	public @ResponseBody Store stores(Long id) {
		return _template.queryForObject("select * from stores where id = " + id, new StoreRowMapper());
	}
	
	@RequestMapping(value = "/stores", method = RequestMethod.GET)
	public @ResponseBody List<Store> allStores() {		
		List<Store> stores = _template.query("select * from stores order by id",new StoreRowMapper());
		return stores;
	}
	
	public class StoreRowMapper implements RowMapper<Store> {

		@Override
		public Store mapRow(ResultSet rs, int rowNum) throws SQLException {
			Store store = new Store();
			store.setStoreId(rs.getInt("id"));
			store.setAmount(rs.getDouble("amount"));
			return store;
		}
		
	}
}
