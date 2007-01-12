package org.mifos.application.holiday.business;

import java.io.Serializable;
import java.util.Date;

public class HolidayPK implements Serializable {

	private Date holidayFromDate;
	
	public HolidayPK() {}
	
	public HolidayPK(Date holidayFromDate) {
		this.holidayFromDate = holidayFromDate;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof HolidayPK) {
			HolidayPK that = (HolidayPK)o;
			return this.holidayFromDate.equals(that.holidayFromDate);
		} else { 
			return false;
		  }
	}

	@Override
	public int hashCode() {
		return holidayFromDate.hashCode();
	}

	public Date getHolidayFromDate() {
		return this.holidayFromDate;
	}

	public void setHolidayFromDate(Date holidayFromDate) {
		this.holidayFromDate = holidayFromDate;
	}	
}
