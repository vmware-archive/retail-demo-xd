xd-demo with Pivotal HD Retail data
===================================

Demo User Story
We want to ingest real time orders from our POS system directly to HDFS via a pipe delimited HTTP post. 
A sample post looks like:

Customer ID, Order ID, Order Amount, Store ID
curl -d "3096,3339,8216.32,3240" http://localhost:8000 - Good Post
curl -d "BAD_DATA,3339,8216.32,3240" http://localhost:8000 - Bad Post
3096|3339|8216|32,324 - Dream State in HDFS with HAWQ and in-memory Query

We are going to re-use some integration work that was done in the past and we need to transform and filter the POS data before 
ingesting into hadoop. First, the order delimiter is a "comma", we need the delimiter to be a Pipe. 
Second, some orders have bad data. We need to filter these records before persisting them to HDFS. After landing the data into hadoop, 
we would like to run SQL analytics on the orders to see if they match known fraudulent orders from the past. Hive is not an option 
because it does not provide fast enough response time and full ANSI compliance.

BONUS: Some of these orders have very high amounts($5000+ is our wire-tap flag) that we want forward to an in-memory database that one of our
real-time fraud detection applications uses to catch criminals before they leave the store. The in-memory database will need to
support several hundred thousand transactions per second. 

In order to get this running with Pivotal HD
1) Start Pivotal HD instance. It is optional to run the "pivotal-samples" data labs to populate the retail_demo
DB with HAWQ tables/data. The "pivotal-samples" github project is located at: https://github.com/PivotalHD/pivotal-samples

2) Install Spring XD from source. The Github project is located at: https://github.com/SpringSource/spring-xd

3) Update your spring-xd hadoop config ($SPRING_XD/conf/hadoop.properties) to reflect webhdfs:
	fs.default.name=webhdfs://192.168.72.172:50070

4) Copy order-filter.groovy, order-transformer.groovy and order-filter2.groovy to $SPRING_XD/xd/modules/processor/scripts directory

5) Copy xd_vm/lib jars to $SPRING_XD/xd/lib. There are 4 jars that are needed for the SQLFire tap to work.

6) Copy xd_vm/sink/sqlf.xml to $SPRING_XD/xd/modules/sink. The sqlf.xml config file will use a class in xd-demo-0.0.1-SNAPSHOT.jar. 
	
7) Run Spring XD DIRT/Admin in a terminal window
$SPRING_XD/xd/bin/xd-singlenode 

8) Run Spring XD Shell in a terminal window 
$SPRING_XD/shell/bin/spring-xd-shell 

9) In Spring XD Shell - Create Hadoop Stream and tap stream
Refer to xd_streams.txt file for details on the syntax.

10) Copy phd_vm scripts to your Pivotal HD VM. You will use demo.py to create tables, run queries and hdfs directories. 
Before sending data to spring xd. HDFS folder must be created to avoid errors.
python demo.py setup_hdfs    

11) Copy sqlf_vm scripts to your SQLFire VM. [SQLFIRE VM] You will use demo.py to create tables and run queries. 
Before sending data to spring xd. SQLFire tables must be created to avoid errors.
Run "python demo.py setup" 

12) Run send_data.py
python send_data.py

13) [SQLFIRE VM] Verify that SQLFire is getting only order amounts > 5000
python demo.py query

14) [PIVOTALHD_VM] Create PXF and HAWQ Tables
python demo.py setup_hawq

15) [PIVOTALHD_VM] Run a PXF and HAWQ Query
python demo.py query_hawq

16) [PIVOTALHD_VM] Run a PXF and HAWQ Query
python demo.py teardown_hawq

