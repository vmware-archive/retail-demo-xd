/**
 * Simple order filter that checks for bad
 * data and an order amount > 5000
 *
 */
amount = Double.valueOf(payload.split(",")[2])
 
if (payload.contains("BAD_DATA") || amount < 5000)
{
   return false
}
else
{
   return true
}