
function ApplicationModel() {
  var self = this;

  self.retailOrders = ko.observable(new OrderModel());

  self.start = function() {
	  // Load initial state from server
  	  $.getJSON("/xd-demo-client/orders", 
  			  function(data) {
  			  	self.retailOrders().loadOrders(data);
  	  		  });
  }
  
  self.refresh = function() {
	  // Reload state from server
  	  $.getJSON("/xd-demo-client/orders", 
  			  function(data) {
  			  	self.retailOrders().reloadOrders(data);
  	  		  });
  }
}

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
    self.runningTotal("$" + total.toFixed(2));
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
	    self.runningTotal("$" + total.toFixed(2));
  };
  
};

function OrderRow(data) {
  var self = this;
  self.storeId = data.storeId;
  self.customerId = data.customerId;
  self.orderAmount = data.orderAmount;
  self.formattedOrderAmount = ko.computed(function() { return "$" + self.orderAmount.toFixed(2); });
  self.orderId = data.orderId;
  self.numItems = data.numItems;
  
  self.change = ko.observable(0);
  self.arrow = ko.observable();
};
