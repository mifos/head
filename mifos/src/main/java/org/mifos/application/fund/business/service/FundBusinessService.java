package org.mifos.application.fund.business.service;

import java.util.List;

import org.mifos.application.fund.business.FundBO;
import org.mifos.application.fund.persistence.FundPersistence;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class FundBusinessService implements BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
	
	public List<FundCodeEntity> getFundCodes() throws ServiceException {
		try {
			return new FundPersistence().getFundCodes();
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}
	
	public List<FundBO> getSourcesOfFund() throws ServiceException {
		try {
			return new FundPersistence().getSourcesOfFund();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public FundBO getFund(String fundName) throws ServiceException{
		try {
			return new FundPersistence().getFund(fundName);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public FundBO getFund(Short fundId) throws ServiceException{
		try {
			return new FundPersistence().getFund(fundId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
}
