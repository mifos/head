package org.mifos.framework.business.service;


import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;

public abstract class BusinessService {
	
	public abstract BusinessObject getBusinessObject(UserContext userContext); 
	
}
