import sys
import commands
import config
 
def shellcmd(cmd):
  (status, output) = commands.getstatusoutput(cmd)
  print output
  print "Command Ran: " + cmd

def main():
  args = sys.argv[1:]
  if len(args) > 0:
    if args[0].startswith("-") or args[0].startswith("h"):
      print """
      Before running this script, specify demo config properties in config.py. 
      This install script will attempt to copy files to a local Spring XD instance 
      and scp files to remote VMs. This script only works on mac or linux.
      """
  else:
    
    scp_to_phd = "scp ./phd_demo.py %s@%s:/home/%s/demo.py" % (config.phd_username, config.phd_hostname, config.phd_username)
    shellcmd(scp_to_phd) 
     
    scp_to_sqlf = "scp ./sqlf_demo.py %s@%s:/home/%s/demo.py" % (config.sqlf_username, config.sqlf_hostname, config.sqlf_username)
    shellcmd(scp_to_sqlf) 
    
    scp_to_sqlf2 = "scp ./config.py %s@%s:/home/%s/config.py" % (config.sqlf_username, config.sqlf_hostname, config.sqlf_username)
    shellcmd(scp_to_sqlf2) 
   
    cp_to_xd = "cp -r ./springxd_files/* %s/xd" % (config.springxd_home)
    shellcmd(cp_to_xd) 
  
if __name__ == "__main__":
  main()
