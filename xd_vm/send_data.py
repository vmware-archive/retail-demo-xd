import sys
import re
import os
import shutil
import commands
import random

def main():
  for x in xrange(10001):
    cust_id = str(x)
    order_id = str(random.randrange(2000,10000))
    order_amount = str('{:20.2f}'.format(random.uniform(100,10000))).strip()
    store_id = str(random.randrange(1,5000))
    
    stream_url = "http://localhost:8000"
    if x % 101 == 0:
       cust_id = "BAD_DATA"
    if x % 500 == 0:
       cust_id = "16186961"
    
    order_str = cust_id + "," + order_id + "," + order_amount + "," + store_id
    cmd = 'curl -d "%s" %s' % (order_str, stream_url)
    print "Command to run:", cmd  
    (status, output) = commands.getstatusoutput(cmd)
  
if __name__ == "__main__":
  main()
