package org.mifos.application.personnel.util.valueobjects;

import java.sql.Date;

import org.mifos.framework.util.valueobjects.ValueObject;

public class PersonnelMovement extends ValueObject{
	/**It is the running number of personnel movement table */
	private Short personnelMovementId;
	
	/**Denotes the id of the personnel for which movement is being recorded */
	private Short personnelId;
	
	/**Denotes the old office id  of the personnel (before transfer).*/
	private Short officeId;
	
	/**Denotes the startdate in old office.*/
	private Date startDate;
	
	/**Denotes the endDate in old office.*/
	private Date endDate;
	
	/**Denotes the the date at which personnel is transferred.*/
	private Date createdDate;
	
	/**Denotes the the personnel who transferred the user.*/
	private Short createdBy;
	
	/**Denotes the the version number of the personnel movement record.*/
	private Short versionNo;
	
	/**
     * Simple constructor of PersonnelMovement instances.
     */
	public PersonnelMovement() {}
	
	/**
     * Return the value of the createdBy.
     * @return Short
     */
	public Short getCreatedBy() {
		return createdBy;
	}
	
	/**
     * Sets the value of createdBy 
     * @param createdBy
     */
	public void setCreatedBy(Short createdBy) {
		this.createdBy = createdBy;
	}
	
	/**
     * Return the value of the createdDate.
     * @return Date
     */
	public Date getCreatedDate() {
		return createdDate;
	}
	
	/**
     * Sets the value of createdDate 
     * @param createdDate
     */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	/**
     * Return the value of the endDate.
     * @return Date
     */
	public Date getEndDate() {
		return endDate;
	}
	
	/**
     * Sets the value of endDate 
     * @param endDate
     */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
     * Return the value of the officeId.
     * @return Short
     */
	public Short getOfficeId() {
		return officeId;
	}
	
	/**
     * Sets the value of officeId 
     * @param officeId
     */
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}
	
	/**
     * Return the value of the personnelId.
     * @return Short
     */
	public Short getPersonnelId() {
		return personnelId;
	}
	
	/**
     * Sets the value of personnelId 
     * @param personnelId
     */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}
	
	/**
     * Return the value of the personnelMovementId.
     * @return Short
     */
	public Short getPersonnelMovementId() {
		return personnelMovementId;
	}
	
	/**
     * Sets the value of personnelMovementId 
     * @param personnelMovementId
     */
	public void setPersonnelMovementId(Short personnelMovementId) {
		this.personnelMovementId = personnelMovementId;
	}
	
	/**
     * Return the value of the startDate.
     * @return Date
     */
	public Date getStartDate() {
		return startDate;
	}
	
	/**
     * Sets the value of startDate 
     * @param startDate
     */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
     * Return the value of the versionNo.
     * @return Short
     */
	public Short getVersionNo() {
		return versionNo;
	}
	
	/**
     * Sets the value of versionNo 
     * @param versionNo
     */
	public void setVersionNo(Short versionNo) {
		this.versionNo = versionNo;
	}
	
}
