package com.pivotal.example.xd;

import java.text.DecimalFormat;

public class Order {

	private int orderId;
	private int customerId;
	private double orderAmount;
	private int storeId;
	private int numItems;

	private final DecimalFormat FD = new DecimalFormat("#.00");

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public double getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(double orderAmount) {
		this.orderAmount = Double.parseDouble(FD.format(orderAmount));
	}

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public int getNumItems() {
		return numItems;
	}

	public void setNumItems(int numItems) {
		this.numItems = numItems;
	}

}