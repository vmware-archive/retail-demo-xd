import sys
import commands

def setup_hdfs():
  shellcmd('hdfs dfs -rm -r /xd')
  shellcmd('hdfs dfs -mkdir /xd')
  shellcmd('hdfs dfs -mkdir /xd/order_stream')
  shellcmd('hdfs dfs -chown williamsj:gpadmin /xd/order_stream')
  shellcmd('hdfs dfs -ls -R /xd')
  print "hadoop setup"
   
def setup_hawq():
   shellcmd('psql -f hawq_setup.sql')
   print "PXF and HAWQ tables created"
   
def query_hawq():
   shellcmd('psql -f hawq_queries.sql')
   print "Ran a few HAWQ queries"
   
def teardown_hawq():
   shellcmd('psql -f hawq_teardown.sql')
   print "PHD tables dropped"
   
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
