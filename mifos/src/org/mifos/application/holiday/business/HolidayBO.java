package org.mifos.application.holiday.business;

import java.util.Date;

import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.holiday.util.resources.HolidayConstants;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.DateUtils;

public class HolidayBO extends BusinessObject {

	private HolidayPK holidayPK;

	private Date holidayThruDate;

	private String holidayName;
	
	private RepaymentRuleEntity repaymentRuleEntity;

	private boolean validationEnabled = true;
	
	private Short holidayChangesAppliedFlag;   
	
	public boolean isValidationEnabled() {
		return validationEnabled;
	}

	public void setValidationEnabled(boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
	}

	protected HolidayBO() {
		this.holidayPK = null;
		this.holidayChangesAppliedFlag = YesNoFlag.NO.getValue();
	}

	public HolidayBO(HolidayPK holidayPK, Date holidayThruDate,
			String holidayName, RepaymentRuleEntity repaymentRuleEntity) throws ApplicationException {

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
		this.repaymentRuleEntity = repaymentRuleEntity;
		this.holidayChangesAppliedFlag = YesNoFlag.NO.getValue();
	}
	
	public HolidayBO(HolidayPK holidayPK, Date holidayThruDate,
			String holidayName, short repaymentRuleId, String lookupValueKey) throws ApplicationException {

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
		this.repaymentRuleEntity = new RepaymentRuleEntity(repaymentRuleId, lookupValueKey);
		this.holidayChangesAppliedFlag = YesNoFlag.NO.getValue();
	}

	public HolidayPK getHolidayPK() {
		return this.holidayPK;
	}

	public RepaymentRuleEntity getRepaymentRuleEntity() {
		return this.repaymentRuleEntity;
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
	private void setRepaymentRuleEntity(RepaymentRuleEntity repaymentRuleEntity) {
		this.repaymentRuleEntity = repaymentRuleEntity;
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
	
	public Short getHolidayChangesAppliedFlag() {
		return holidayChangesAppliedFlag;
	}

	public void setHolidayChangesAppliedFlag(Short flag) {
		this.holidayChangesAppliedFlag = flag;
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
		
		if(this.getRepaymentRuleEntity().getLookUpValue().equals(RepaymentRuleTypes.SAME_DAY.getValue()))
			this.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());
		
		new HolidayPersistence().createOrUpdate(this);
		
		//this block should not be here
		//HolidayUtils.rescheduleLoanRepaymentDates(this);
		//HolidayUtils.rescheduleSavingDates(this);
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
		return repaymentRuleEntity.getLookUpValue();
	}
	
	public Short getRepaymentRuleId()
	{
		return repaymentRuleEntity.getId();
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