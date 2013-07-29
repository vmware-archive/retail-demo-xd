#!/usr/bin/python

import sys
import commands
import os
import config

sqlfire_setup_sql = """
CREATE TABLE REALTIME_ORDERS
(
   CUSTOMER_ID INT NOT NULL,
   ORDER_ID INT NOT NULL,
   ORDER_AMOUNT NUMERIC(10,2),
   STORE_ID INT
)  REPLICATE;
"""

sqlfire_query_sql = """
--Pull all of the records back
SELECT * FROM REALTIME_ORDERS ORDER BY ORDER_AMOUNT DESC, STORE_ID ASC;

--If spring xd is working, there should not be any orders under 5000
SELECT MIN(ORDER_AMOUNT) FROM REALTIME_ORDERS;
"""

sqlfire_teardown_sql = """
--Cleanup SQL
DROP TABLE REALTIME_ORDERS;
"""

def setup():
  sqlfcmd(sqlfire_setup_sql)
   
def teardown():
  sqlfcmd(sqlfire_teardown_sql)
   
def query():
  sqlfcmd(sqlfire_query_sql)
   
def shellcmd(cmd):
    (status, output) = commands.getstatusoutput(cmd)
    print output
    
def sqlfcmd(sql):
    f = open("out.sql", "w")
    f.write(sql)      
    f.close()
    shellcmd("%s/bin/sqlf run -client-bind-address=%s -client-port=%s -file=./out.sql" % (config.sqlf_home,config.sqlf_hostname,config.sqlf_client_port))

def main():
  args = sys.argv[1:]
  if not args:
    print "usage (type one option): setup | teardown | query";
    sys.exit(1)
	
  functionList = {'setup': setup, 'teardown': teardown, 'query': query}
                  
  functionList[args[0]]()
  
if __name__ == "__main__":
  main()
