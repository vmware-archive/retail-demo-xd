--Pull all of the records back
select * from realtime_orders order by order_amount desc, store_id asc;

--If spring xd is working, there should not be any orders under 5000
select min(order_amount) from realtime_orders;