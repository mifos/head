package org.mifos.application.holiday.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.util.helpers.YesNoFlag;
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

	public List<HolidayBO> getHolidays(int year, int localId) 
	throws PersistenceException {
		SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		isoDateFormat.setLenient(false);
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LOCALE_ID", new Short((short) localId));
		try {
			parameters.put("START_OF_YEAR", isoDateFormat.parse(year + "-01-01"));
			parameters.put("END_OF_YEAR", isoDateFormat.parse(year + "-12-31"));
		}
		catch (ParseException e) {
			throw new PersistenceException(e);
		}
		
		return executeNamedQuery(NamedQueryConstants.GET_HOLIDAYS, parameters);
	}
	
	public List<RepaymentRuleEntity> getRepaymentRuleTypes(int localId) throws PersistenceException{
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("LOCALE_ID", localId);
		
		return executeNamedQuery(NamedQueryConstants.GET_REPAYMENT_RULE_TYPES, parameters);
	}
	
	public List<LoanScheduleEntity> getAllLoanSchedules(HolidayBO holiday) 
	throws PersistenceException{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FROM_DATE", holiday.getHolidayFromDate());
		parameters.put("THRU_DATE", holiday.getHolidayThruDate());
		
		return executeNamedQuery( NamedQueryConstants.ALL_LOAN_SCHEDULE,
									parameters);
	}
	
	public List<HolidayBO> getUnAppliedHolidays() throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("FLAG", YesNoFlag.NO.getValue());
		List<HolidayBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_HOLIDAYS_BY_FLAG,
				queryParameters);
		return queryResult;
	}
	
	public List<SavingsScheduleEntity> getAllSavingSchedules(HolidayBO holiday) throws PersistenceException{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FROM_DATE", holiday.getHolidayFromDate());
		parameters.put("THRU_DATE", holiday.getHolidayThruDate());
		
		return executeNamedQuery( NamedQueryConstants.ALL_SAVING_SCHEDULE,
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
	
	
	public List<HolidayBO> getDistinctYears() 
	throws PersistenceException {		
		Map<String, Object> parameters = new HashMap<String, Object>();		
		return executeNamedQuery("holiday.getDistinctYears", parameters);
	}
	
}
