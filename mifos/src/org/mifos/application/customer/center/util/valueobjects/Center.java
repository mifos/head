/**
 * 
 */
package org.mifos.application.customer.center.util.valueobjects;

import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.util.valueobjects.Customer;



/**
 * @author sumeethaec
 *
 */
public class Center extends Customer {
	
	public String getResultName(){
		return CenterConstants.CENTERVO;
	}
	     
	
	
}
