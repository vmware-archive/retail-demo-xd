package com.pivotal.example.xd;

import java.text.DecimalFormat;

public class Store {
		
		private final DecimalFormat FD = new DecimalFormat("#.00");
		
		private int _id;
		private double _amt;
		
		public int getStoreId() {
			return _id;
		}
		
		public void setStoreId(int id) {
			_id = id;
		}
		
		public Number getAmount() {
			return _amt;
		}
		
		public void setAmount(double amt) {
			_amt = Double.parseDouble(FD.format(amt));
		}
	}