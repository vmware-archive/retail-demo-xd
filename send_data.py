#!/usr/bin/python

import re
import shutil
import commands
import random
import json
import urllib2

def main():
  for x in xrange(10001):
    cust_id = str(x)
    order_id = str(random.randrange(2000,10000))
    order_amount = str('{:20.2f}'.format(random.uniform(100,10000))).strip()
    store_id = str(random.randrange(1,1000))
    
    if x % 101 == 0:
       cust_id = "BAD_DATA"
    if x % 500 == 0:
       cust_id = "16186961"
       
    num_items = str(random.randrange(1,100))
       
    data = json.dumps({"customerid": cust_id, "orderid": order_id, "orderamount": order_amount, "storeid": store_id, "items": num_items})
    
    req = urllib2.Request("http://localhost:8000", data, {'Content-Type': 'application/json'})
    print "POST URL:" + req.get_full_url()
    print "POST DATA:" + req.data
    f = urllib2.urlopen(req)
    response = f.read()
    f.close()
  
if __name__ == "__main__":
  main()
