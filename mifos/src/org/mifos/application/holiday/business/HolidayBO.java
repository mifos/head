package org.mifos.application.holiday.business;

import java.util.Date;

import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.HolidayUtils;
import org.mifos.application.holiday.util.resources.HolidayConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.DateUtils;

public class HolidayBO extends BusinessObject {

	private HolidayPK holidayPK;

	private Date holidayThruDate;

	private String holidayName;

	private Short repaymentRuleId;

	private String repaymentRule; // view property

	private boolean validationEnabled = true;
	
	public boolean isValidationEnabled() {
		return validationEnabled;
	}

	public void setValidationEnabled(boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
	}

	protected HolidayBO() {
		this.holidayPK = null;
	}

	public HolidayBO(HolidayPK holidayPK, Date holidayThruDate,
			String holidayName, Short localeId, Short repaymentRuleId,
			String repaymentRule) throws ApplicationException {

		this.holidayPK = new HolidayPK();

		if (holidayPK != null) {
			this.holidayPK.setOfficeId(holidayPK.getOfficeId());
			this.holidayPK.setHolidayFromDate(holidayPK.getHolidayFromDate());
		}
		else {
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

	@SuppressWarnings("unused")
	// see .hbm.xml file
	private void setRepaymentRuleId(Short repaymentRuleId) {
		this.repaymentRuleId = repaymentRuleId;
	}

	@SuppressWarnings("unused")
	// see .hbm.xml file
	private void setHolidayFromDate(Date holidayFromDate) {
		this.holidayPK.setHolidayFromDate(holidayFromDate);
	}

	@SuppressWarnings("unused")
	// see .hbm.xml file
	private void setHolidayThruDate(Date holidayThruDate) {
		this.holidayThruDate = holidayThruDate;
	}

	@SuppressWarnings("unused")
	// see .hbm.xml file
	private void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}

	public void save() throws ApplicationException {
		if (this.getHolidayThruDate() == null) {
			this.setHolidayThruDate(this.getHolidayFromDate());
		}
		
		if(isValidationEnabled()) {
			this.validateFromDateAgainstCurrentDate(this.getHolidayFromDate());
			this.validateFromDateAgainstThruDate(this.getHolidayFromDate(), this.getHolidayThruDate());
		}

		new HolidayPersistence().createOrUpdate(this);
	}

	public void update(HolidayPK holidayPK, Date holidayThruDate,
			String holidayName) throws ApplicationException {
		this.holidayName = holidayName;
		this.holidayPK.setOfficeId(holidayPK.getOfficeId());
		this.holidayPK.setHolidayFromDate(holidayPK.getHolidayFromDate());

		if (this.getHolidayThruDate() == null) {
			this.setHolidayThruDate(this.getHolidayFromDate());
		}

		if(isValidationEnabled()) {
			this.validateFromDateAgainstCurrentDate(this.getHolidayFromDate());
			this.validateFromDateAgainstThruDate(this.getHolidayFromDate(), this.getHolidayThruDate());
		}

		// this block should not be here
		new HolidayPersistence().createOrUpdate(this);
		HolidayUtils.rescheduleLoanRepaymentDates(this);
		HolidayUtils.rescheduleSavingDates(this);
		// end of block
	}

	protected void validateHolidayState(Short masterTypeId, Short stateId,
			boolean isCustomer) throws ApplicationException {
		Integer records;
		records = new HolidayPersistence().isValidHolidayState(masterTypeId,
				stateId, isCustomer);
		if (records.intValue() != 0) {
			throw new ApplicationException(
					HolidayConstants.EXCEPTION_STATE_ALREADY_EXIST);
		}
	}

	public String getRepaymentRule() {
		return repaymentRule;
	}

	private void validateFromDateAgainstCurrentDate(Date fromDate)
			throws ApplicationException {
		if (DateUtils.getDateWithoutTimeStamp(fromDate.getTime()).compareTo(
				DateUtils.getCurrentDateWithoutTimeStamp()) <= 0) {
			throw new ApplicationException(HolidayConstants.INVALIDFROMDATE);
		}
	}

	private void validateFromDateAgainstThruDate(Date fromDate, Date thruDate)
			throws ApplicationException {
		if (DateUtils.getDateWithoutTimeStamp(fromDate.getTime()).compareTo(
				DateUtils.getDateWithoutTimeStamp(thruDate.getTime())) > 0) {
			throw new ApplicationException(HolidayConstants.INVALIDTHRUDATE);
		}
	}

}
