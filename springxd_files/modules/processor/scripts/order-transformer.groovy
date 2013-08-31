/**
 * Takes a JSON payload and converts it into
 * a pipe delimited String.
 *
 */
import groovy.json.JsonSlurper
 
def jsonOrder = new JsonSlurper().parseText( payload )
def pipeString = jsonOrder.customerId + "|" + jsonOrder.orderId + "|" + jsonOrder.orderAmount + "|" + jsonOrder.storeId + "|" + jsonOrder.numItems
return pipeString
