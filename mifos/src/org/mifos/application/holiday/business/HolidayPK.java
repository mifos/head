package org.mifos.application.holiday.business;

import java.io.Serializable;
import java.util.Date;

public class HolidayPK implements Serializable {

	private Date holidayFromDate;
	private Short officeId;
	
	public HolidayPK() {}
	
	public HolidayPK(Short officeId, Date holidayFromDate) {
		this.officeId = officeId;
		this.holidayFromDate = holidayFromDate;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HolidayPK other = (HolidayPK) obj;
		if (holidayFromDate == null) {
			if (other.holidayFromDate != null)
				return false;
		}
		else if (!holidayFromDate.equals(other.holidayFromDate))
			return false;
		if (officeId == null) {
			if (other.officeId != null)
				return false;
		}
		else if (!officeId.equals(other.officeId))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((holidayFromDate == null) ? 0 : holidayFromDate.hashCode());
		result = PRIME * result + ((officeId == null) ? 0 : officeId.hashCode());
		return result;
	}

	public Date getHolidayFromDate() {
		return this.holidayFromDate;
	}

	public void setHolidayFromDate(Date holidayFromDate) {
		this.holidayFromDate = holidayFromDate;
	}

	/**
	 * @return the officeId
	 */
	public Short getOfficeId() {
		return officeId;
	}

	/**
	 * @param officeId the officeId to set
	 */
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}	
}
