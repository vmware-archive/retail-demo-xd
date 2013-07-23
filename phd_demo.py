import sys
import commands

hawq_setup_sql = """
--Create external table for the HDFS realtime order data stream
CREATE EXTERNAL TABLE realtime_orders_pxf (customer_id int, order_id integer, order_amount numeric(10,2), store_id integer) 
LOCATION ('pxf://pivhdsne:50070/xd/order_stream/order_stream-*.txt?Fragmenter=HdfsDataFragmenter&Accessor=TextFileAccessor&Resolver=TextResolver') 
FORMAT 'TEXT' (DELIMITER = '|'); 

CREATE TABLE realtime_orders_hawq as select * from realtime_orders_pxf;
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
"""

def setup_hdfs():
  shellcmd('hdfs dfs -rm -r /xd')
  shellcmd('hdfs dfs -mkdir /xd')
  shellcmd('hdfs dfs -mkdir /xd/order_stream')
  shellcmd('hdfs dfs -chown williamsj:gpadmin /xd/order_stream')
  shellcmd('hdfs dfs -ls -R /xd')
  print "hadoop setup"
   
def setup_hawq():
   psql(hawq_setup_sql)

def query_hawq():
   psql(hawq_queries_sql)
   
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
    print "usage (type one option): setup_hdfs | setup_hawq | query_hawq | teardown_hawq";
    sys.exit(1)
	
  functionList = {'setup_hdfs': setup_hdfs, 'setup_hawq': setup_hawq, 
                  'query_hawq': query_hawq, 'teardown_hawq':teardown_hawq}
                  
  functionList[args[0]]()
  
if __name__ == "__main__":
  main()
