/**
 * 
 */
package org.mifos.application.customer.center.business;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.security.util.UserContext;



/**
 * @author sumeethaec
 *
 */
public class CenterBO extends CustomerBO {
	
	public CenterBO() {
		super();
	}

		
	public boolean isCustomerActive()
	{
		if(getCustomerStatus().getStatusId().equals(CustomerConstants.CENTER_ACTIVE_STATE))
			return true;
		return false;
	}
}
