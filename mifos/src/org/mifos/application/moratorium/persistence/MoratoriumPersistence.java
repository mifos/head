package org.mifos.application.moratorium.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.moratorium.business.MoratoriumBO;
import org.mifos.framework.exceptions.PersistenceException;

public class MoratoriumPersistence extends MasterPersistence {

	// for getting all records from moratorium table(closed & open)
	public List<MoratoriumBO> getMoratoriums() throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();		
		List<MoratoriumBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_MORATORIUMS, queryParameters);
		return queryResult;
	}
	
	// for getting moratorium by moratoriumId
	public List<MoratoriumBO> getMoratoriumById(String moratoriumId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("moratoriumId", moratoriumId);
		List<MoratoriumBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_MORATORIUM_BY_ID, queryParameters);
		return queryResult;
	}
	
	// for getting moratoriums by customerId
	public List<MoratoriumBO> getMoratoriumByCustomerId(String customerId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("customerId", customerId);
		List<MoratoriumBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_MORATORIUM_BY_CUSTOMER_ID, queryParameters);
		return queryResult;
	}
	
	// for getting moratoriums by officeId
	public List<MoratoriumBO> getMoratoriumByOfficeId(String officeId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("officeId", officeId);
		List<MoratoriumBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_MORATORIUM_BY_OFFICE_ID, queryParameters);
		return queryResult;
	}
}
