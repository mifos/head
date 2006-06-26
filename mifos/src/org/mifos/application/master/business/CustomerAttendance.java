/**
 * 
 */
package org.mifos.application.master.business;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author mohammedn
 *
 */
public class CustomerAttendance extends ValueObject {
	
	public CustomerAttendance() {
	}

	private static final long serialVersionUID = 432659879671L;
	
	private Short attendanceId;
	
	private Integer lookUpId;
	
	private String desciption;

	
	public Short getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(Short attendanceId) {
		this.attendanceId = attendanceId;
	}

	/**
	 * @return Returns the desciption.
	 */
	public String getDesciption() {
		return desciption;
	}

	/**
	 * @param desciption The desciption to set.
	 */
	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	/**
	 * @return Returns the lookUpId.
	 */
	public Integer getLookUpId() {
		return lookUpId;
	}

	/**
	 * @param lookUpId The lookUpId to set.
	 */
	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

	
	


}
