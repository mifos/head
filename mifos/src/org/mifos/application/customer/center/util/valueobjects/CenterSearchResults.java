/**
 * 
 */
package org.mifos.application.customer.center.util.valueobjects;

import java.io.Serializable;

public class CenterSearchResults implements Serializable {
	/**Denotes the office id on which the search is conducted*/
	private short parentOfficeId;
	/**Denotes the office name to whicht he center belongs*/
	private String parentOfficeName;
	/**Denotes the center id of the center being searched*/
	private String centerSystemId;
	/**Denotes the center name on which the search is conducted*/
	private String centerName;
	/**
	 * Method which returns the centerId	
	 * @return Returns the centerId.
	 */
	public String getCenterSystemId() {
		return centerSystemId;
	}
	/**
	 * Method which sets the centerId
	 * @param centerId The centerId to set.
	 */
	public void setCenterSystemId(String centerId) {
		this.centerSystemId = centerId;
	}
	/**
	 * Method which returns the centerName	
	 * @return Returns the centerName.
	 */
	public String getCenterName() {
		return centerName;
	}
	/**
	 * Method which sets the centerName
	 * @param centerName The centerName to set.
	 */
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	/**
	 * Method which returns the parentOfficeId	
	 * @return Returns the parentOfficeId.
	 */
	public short getParentOfficeId() {
		return parentOfficeId;
	}
	/**
	 * Method which sets the parentOfficeId
	 * @param parentOfficeId The parentOfficeId to set.
	 */
	public void setParentOfficeId(short parentOfficeId) {
		this.parentOfficeId = parentOfficeId;
	}
	/**
	 * Method which returns the parentOfficeName	
	 * @return Returns the parentOfficeName.
	 */
	public String getParentOfficeName() {
		return parentOfficeName;
	}
	/**
	 * Method which sets the parentOfficeName
	 * @param parentOfficeName The parentOfficeName to set.
	 */
	public void setParentOfficeName(String parentOfficeName) {
		this.parentOfficeName = parentOfficeName;
	}
	
	
	
}
