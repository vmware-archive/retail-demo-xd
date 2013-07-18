import sys
import commands
import os

sqlfire_home = os.getenv('SQLFIRE_HOME', "SQLFIRE_HOME_GOES_HERE_OR_ENV")
client_bind_addr = 'sqlfire'
client_port = '7711'

def setup():
  sqlf('sqlfire_setup.sql')
  print "Created SQLFire table"
   
def teardown():
   sqlf('sqlfire_teardown.sql')
   print "Dropped SQLFire data/table"
   
def query():
   sqlf('sqlfire_query.sql')
   print "Ran a couple SQLFire queries"
   
def shellcmd(cmd):
    (status, output) = commands.getstatusoutput(cmd)
    print output

def sqlf(file):
   shellcmd("%s/bin/sqlf run -client-bind-address=%s -client-port=%s -file=%s" % (sqlfire_home,client_bind_addr,client_port,file))

def main():
  args = sys.argv[1:]
  if not args:
    print "usage (type one option): setup | teardown | query";
    sys.exit(1)
	
  functionList = {'setup': setup, 'teardown': teardown, 'query': query}
                  
  functionList[args[0]]()
  
if __name__ == "__main__":
  main()
