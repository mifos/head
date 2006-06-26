/**
 * 
 */
package org.mifos.application.customer.center.exception;

import org.mifos.framework.exceptions.ApplicationException;

/**
 * @author sumeethaec
 *
 */
public class LoanOfficerNotActiveException extends ApplicationException {
	public String getKey(){
		return "LoanOfficerNotActiveException";
	}
}
