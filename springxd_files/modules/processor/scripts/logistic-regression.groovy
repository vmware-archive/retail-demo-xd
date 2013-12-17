import org.springframework.jdbc.core.JdbcTemplate
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

class LogisticRegression {

	JdbcTemplate template
	float threshold
	static long lastModelUpdate = 0L
	static String getModelSql = "SELECT UNNEST(coef) coef FROM model"
	static coef = new double[3] // { orderAmount, storeId, numItems }
	static final long DT_MAX_MILLIS = 60000 // One minute

	public float getScore (String orderAmount, String storeId, String numItems) {
		updateModel()
				
		// Compute inner product of incoming transaction with coeff vector
		float dotProd = 0.0f 
		dotProd += coef[0] * Double.parseDouble(orderAmount) 
		dotProd += coef[1] * Double.parseDouble(storeId) 
		dotProd += coef[2] * Double.parseDouble(numItems) 
		
		// Apply the exponential 
		return 1.0/(1.0 + Math.exp(-1.0 * dotProd)) // [0.5, 1.0) 
	} 
		
	// Keep this interface constant 
	public boolean execute(Object payload) { 
		def jso = new JsonSlurper().parseText(payload) 
		float score = getScore(jso.orderAmount, jso.storeId, jso.numItems) 
		if (score >= threshold) { 
			System.out.println("fraud score alert: " + score + "\n" + "customer id: " + jso.customerId)
			return true
		} else {
			return false
		}
	} 
	
	private void updateModel() {
		// Re-initialize model, every so often
		long now = System.currentTimeMillis()
		synchronized (this) {
			if(now - lastModelUpdate > DT_MAX_MILLIS) {
				System.out.println("Fetching updates for model ...");
				def rowMapList = template.queryForList(getModelSql)
				int i = 0
				rowMapList.each { row -> coef[i++] = row["coef"] }
				lastModelUpdate = now
			}
		}		
	}
}
