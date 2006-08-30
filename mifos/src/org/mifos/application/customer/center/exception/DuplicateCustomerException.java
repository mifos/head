/**
 * 
 */
package org.mifos.application.customer.center.exception;

import org.mifos.framework.exceptions.ApplicationException;

public class DuplicateCustomerException extends ApplicationException {
	public DuplicateCustomerException(Object values[] ){
		super(values);
	}
	public DuplicateCustomerException(String key , Object values[] ){
		super(key , values);
	}
	public String getKey(){
		return "Customer.DuplicateCustomerName";
	}
}
