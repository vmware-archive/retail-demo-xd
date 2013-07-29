/**
 * Takes a JSON payload and converts it into
 * a pipe delimited String.
 *
 */
import groovy.json.JsonSlurper
 
def jsonOrder = new JsonSlurper().parseText( payload )
def pipeString = jsonOrder.customerid + "|" + jsonOrder.orderid + "|" + jsonOrder.orderamount + "|" + jsonOrder.storeid
return pipeString
