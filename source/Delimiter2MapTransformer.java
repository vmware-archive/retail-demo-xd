package com.pivotal.xd.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Delimiter2MapTransformer {
	
	private String _delimiter;
	
	public Delimiter2MapTransformer(String delimiter) {
		_delimiter = delimiter;
	}

	public Map<String,String> transform(String payload) {
		StringTokenizer st = new StringTokenizer(payload, _delimiter);
		Map<String, String> m = new HashMap<String, String>();
		int i = 0;
		while (st.hasMoreTokens()) {
			m.put(String.valueOf(i), (String) st.nextElement());
			i++;
		}
		return m;
	}
}
