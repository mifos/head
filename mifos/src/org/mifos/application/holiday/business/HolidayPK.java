package org.mifos.application.holiday.business;

import java.io.Serializable;
import java.util.Date;

public class HolidayPK implements Serializable {

	private Short repaymentRuleId;
	
	private Date holidayFromDate;
	
	public HolidayPK() {}
	
	public HolidayPK(Short repaymentRuleId, Date holidayFromDate) {
		this.repaymentRuleId = repaymentRuleId;
		this.holidayFromDate = holidayFromDate;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof HolidayPK) {
			HolidayPK that = (HolidayPK)o;
			return this.repaymentRuleId.equals(that.repaymentRuleId) && this.holidayFromDate.equals(that.holidayFromDate);
		} else { 
			return false;
		  }
	}

	@Override
	public int hashCode() {
		return repaymentRuleId.hashCode() + holidayFromDate.hashCode();
	}


	public Short getRepaymentRuleId() {
		return this.repaymentRuleId;
	}

	public Date getHolidayFromDate() {
		return this.holidayFromDate;
	}

	public void setRepaymentRuleId(Short repaymentRuleId) {
		this.repaymentRuleId = repaymentRuleId;
	}

	public void setHolidayFromDate(Date holidayFromDate) {
		this.holidayFromDate = holidayFromDate;
	}	
}
