package org.mifos.application.holiday.business;

import java.util.Date;

import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.HolidayUtils;
import org.mifos.application.holiday.util.resources.HolidayConstants;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ApplicationException;

public /*abstract*/ class HolidayBO extends BusinessObject {

	private HolidayPK holidayPK;

	private Date holidayThruDate;

	private String holidayName;

	private Short repaymentRuleId;
	private String repaymentRule; // view property
	
	private SupportedLocalesEntity supportedLocales;

	protected HolidayBO() {
		this.holidayPK = null;
	}

	// was protected
	public HolidayBO(HolidayPK holidayPK, Date holidayThruDate, String holidayName, 
			            Short localeId, Short repaymentRuleId, String repaymentRule)
			throws ApplicationException {
		
		this.holidayPK = new HolidayPK();

		if (holidayPK != null) {
			this.holidayPK.setOfficeId(holidayPK.getOfficeId());
			this.holidayPK.setHolidayFromDate(holidayPK.getHolidayFromDate());
		} else {
			throw new ApplicationException(
					HolidayConstants.HOLIDAY_CREATION_EXCEPTION);
		}
		this.holidayThruDate = holidayThruDate;
		this.holidayName = holidayName;
		this.repaymentRuleId = repaymentRuleId;
		this.repaymentRule = repaymentRule;
	}

	public HolidayPK getHolidayPK() {
		return this.holidayPK;
	}

	public Short getRepaymentRuleId() {
		return this.repaymentRuleId;
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

	private void setRepaymentRuleId(Short repaymentRuleId) {
		this.repaymentRuleId = repaymentRuleId;
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

	public void save() throws ApplicationException {
		try {
			new HolidayPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new ApplicationException(e);
		}
	}

	//protected 
	public void update(HolidayPK holidayPK, Date holidayThruDate, 
						  String holidayName, Short localeId)
			throws ApplicationException {

		this.holidayName = holidayName;
		this.holidayPK.setOfficeId(holidayPK.getOfficeId());
		this.holidayPK.setHolidayFromDate(holidayPK.getHolidayFromDate());
		this.supportedLocales = new SupportedLocalesEntity(localeId);
		
		// this block should not be here
		try {
			new HolidayPersistence().createOrUpdate(this);
			HolidayUtils.rescheduleLoanRepaymentDates(this);
		} catch (PersistenceException e) {
			throw new ApplicationException(e);
		}
		// end of block
	}

	protected void validateHolidayState(Short masterTypeId, Short stateId,
			boolean isCustomer) throws ApplicationException {
		Integer records;
		try {
			records = new HolidayPersistence().isValidHolidayState(
					masterTypeId, stateId, isCustomer);
			if (records.intValue() != 0)
				throw new ApplicationException(
						HolidayConstants.EXCEPTION_STATE_ALREADY_EXIST);
		} catch (PersistenceException pe) {
			throw new ApplicationException(pe);
		}
	}

	public String getRepaymentRule() {
		return repaymentRule;
	}
	

}
