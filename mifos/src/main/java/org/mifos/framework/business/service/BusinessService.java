package org.mifos.framework.business.service;


import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;

/**
 * There's some logic to populate a form from a business service 
 * in {@link BaseAction}.  We don't always do things that way.
 * In many cases, there is no need for a business service layer.
 */
public abstract class BusinessService {
	
	public abstract BusinessObject getBusinessObject(UserContext userContext); 
	
}
