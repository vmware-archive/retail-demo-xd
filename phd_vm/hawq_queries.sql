--pull 10 orders from the pxf table
select * from realtime_orders_pxf order by order_amount desc, store_id asc limit 10;

--pull 10 orders from the hawq table
select * from realtime_orders_hawq order by order_amount desc, store_id asc limit 10;

--join the realtime order table to an archive order table to look for signs of customer specific fraud

--select a_order.customer_id, 
--       a_order.store_id as PAST_FRAUD_LOC, 
--       b_order.store_id as CURRENT_FRAUD_LOC, 
--       a_order.fraud_code as PAST_FRAUD_CODE,
--       b_order.order_amount as AMOUNT
--from retail_demo.orders_hawq a_order, 
--     realtime_orders_hawq b_order
--where a_order.customer_id=b_order.customer_id
--and a_order.fraud_code IS NOT NULL;