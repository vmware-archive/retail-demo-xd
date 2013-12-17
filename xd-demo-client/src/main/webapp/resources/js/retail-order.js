function ApplicationModel() {
	var self = this;

	self.retailOrders = ko.observable(new OrderModel());
	self.orderAnalytics = ko.observable(new OrderAnalyticsModel());
	self.statesMap = ko.observable(new MapModel());

	self.start = function() {
		// Load initial state from server
		$.getJSON("/orders", function(data) {
			self.retailOrders().loadOrders(data);
		});
		$.getJSON("/xdanalytics/orders", function(data) {
			self.orderAnalytics().loadOrderAnalytics(data);
		});
		self.statesMap().loadMap(false);
	}

	self.refresh = function() {
		// Reload state from server
		$.getJSON("/orders", function(data) {
			self.retailOrders().reloadOrders(data);
		});
		$.getJSON("/xdanalytics/orders", function(data) {
			self.orderAnalytics().reloadOrderAnalytics(data);
		});
		self.statesMap().loadMap(true);
	}
}

//Order data table code starts here
function OrderModel() {
	var self = this;
	self.rows = ko.observableArray();
	self.runningTotal = ko.observable(null);
	var rowLookup = {};

	self.loadOrders = function(orders) {
		var total = 0;
		for ( var i = 0; i < orders.length; i++) {
			var row = new OrderRow(orders[i]);
			self.rows.push(row);
			total += row.orderAmount;
			rowLookup[row.orderId] = row;
		}
		self.runningTotal(accounting.formatMoney(total));
	};

	self.reloadOrders = function(orders) {
		self.rows.removeAll();
		var total = 0;
		for ( var i = 0; i < orders.length; i++) {
			var row = new OrderRow(orders[i]);
			self.rows.push(row);
			total += row.orderAmount;
			rowLookup[row.orderId] = row;
		}
		self.runningTotal(accounting.formatMoney(total));
	};
};

function OrderRow(data) {
	var self = this;
	self.storeId = data.storeId;
	self.customerId = data.customerId;
	self.orderAmount = data.orderAmount;
	
	self.formattedOrderAmount = ko.computed(function() {
		return accounting.formatMoney(self.orderAmount);
	});
	self.orderId = data.orderId;
	self.numItems = data.numItems;
};

//Spring XD analytic summary code starts here
function OrderAnalyticsModel() {
	var self = this;
	self.rows = ko.observableArray();
	var rowLookup = {};

	self.loadOrderAnalytics = function(data) {
		for ( var i = 0; i < data.length; i++) {
			var row = new OrderAnalyticsRow(data[i]);
			self.rows.push(row);
		}
	};

	self.reloadOrderAnalytics = function(data) {
		self.rows.removeAll();
		for ( var i = 0; i < data.length; i++) {
			var row = new OrderAnalyticsRow(data[i]);
			self.rows.push(row);
		}
	};
};

function OrderAnalyticsRow(data) {
	var self = this;
	self.lastAmt = accounting.formatMoney(data.last);
	self.minAmt = accounting.formatMoney(data.min);
	self.maxAmt = accounting.formatMoney(data.max);
	self.avgAmt = accounting.formatMoney(data.avgOrder);
	self.orderCnt = data.count;
	self.analyticsKey = data.analyticsKey;
};

// MAP CODE STARTS HERE
function MapModel() {

	var self = this;
	var mapVisible = true;

	self.toggleMap = function() {
		if (mapVisible == false) {
			d3.select("#main-content").transition().duration(500).style("opacity", 0);
			d3.select("svg").transition().duration(500).style("opacity", 1).attr("width", 800).attr(
					"height", 500);
			mapVisible = true;
		} else {
			d3.select("svg").transition().duration(500).style("opacity", 0).attr("width", 0).attr(
					"height", 0);
			d3.select("#main-content").transition().duration(500).style("opacity", 1);
			mapVisible = false;
		}
	};
	
	self.loadMap = function(refresh) {
		// Width and height
		var w = 800;
		var h = 500;

		// Define map projection
		var projection = d3.geo.albersUsa().translate([ w / 2, h / 2 ]).scale(
				[ 900 ]);

		// Define path generator
		var path = d3.geo.path().projection(projection);

		// Define quantize scale to sort data values into buckets of color
		var color = d3.scale.quantize().range(
				[ "green", "darkgreen", "red", "darkred" ]);
		// Colors taken from colorbrewer.js, included in the D3 download

		// Create SVG element
		var svg;
		if (refresh == true) {
			svg = d3.select("svg");
		}
		else
		{
			svg = d3.select("#usmap").append("svg").attr("width", w).attr(
					"height", h);
		}

		// d3.csv("us-ag-productivity-2004.csv", function(data) {
		d3.json("/ordersumbystate", function(data) {
			// Set input domain for color scale
			color.domain([ d3.min(data, function(d) {
				return d.orderAmount;
			}), d3.max(data, function(d) {
				return d.orderAmount;
			}) ]);

			// Load in GeoJSON data
			d3.json("us-states.json", function(json) {

				// Merge the ag. data and GeoJSON
				// Loop through once for each ag. data value
				for ( var i = 0; i < data.length; i++) {

					// Grab state name
					var dataState = data[i].stateId;

					// Grab data value, and convert from string to float
					var dataValue = parseFloat(data[i].orderAmount);

					// Find the corresponding state inside the GeoJSON
					for ( var j = 0; j < json.features.length; j++) {

						// var jsonState = json.features[j].properties.name;
						var jsonState = json.features[j].id;

						if (dataState == jsonState) {

							// Copy the data value into the JSON
							json.features[j].properties.value = dataValue;

							// Stop looking through the JSON
							break;

						}
					}
				}
				if (refresh == true) {
					// Bind data and create one path per GeoJSON feature
					svg.selectAll("path").data(json.features).transition().duration(500).attr(
							"d", path).style("fill", function(d) {
						// Get data value
						var value = d.properties.value;

						if (value) {
							// If value exists
							return color(value);
						} else {
							// If value is undefined
							return "grey";
						}
					});
				} else {
					// Bind data and create one path per GeoJSON feature
					svg.selectAll("path").data(json.features).enter().append(
							"path").attr("d", path).style("fill", function(d) {
						// Get data value
						var value = d.properties.value;

						if (value) {
							// If value exists
							return color(value);
						} else {
							// If value is undefined
							return "grey";
						}
					});
				}
			});
		});
	};

};
