package com.pivotal.example.xd;

public class OrderAnalytics {

	private String last;
	private String alpha;
	private String avgOrder;
	private String max;
	private String min;
	private String count;
	private String analyticsKey;

	public OrderAnalytics(String analyticsKey, String redisValue) {
		super();
		if (redisValue != null) {
			String[] arr = redisValue.split(" ");
			this.last = arr[0];
			this.alpha = arr[1];
			this.avgOrder = arr[2];
			this.max = arr[3];
			this.min = arr[4];
			this.count = arr[5];
			this.analyticsKey = analyticsKey;
		}
	}

	public String getAlpha() {
		return alpha;
	}

	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}	

	public String getAvgOrder() {
		return avgOrder;
	}

	public void setAvgOrder(String avgOrder) {
		this.avgOrder = avgOrder;
	}

	public String getAnalyticsKey() {
		return analyticsKey;
	}

	public void setAnalyticsKey(String analyticsKey) {
		this.analyticsKey = analyticsKey;
	}

	@Override
	public String toString() {
		return "OrderAnalytics [analyticsKey=" + analyticsKey + ", last="
				+ last + ", alpha=" + alpha + ", average=" + avgOrder + ", max="
				+ max + ", min=" + min + ", count=" + count + "]";
	}

}
