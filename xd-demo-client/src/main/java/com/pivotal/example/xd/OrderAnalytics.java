package com.pivotal.example.xd;

public class OrderAnalytics {

	private String last;
	private String alpha;
	private String average;
	private String max;
	private String min;
	private String count;

	public OrderAnalytics(String redisValue) {
		super();
		if (redisValue != null) {
			String[] arr = redisValue.split(" ");
			this.last = arr[0];
			this.alpha = arr[1];
			this.average = arr[2];
			this.max = arr[3];
			this.min = arr[4];
			this.count = arr[5];
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

	public String getAverage() {
		return average;
	}

	public void setAverage(String average) {
		this.average = average;
	}

	@Override
	public String toString() {
		return "OrderAnalytics [last=" + last + ", alpha=" + alpha
				+ ", average=" + average + ", max=" + max + ", min=" + min
				+ ", count=" + count + "]";
	}

}
