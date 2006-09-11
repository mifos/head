package org.mifos.application.customer.center.business.service;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class CenterBusinessService extends BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public CenterBO getCenter(Integer customerId) {
		return new CenterPersistence().getCenter(customerId);
	}
	
	public CenterBO getCenterBySystemId(String globalCustNum) throws ServiceException{
		try {
			return new CenterPersistence().getCenterBySystemId(globalCustNum);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
}
