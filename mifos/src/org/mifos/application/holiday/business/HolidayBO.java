package org.mifos.application.holiday.business;

import java.util.Date;

import org.mifos.application.holiday.exceptions.HolidayException;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.resources.HolidayConstants;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.PersistenceException;

public /*abstract*/ class HolidayBO extends BusinessObject {

	private HolidayPK holidayPK;

	private Date holidayThruDate;

	private String holidayName;

	private String repaymentRule; // view property
	
	private SupportedLocalesEntity supportedLocales;

	protected HolidayBO() {
		this.holidayPK = null;
	}

	// was protected
	public HolidayBO(HolidayPK holidayPK, Date holidayThruDate, String holidayName, 
			            Short localeId, String repaymentRule)//, Short userId)
			throws HolidayException {
		
		//setCreateDetails(userId, new Date());
		this.holidayPK = new HolidayPK();

		if (holidayPK != null) {
			System.out.println("holidayPK.getHolidayFromDate() = "+holidayPK.getHolidayFromDate());
			this.holidayPK.setHolidayFromDate(holidayPK.getHolidayFromDate());
			this.holidayPK.setRepaymentRuleId(holidayPK.getRepaymentRuleId());			
		} else {
			throw new HolidayException(
					HolidayConstants.HOLIDAY_CREATION_EXCEPTION);
		}
		this.holidayThruDate = holidayThruDate;
		this.holidayName = holidayName;
		this.repaymentRule = repaymentRule;
		//this.supportedLocales = new SupportedLocalesEntity(localeId);
	}

	public HolidayPK getHolidayPK() {
		return this.holidayPK;
	}

	public Short getOfficeId() {
		return this.holidayPK.getRepaymentRuleId();
	}

	public Date getHolidayFromDate() {
		return this.holidayPK.getHolidayFromDate();
	}

	public Date getHolidayThruDate() {
		return this.holidayThruDate;
	}

	public String getHolidayName() {
		return this.holidayName;
	}

	public void setHolidayPK(HolidayPK holidayPK) {
		this.holidayPK = holidayPK;
	}

	private void setOfficeId(Short repaymentRuleId) {
		this.holidayPK.setRepaymentRuleId(repaymentRuleId);
	}

	private void setHolidayFromDate(Date holidayFromDate) {
		this.holidayPK.setHolidayFromDate(holidayFromDate);
	}

	private void setHolidayThruDate(Date holidayThruDate) {
		this.holidayThruDate = holidayThruDate;
	}

	private void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}

	public SupportedLocalesEntity getSupportedLocales() {
		return this.supportedLocales;
	}

	private void setSupportedLocales(SupportedLocalesEntity supportedLocales) {
		this.supportedLocales = supportedLocales;
	}

	public void save() throws HolidayException {
		try {
			new HolidayPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new HolidayException(e);
		}
	}

	//protected 
	public void update(HolidayPK holidayPK, Date holidayThruDate, 
						  String holidayName, Short localeId)//, Short userId)
			throws HolidayException {

		this.holidayName = holidayName;
		this.holidayPK.setRepaymentRuleId(holidayPK.getRepaymentRuleId());
		this.holidayPK.setHolidayFromDate(holidayPK.getHolidayFromDate());
		this.supportedLocales = new SupportedLocalesEntity(localeId);
		
		// this block should not be here
		try {
			new HolidayPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new HolidayException(e);
		}
		// end of block
	}

	protected void validateHolidayState(Short masterTypeId, Short stateId,
			boolean isCustomer) throws HolidayException {
		Integer records;
		try {
			records = new HolidayPersistence().isValidHolidayState(
					masterTypeId, stateId, isCustomer);
			if (records.intValue() != 0)
				throw new HolidayException(
						HolidayConstants.EXCEPTION_STATE_ALREADY_EXIST);
		} catch (PersistenceException pe) {
			throw new HolidayException(pe);
		}
	}

	public String getRepaymentRule() {
		return repaymentRule;
	}

}
