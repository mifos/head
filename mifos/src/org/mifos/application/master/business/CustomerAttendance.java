/**
 * 
 */
package org.mifos.application.master.business;

import org.mifos.framework.persistence.Persistence;

/**
 * Appears to be unused.
 */
public class CustomerAttendance extends Persistence {

	public CustomerAttendance() {
	}

	private Short attendanceId;

	private Integer lookUpId;

	private String desciption;

	public Short getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(Short attendanceId) {
		this.attendanceId = attendanceId;
	}

	public String getDesciption() {
		return desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

}
