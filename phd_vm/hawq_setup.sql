--Create external table for the HDFS realtime order data stream
CREATE EXTERNAL TABLE realtime_orders_pxf (customer_id int, order_id integer, order_amount numeric(10,2), store_id integer) 
LOCATION ('pxf://pivhdsne:50070/xd/order_stream/order_stream-*.txt?Fragmenter=HdfsDataFragmenter&Accessor=TextFileAccessor&Resolver=TextResolver') 
FORMAT 'TEXT' (DELIMITER = '|'); 

--Create HAWQ Table       
CREATE TABLE realtime_orders_hawq 
AS SELECT * FROM realtime_orders_pxf;