package org.mifos.application.customer.group.business.service;

import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;

public class GroupBusinessService implements BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
	
	public GroupBO findBySystemId(String globalCustNum) throws ServiceException{
		try {
			return new GroupPersistence().findBySystemId(globalCustNum);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public GroupBO getGroup(Integer customerId) throws ServiceException{
		try{
			return new GroupPersistence().geGroup(customerId);
		}catch(PersistenceException pe){
			throw new ServiceException(pe);
		}
	}
	public QueryResult search(String searchString,
			Short userId) throws ServiceException {
		
		try {
			return new GroupPersistence().search(searchString,userId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		
	}
	public QueryResult searchForAddingClientToGroup(String searchString,
			Short userId) throws ServiceException {
		
		try {
			return new GroupPersistence().searchForAddingClientToGroup(searchString,userId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		
	}
}
