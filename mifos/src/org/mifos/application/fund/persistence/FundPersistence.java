package org.mifos.application.fund.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class FundPersistence extends Persistence {
	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.FUNDLOGGER);

	public Integer getFundNameCount(String fundName)throws PersistenceException {
		logger.debug("getting the fund name count for :" + fundName);
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(FundConstants.FUND_NAME, fundName);
		return (Integer) execUniqueResultNamedQuery(
				NamedQueryConstants.CHECK_FUND_NAME_EXIST, queryParameters);
	}
	
	public List<FundCodeEntity> getFundCodes() throws PersistenceException {
		return (List<FundCodeEntity>)executeNamedQuery(NamedQueryConstants.GET_FUND_CODES,null);
	}
	
	public List<FundBO> getSourcesOfFund() throws PersistenceException {
		return executeNamedQuery(NamedQueryConstants.PRDSRCFUNDS, null);
	}
	
	public FundBO getFund(String fundName) throws PersistenceException{
		Map<String,Object> queryParameters=new HashMap<String,Object>();
		queryParameters.put(FundConstants.FUND_NAME,fundName);
		return (FundBO)execUniqueResultNamedQuery(NamedQueryConstants.GET_FUND_FOR_GIVEN_NAME,queryParameters);
	}
	
	public FundBO getFund(Short fundId)	throws PersistenceException {
		return (FundBO) getPersistentObject(FundBO.class,fundId);
	}
}
