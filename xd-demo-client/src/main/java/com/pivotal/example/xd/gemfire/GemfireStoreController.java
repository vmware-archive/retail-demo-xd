package com.pivotal.example.xd.gemfire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.GemfireOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gemstone.gemfire.cache.query.SelectResults;
import com.pivotal.example.xd.Store;

@Controller
public class GemfireStoreController {
	
	@Autowired
	GemfireOperations _template;
	
	@RequestMapping(value = "/stores/{id}", method = RequestMethod.GET)
	public @ResponseBody Store stores(@PathVariable Long id) {
		SelectResults<String> results = _template.find("select distinct * from /Stores where storeId=" + id);
		
		//should always be only 1
		for(String s : results) {
			return new GFStore(s);
		}
		
		return null;
	}
	
	@RequestMapping(value = "/stores", method = RequestMethod.GET)
	public @ResponseBody List<Store> allStores() {		
		SelectResults<String> results = _template.find("select distinct * from /Stores order by storeId");
		
		//sorting isn't working... have to do it manually
		List<Store> stores = new ArrayList<Store>();
		for(String s : results) {
			stores.add(new GFStore(s));
		}
		Collections.sort(stores, new Comparator<Store>() {
			public int compare(Store obj1, Store obj2) {
				if(obj1.getStoreId() > obj2.getStoreId()) {
					return 1;
				} else if(obj1.getStoreId() < obj2.getStoreId()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		
		return stores;
	}

	public class GFStore extends Store {
		
		public GFStore(String json) {
			JSONObject object;
			try {
				object = new JSONObject(json);
				setStoreId(object.getInt("storeId"));
			} catch(JSONException ex) {
				throw new RuntimeException("Invalid JSON: " + json);
			}
			
			try {
				double amt = object.getDouble("amount");
				setAmount(amt);
			} catch(Exception ex) { 
				setAmount(0.00);
			}
		}
	}
}
