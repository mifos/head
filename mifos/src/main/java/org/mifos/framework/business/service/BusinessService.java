package org.mifos.framework.business.service;


import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;

/**
 * Use of this class is @deprecated.
 * 
 * BusinessService classes generally return BusinessObject instances.
 * We plan to migrate away from this pattern and define Service classes
 * which return Data Transfer Objects (DTOs) such that the service layer
 * does not expose domain objects.
 */
public interface BusinessService {
	
	public BusinessObject getBusinessObject(UserContext userContext); 
	
}
