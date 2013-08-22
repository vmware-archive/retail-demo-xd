xd-demo-client
==============

Update web.xml spring.profiles.active param to control which active profile to use:
	-- 'sqlfire': read data from SQLFire
	-- 'gemfire': read data from GemFire JSON region

Update app.properties (src/main/webapps/WEB-INF/classes) to reflect the IP addresses of your gemfire and sqlfire environments

The application will be available at: http://localhost:<port>/xd-demo-client/resources/stores.html