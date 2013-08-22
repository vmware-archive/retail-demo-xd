xd-demo with Pivotal HD Retail data
===================================

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
because it does not provide fast enough response time and full ANSI compliance. Some of these orders have very high amounts($5000+ is our wire-tap flag) that we want forward to an in-memory database that one of our
real-time fraud detection applications uses to catch criminals before they leave the store. The in-memory database will need to
support several hundred thousand transactions per second. 

<strong>In order to get this running with Pivotal HD</strong>
<ol>
<li>Start Pivotal HD instance. It is optional to run the "pivotal-samples" data labs to populate the retail_demo
DB with HAWQ tables/data. The "pivotal-samples" github project is located at: 

https://github.com/PivotalHD/pivotal-samples</li>

<li>Install Spring XD from source. The Github project is located at: 

https://github.com/SpringSource/spring-xd</li>

<li>Update your spring-xd hadoop config ($SPRING_XD/conf/hadoop.properties) to reflect webhdfs:
	
fs.default.name=webhdfs://192.168.72.172:50070</li>

<li>Open config.py and add entries for each property. This is very important to ensure connectivity to Pivotal HD and SQLFire.</li>

<li>In a terminal window run(will scp python demo scripts to pivotal hd and sqlfire VMs. Will copy spring xd scripts, lib jars, modules and sink config:
   <br/><code>./install.py</code>
</li> 
<li>Run Spring XD DIRT/Admin in a terminal window
   <br/><code>$SPRING_XD/xd/bin/xd-singlenode</code>
</li>
<li>Run Spring XD Shell in a terminal window 
 
<br/><code>$SPRING_XD/shell/bin/spring-xd-shell</code>
</li>
<li>In Spring XD Shell - Create Hadoop Stream and tap stream
Refer to xd_streams.txt file for details on the syntax. You may need to change to "tap @ order_stream" reflect the proper
sqlfire hostname.  make sure you create the stream for accepting the training set data</li>

<li>[PIVOTALHD TERMINAL] Open an ssh session to your Pivotal VM and run this script. You must do this before starting the data stream.
   <br/><code>./demo.py setup_hdfs</code>
</li>

<li>In a terminal window, run build_training_data.py to start a data stream that will populate a baseline set of data to build a statistical model.  This will populate 100K records so it will take a bit of time
   <br/><code>./build_training_data.py </code>
</li>

<li>[PIVOTALHD TERMINAL]  Open an ssh session to your Pivotal VM and run this script.  This will take a bit of time.
   <br/><code>./demo.py train_analytic</code>
</li>

<li>[SQLFIRE TERMINAL]  Open an ssh session to your SQLFire VM and run this script.
   <br/><code>./demo.py setup</code>
</li>

<li>In a terminal window, run send_data.py to start a data stream simulation.
   <br/><code>./send_data.py</code>
</li>

<li>[SQLFIRE TERMINAL] Verify that SQLFire is getting only order amounts > 5000
<br/><code>./demo.py query</code>
</li>

<li>[PIVOTALHD TERMINAL] Create PXF and HAWQ Tables
   <br/><code>./demo.py setup_hawq</code>
</li>

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
<li>[PIVOTALHD TERMINAL] Teardown HAWQ tables
   <br/><code>./demo.py teardown_hawq</code>
</li>
<li>[SQLFIRE TERMINAL] Teardown SQLFire tables
   <br/><code>./demo.py teardown</code>
</li>
</ol>

xd-demo-client
==============

Update web.xml spring.profiles.active param to control which active profile to use:
	-- 'sqlfire': read data from SQLFire
	-- 'gemfire': read data from GemFire JSON region

Update app.properties (src/main/webapps/WEB-INF/classes) to reflect the IP addresses of your gemfire and sqlfire environments

The application will be available at: http://localhost:<port>/xd-demo-client/resources/stores.html

