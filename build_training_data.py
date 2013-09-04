#!/usr/bin/python

import re
import shutil
import commands
import random
import json
import urllib2

def main():
  for x in xrange(100001):
    cust_id = str(x)
    order_id = str(random.randrange(2000,10000))
    order_amount = str('{:20.2f}'.format(random.uniform(100,10000))).strip()
    state_id = str(random.randrange(1,52)).zfill(2) 
    city_id = str(random.randrange(1,99)).zfill(2)
    store_id = state_id + city_id

    if x % 500 == 0:
       cust_id = "16186961"
       
    num_items = str(random.randrange(10,1000))
    
    is_fraudulent = 0;
    # Flag items above a specific $$ per item and specfic stores
    if ((float(order_amount)/int(num_items)) >= 500) and (int(store_id) % 10 == 0):
        is_fraudulent = 1;
        print "************************"
        print "Created fraudulent order - " + str(x)
        print "************************"   
       
    data = json.dumps({"customerid": cust_id, "orderid": order_id, "orderamount": order_amount, "storeid": store_id, "items": num_items, "fraudulent": is_fraudulent})
    
    req = urllib2.Request("http://localhost:8001", data, {'Content-Type': 'application/json'})
    #print "POST URL:" + req.get_full_url()
    #print "POST DATA:" + req.data
    f = urllib2.urlopen(req)
    response = f.read()
    f.close()
  
if __name__ == "__main__":
  main()
