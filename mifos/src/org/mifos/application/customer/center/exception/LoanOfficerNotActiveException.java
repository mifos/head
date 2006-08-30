/**
 * 
 */
package org.mifos.application.customer.center.exception;

import org.mifos.framework.exceptions.ApplicationException;

public class LoanOfficerNotActiveException extends ApplicationException {
	public String getKey(){
		return "LoanOfficerNotActiveException";
	}
}
