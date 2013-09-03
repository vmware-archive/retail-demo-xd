import org.springframework.jdbc.core.JdbcTemplate
import groovy.json.JsonSlurper

class LogisticRegression  {

	JdbcTemplate template
	float threshold
    
	public boolean execute(Object payload) {
	    def jsonOrder = new JsonSlurper().parseText( payload )
		String sql = "select madlib.logistic(madlib.array_dot(model.coef,ARRAY[" + 
				jsonOrder.orderAmount +"," +
				jsonOrder.storeId + "," +
				jsonOrder.numItems + "]::double precision[])) as score from model"
		float score = template.queryForObject(sql, Float)
		if(score >= threshold) 
		{
		    System.out.println("fraud score alert:" + score)
			return true
		}
		else 
		{
			return false
		}
	}
}
	