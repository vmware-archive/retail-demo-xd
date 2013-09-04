import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pivotal.example.xd.OrderAnalytics;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml" })
@Ignore
public class AnalyticsTestCase {

	@Autowired
	RedisTemplate redisTemplate;

	@Test
	public void test() {
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		
		//System.out.println(redisTemplate.opsForValue().get("key"));
		//redisTemplate.opsForValue().set("key", "abcd");
		
		//OrderAnalytics oa = new OrderAnalytics((String)redisTemplate.opsForValue().get("richgauges.order_gauge"));
		//System.out.println(oa);
		
	}

}
