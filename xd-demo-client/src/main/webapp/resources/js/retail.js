
function ApplicationModel() {
  var self = this;

  self.retailStores = ko.observable(new StoreModel());

  self.start = function() {
	  // Load initial state from server
  	  $.getJSON("/xd/stores", 
  			  function(data) {
  			  	self.retailStores().loadStores(data);
  	  		  });
  }
  
  self.refresh = function() {
	  // Load initial state from server
  	  $.getJSON("/xd/stores", 
  			  function(data) {
  			  	self.retailStores().updateStores(data);
  	  		  });
  }
}

function StoreModel() {
  var self = this;

  self.rows = ko.observableArray();

  self.totalValue = ko.computed(function() {
    var result = 0;
    for ( var i = 0; i < self.rows().length; i++) {
      result += self.rows()[i].amount();
    }
    return "$" + result.toFixed(2);
  });

  var rowLookup = {};

  self.loadStores = function(stores) {
    for ( var i = 0; i < stores.length; i++) {
      var row = new StoreRow(stores[i]);
      self.rows.push(row);
      rowLookup[row.storeId] = row;
    }
  };
  
  self.updateStores = function(stores) {
    for ( var i = 0; i < stores.length; i++) {
    	var row = new StoreRow(stores[i]);
    	if (row.storeId in rowLookup) {
    		rowLookup[row.storeId].updateAmt(row.amount());
        }
    }
  }; 
};

function StoreRow(data) {
  var self = this;
  self.storeId = data.storeId;
  self.amount = ko.observable(data.amount);
  self.formattedAmt = ko.computed(function() { return "$" + self.amount().toFixed(2); });
  self.change = ko.observable(0);
  self.arrow = ko.observable();

  self.updateAmt = function(newAmt) {
    var delta = (newAmt - self.amount()).toFixed(2);
    self.arrow((delta < 0) ? '<i class="icon-arrow-down"></i>' : '<i class="icon-arrow-up"></i>');
    self.change((delta / self.amount() * 100).toFixed(2));
    self.amount(newAmt);
  };
};
