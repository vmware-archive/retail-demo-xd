xd-demo with Pivotal HD Retail data
===================================

<strong>Contributors</strong><br/>
<ul>
<li>James Williams - jwilliams@gopivotal.com</li>
<li>Michael Goddard - mgoddard@gopivotal.com</li>
<li>Adam Zwickey - azwickey@gopivotal.com</li>
</ul>

<strong>Demo User Story</strong><br/>
We want to ingest real time orders from our POS system directly to HDFS via a pipe delimited HTTP post. 
A sample post looks like:

<pre>Customer ID, Order ID, Order Amount, Store ID
curl -d "{\"orderid\":\"123\",\"storeid\":\"456\",\"customerid\":\"789\",\"orderamount\":\"5000.01\"}" http://localhost:8000 - Good Post
curl -d "{\"orderid\":\"BAD_DATA\",\"storeid\":\"456\",\"customerid\":\"789\",\"orderamount\":\"5000.01\"}" http://localhost:8000 - Bad Post
123|456|789|5000.01 - Dream State in HDFS with HAWQ and in-memory Query
</pre>

We are going to re-use some integration work that was done in the past and we need to transform and filter the POS data before 
ingesting into hadoop. The HTTP stream will accept JSON formatted key/value pairs of Order data. 
Some orders have bad data. We need to filter these records before persisting them to HDFS. After landing the data into hadoop, 
we would like to run SQL analytics on the orders to see if they match known fraudulent orders from the past. Hive is not an option 
because it does not provide fast enough response time and full ANSI compliance. We want to run a logistic regression model on all  
orders to feed our real-time fraud detection applications that aim to catch criminals before they leave the store. The logistic regression 
model needs to be re-trained periodically via a scheduled process. The in-memory fraud data store needs to be flushed on a configurable
interval and HDFS files need to be archived via a scheduled process. 

<strong>In order to get this running with Pivotal HD</strong>
<ol>
<li>Start Pivotal HD instance. It is optional to run the "pivotal-samples" data labs to populate the retail_demo
DB with HAWQ tables/data. The "pivotal-samples" github project is located at: 

https://github.com/PivotalHD/pivotal-samples</li>

<li>Download and install the latest Spring XD binary. The project is located at: 

http://projects.spring.io/spring-xd/</li>

<<<<<<< HEAD
<li>Update your spring-xd hadoop config ($SPRING_XD/conf/hadoop.properties) to reflect your hdfs address:
=======
<li>Update your spring-xd hadoop config ($SPRING_XD/xd/config/hadoop.properties) to reflect webhdfs:
>>>>>>> 4985ef63c23b7c2723e426e91d14f685bebacd48
	
fs.default.name=hdfs://my-hadoop:8020</li>

<li>Open config.py and add entries for each property. This is very important to ensure connectivity to Pivotal HD and SQLFire.</li>

<li>In a terminal window run(will scp python demo scripts to pivotal hd and sqlfire VMs. Will copy spring xd scripts, lib jars, modules and sink config:
   <br/><code>./install.py</code>
</li> 
<li>Run 3 Spring XD runtimes in terminal windows(redis, admin, container)
  <br/><code>sudo sysctl -w net.inet.tcp.msl=1000<br/>
            $SPRING_XD/redis/bin/redis-server<br/>
            $SPRING_XD/xd/bin/xd-admin --hadoopDistro phd1<br/>
            $SPRING_XD/xd/bin/xd-container --hadoopDistro phd1</code>
</li>
<li>Run Spring XD Shell in a terminal window 
 
<br/><code>$SPRING_XD/shell/bin/spring-xd-shell --hadoopDistro phd1</code>
</li>
<li>In Spring XD Shell - Create Hadoop ingest, Pivotal HD analytics tap and SQLFire sink.
<code>script --file ../../xd/cmd/create-all.cmds</code></li>

<li>[PIVOTALHD TERMINAL] Open an ssh session to your Pivotal VM and run this script. You must do this before starting the data stream.
   <br/><code>./demo.py setup_hdfs</code>
</li>

<li>In a terminal window, run send_data.py to start a data stream simulation.
   <br/><code>./send_data.py</code>
</li>

<li>[SQLFIRE TERMINAL] Verify that SQLFire is getting only a small subset of orders
<br/><code>./demo.py query</code>
</li>

<li>In Spring XD Shell - Re-run batch jobs(should delete SQLFire data, populate HAWQ tables, and re-run analytic training model)
<br/><code>script --file ../../xd/cmd/deploy-batch.cmds</code></li>

<li>In Spring XD Shell - Reset the richgauge taps to 0)
<br/><code>script --file ../../xd/cmd/reset-taps.cmds</code></li>

<li>[PIVOTALHD_TERMINAL] Run a PXF and HAWQ Query
   <br/><code>./demo.py query_hawq</code>
</li>

<li>Install DB Visualizer and run queries through a JDBC client GUI. http://www.dbvis.com/.
You will need to add a new "Cache" Driver JAR for SQLFire. You will need to modify '/data/1/hawq_master/gpseg-1/pg_hba.conf' in your Pivotal HD VM to remote connect.
</li>
<li>[PIVOTALHD TERMINAL] Restart Pivotal HD via the stop/start scripts.
<pre>
/home/gpadmin/stop_all.sh;
/home/gpadmin/start_all.sh;</pre>
</li>

<li>In Spring XD Shell - Remove all streams/taps from Spring XD. Does not delete any data)
<code>script --file ../../xd/cmd/destroy-all.cmds</code></li>
</ol>

xd-demo-client
==============
<ol>
	<li>Update app.properties (src/main/webapps/WEB-INF/classes) to reflect the IP addresses 
	of your sqlfire environment</li>
	<li>Open a terminal and build the war via maven
	<br/><code>mvn install</code>
	</li>
	<li>Copy the WAR file to a working tc Server or Tomcat server</li>
	<li>The application will be available at: http://localhost:8080/xd-demo-client/resources/index.html</li>
</ol>

