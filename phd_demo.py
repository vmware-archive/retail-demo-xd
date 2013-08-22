#!/usr/bin/python

import sys
import commands

hawq_setup_sql = """
--Create external table for the HDFS realtime order data stream
CREATE EXTERNAL TABLE realtime_orders_pxf (customer_id int, order_id integer, order_amount numeric(10,2), store_id integer, num_items integer) 
LOCATION ('pxf://pivhdsne:50070/xd/order_stream/order_stream-*.txt?Fragmenter=HdfsDataFragmenter&Accessor=TextFileAccessor&Resolver=TextResolver') 
FORMAT 'TEXT' (DELIMITER = '|'); 

CREATE TABLE realtime_orders_hawq as select * from realtime_orders_pxf;
"""

hawq_training_setup_sql = """
--Create external table for the HDFS training order data stream
CREATE EXTERNAL TABLE orders_training_pxf (customer_id int, order_id integer, order_amount numeric(10,2), store_id integer, num_items integer, is_fraudulent integer) 
LOCATION ('pxf://pivhdsne:50070/xd/training_stream/training_stream-*.txt?Fragmenter=HdfsDataFragmenter&Accessor=TextFileAccessor&Resolver=TextResolver') 
FORMAT 'TEXT' (DELIMITER = '|'); 

DROP VIEW IF EXISTS regression_model CASCADE;
CREATE VIEW regression_model AS
SELECT
order_id,
CASE WHEN is_fraudulent=1 THEN TRUE ELSE FALSE END AS dep_var,
ARRAY[order_amount,store_id,num_items]::float8[] as features,
is_fraudulent
FROM orders_training_pxf;

create table model as select * from madlib.logregr('regression_model','dep_var','features');

Select * from model;
"""

hawq_queries_sql = """
--pull 10 orders from the pxf table
select * from realtime_orders_pxf order by order_amount asc, store_id asc limit 10;

--pull 10 orders from the hawq table
select * from realtime_orders_hawq order by order_amount asc, store_id asc limit 10;
"""

hawq_teardown_sql = """
--Cleanup SQL
DROP TABLE realtime_orders_hawq; 
DROP EXTERNAL TABLE realtime_orders_pxf;
DROP EXTERNAL TABLE orders_training_pxf CASCADE;
DROP TABLE model;
"""

def setup_hdfs():
  shellcmd('hdfs dfs -rm -r /xd')
  shellcmd('hdfs dfs -mkdir /xd')
  shellcmd('hdfs dfs -mkdir /xd/order_stream')
  shellcmd('hdfs dfs -chown williamsj:gpadmin /xd/order_stream')
  shellcmd('hdfs dfs -mkdir /xd/training_stream')
  shellcmd('hdfs dfs -chown williamsj:gpadmin /xd/training_stream')
  shellcmd('hdfs dfs -ls -R /xd')
  print "hadoop setup"
   
def setup_hawq():
   psql(hawq_setup_sql)

def query_hawq():
   psql(hawq_queries_sql)
   
def hdfs():
  shellcmd('hdfs dfs -ls -R /xd')
   
def train_analytic():
   psql(hawq_training_setup_sql)
   
def teardown_hawq():
   psql(hawq_teardown_sql)
    
def psql(sql):
    f = open("out.sql", "w")
    f.write(sql)      
    f.close()
    cmd = "psql -e -f ./out.sql" 
    (status, output) = commands.getstatusoutput(cmd)
    print output
    
def shellcmd(cmd):
    (status, output) = commands.getstatusoutput(cmd)
    print output

def main():
  args = sys.argv[1:]
  if not args:
    print "usage (type one option): setup_hdfs | hdfs | train_analytic | setup_hawq | query_hawq | teardown_hawq";
    sys.exit(1)
	
  functionList = {'setup_hdfs': setup_hdfs, 'hdfs': hdfs, 'setup_hawq': setup_hawq, 
                  'query_hawq': query_hawq, 'teardown_hawq':teardown_hawq, 
                  'train_analytic':train_analytic}
                  
  functionList[args[0]]()
  
if __name__ == "__main__":
  main()
