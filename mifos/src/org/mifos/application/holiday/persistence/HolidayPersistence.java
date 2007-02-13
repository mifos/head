package org.mifos.application.holiday.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.exceptions.PersistenceException;

public class HolidayPersistence extends MasterPersistence {

	public HolidayPersistence() {
	}

	public HolidayBO getHoliday(Short holidayId)
			throws PersistenceException {
		return (HolidayBO) getPersistentObject(HolidayBO.class, holidayId);
	}
	
	/* we need a way to make this worx
	 * because our PK is the HolidayPK
	 * public HolidayBO getHoliday(HolidayPK holidayPK)
		throws PersistenceException {
		return (HolidayBO) getPersistentObject(HolidayBO.class, holidayPK);
	}*/

	public List<HolidayBO> getHolidays(int year, int localId) throws PersistenceException {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LOCALE_ID", localId);
		parameters.put("START_OF_YEAR", year + "-01-01");
		parameters.put("END_OF_YEAR", year + "-12-31");
		
		return executeNamedQuery(NamedQueryConstants.GET_HOLIDAYS, parameters);
	}
	
	public List<RepaymentRuleEntity> getRepaymentRuleTypes(int localId) throws PersistenceException{
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LOCALE_ID", localId);
		
		return executeNamedQuery(NamedQueryConstants.GET_REPAYMENT_RULE_TYPES, parameters);
	}
	
	// getAllLoanScheduales
	public List<LoanScheduleEntity> getAllLoanScheduales(HolidayBO holiday) throws PersistenceException{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FROM_DATE", holiday.getHolidayFromDate());
		parameters.put("THRU_DATE", holiday.getHolidayThruDate());
		
		return executeNamedQuery( NamedQueryConstants.ALL_LOAN_SCHEDULE,
									parameters);
	}
	
	public int isValidHolidayState(Short levelId, Short stateId,
			boolean isCustomer) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("levelId", levelId);
		queryParameters.put("stateId", stateId);
		Integer count;
		if (isCustomer)
			count = (Integer) execUniqueResultNamedQuery(
					NamedQueryConstants.CUSTOMER_VALIDATESTATE, queryParameters);
		else
			count = (Integer) execUniqueResultNamedQuery(
					NamedQueryConstants.PRODUCT_VALIDATESTATE, queryParameters);
		return count;
	}

}
