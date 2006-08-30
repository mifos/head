/**

 * Office.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.application.office.util.valueobjects;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class represents the main value object for the office module
 * used to create update office record in the database
 */
public class Office extends ValueObject
{
	static final long serialVersionUID = 0l;
	/**
	 * This would hold the database version for the office
	 */
	private Integer versionNo;
	/**
	 * This would hold the personnel for this office
	 */
//	private Personnel personnel;
	
	/**
	 * This would hold id of the personnelId who created this record 
	 */
	private Short createdBy;
	
	/**
	 * This would hold the personnelId who updated  this record
	 */
	private Short updatedBy;
	
	/**
	 * This would hold the  creation date for this office
	 */
	
	private Date createdDate;
	/**
	 * This would hold the officeId for the office 
	 */
	private Short officeId;
	/**
	 * This would hold the globalOfficeNum for the office
	 */
	private String globalOfficeNum;
	/**
	 * This would hold the OfficeLevel for the office
	 */
	private OfficeLevel level;
	/**
	 * This would hold the maxChildCount 
	 */
	private Integer maxChildCount;
	/**
	 * This would hold the office searchid
	 */
	private String searchId;
	/**
	 * This would hold the operation mode of the office localserver or remote server
	 */
	private Short operationMode;
	/**
	 * This would hold the parent office for the office
	 */
	private Office parentOffice;
	/**
	 * This would hold the status of the office
	 */
	private OfficeStatus status;
	/**
	 * This would hold the custom field for the office
	 */
	private Set customFieldSet;
	/**
	 * This would hold the office name
	 */
	private String officeName;
	/**
	 * This would hold the office short name
	 */
	private String shortName;
	/**
	 * This would hold the office address
	 */
    private OfficeAddress address;

    /**
     * This would hold the officeCode
     */
	private OfficeCode officeCode;

	/**
	 * this would hold the update date
	 */
	private Date updatedDate;
	


	/**
	 * Default constructor for Office
	 *
	 */
	public Office()
	{
		customFieldSet = new HashSet();
	}

	/**
	 * This would sets the officeId
	 * @param officeId
	 */
	public Office(Short officeId)
	{
		this.officeId = officeId;
	}

	/**
	 * This Function returns the createdDate
	 * @return Returns the createdDate.
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * This function sets the createdDate
	 * @param createdDate The createdDate to set.
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * This Function returns the globalOfficeNum
	 * @return Returns the globalOfficeNum.
	 */
	public String getGlobalOfficeNum() {
		return globalOfficeNum;
	}
	/**
	 * This function sets the globalOfficeNum
	 * @param globalOfficeNum The globalOfficeNum to set.
	 */
	public void setGlobalOfficeNum(String globalOfficeNum) {
		this.globalOfficeNum = globalOfficeNum;
	}
	/**
	 * This Function returns the level
	 * @return Returns the level.
	 */
	public OfficeLevel getLevel() {
		return level;
	}
	/**
	 * This function sets the level
	 * @param level The level to set.
	 */
	public void setLevel(OfficeLevel level) {
		this.level = level;
	}
	/**
	 * This Function returns the maxChildCount
	 * @return Returns the maxChildCount.
	 */
	public Integer getMaxChildCount() {
		return maxChildCount;
	}
	/**
	 * This function sets the maxChildCount
	 * @param maxChildCount The maxChildCount to set.
	 */
	public void setMaxChildCount(Integer maxChildCount) {
		this.maxChildCount = maxChildCount;
	}
	/**
	 * This Function returns the name
	 * @return Returns the name.
	 */
	public String getOfficeName() {
		return officeName;
	}
	/**
	 * This function sets the name
	 * @param name The name to set.
	 */
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public Short getOfficeId() {
		return officeId;
	}
	/**
	 * This function sets the officeId
	 * @param officeId The officeId to set.
	 */
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}
	/**
	 * This function returns the operationMode
	 * @return Returns the operationMode.
	 */
	public Short getOperationMode() {
		return operationMode;
	}
	/**
	 * This function sets the operationMode
	 * @param operationMode The operationMode to set.
	 */
	public void setOperationMode(Short operationMode) {
		this.operationMode = operationMode;
	}
	/**
	 * This Function returns the parentOffice
	 * @return Returns the parentOffice.
	 */
	public Office getParentOffice() {
		return parentOffice;
	}
	/**
	 * This function sets the parentOffice
	 * @param parentOffice The parentOffice to set.
	 */
	public void setParentOffice(Office parentOffice)
	{
		this.parentOffice = parentOffice;
	}
	/**
	 * This Function returns the searchId
	 * @return Returns the searchId.
	 */
	public String getSearchId() {
		return searchId;
	}
	/**
	 * This function sets the searchId
	 * @param searchId The searchId to set.
	 */
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	/**
	 * This Function returns the shortName
	 * @return Returns the shortName.
	 */
	public String getShortName() {
		return shortName;
	}
	/**
	 * This function sets the shortName
	 * @param shortName The shortName to set.
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	/**
	 * This Function returns the status
	 * @return Returns the status.
	 */
	public OfficeStatus getStatus() {
		return status;
	}
	/**
	 * This function sets the status
	 * @param status The status to set.
	 */
	public void setStatus(OfficeStatus status) {
		this.status = status;
	}

	/* * This Function returns the updatedDate
	 * @return Returns the updatedDate.
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}
	/**
	 * This function sets the updatedDate
	 * @param updatedDate The updatedDate to set.
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

/*
	 * This function returns the customField
	 * @return Returns the customField.
	 */
	public Set getCustomFieldSet() {
		return customFieldSet;
	}
	/**
	 * This function sets the customField
	 * @param customField The customField to set.
	 */
	public void setCustomFieldSet(Set customFieldSet){
		for(Object obj : customFieldSet){
			OfficeCustomField customField = getOfficeCustomField(((OfficeCustomField)obj).getFieldId());
			if(customField==null) {
				((OfficeCustomField)obj).setOffice(this);
				this.customFieldSet.add(obj);
			} else {
				customField.setFieldValue(((OfficeCustomField)obj).getFieldValue());
			}
		}
	}

	private OfficeCustomField getOfficeCustomField(Short fieldId) {
		if(null != this.customFieldSet && this.customFieldSet.size()>0) {
			for(Object obj : this.customFieldSet) {
				if(((OfficeCustomField)obj).getFieldId().equals(fieldId)) 
					return (OfficeCustomField)obj;
			}
		}
		return null;
	}
/*
	public void setCreatedPersonnel(Personnel personnel)
	{
		this.personnel = personnel;
	}
	public Personnel getCreatedPersonnel()
	{
		return personnel;
	}

	public void setUpdatedPersonnel(Personnel personnel)
	{
		this.personnel = personnel;
	}
	public Personnel getUpdatedPersonnel()
	{
		return personnel;
	}
*/
	/**
	 * This function set the officeCode
	 * @param officeCode
	 */
	public void setOfficeCode(OfficeCode officeCode)
	{

		this.officeCode = officeCode;
	}
	/**
	 * This function return the office code
	 */
	public OfficeCode getOfficeCode()
	{

		return officeCode;
	}
	/**
	 * This function returns the office address
	 * @return
	 */
	public OfficeAddress getAddress() {
		return address;
	}
	/**
	 * This function sets the address
	 * @param address The address to set.
	 */
	public void setAddress(OfficeAddress address)
	
	{
		
		if(address != null)
			address.setOffice(this);
		
		this.address = address;
	}
	/* (non-Javadoc)
	 * @see org.mifos.framework.util.valueobjects.ValueObject#getResultName()
	 */
	@Override
	public String getResultName() {
		
		return OfficeConstants.OFFICEVO;
	}



	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	/**
	 * This function returns the createdBy
	 * @return Returns the createdBy.
	 */
	
	public Short getCreatedBy() {
		return createdBy;
	}

	/**
	 * This function sets the createdBy
	 * @param createdBy the createdBy to set.
	 */
	
	public void setCreatedBy(Short createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * This function returns the updatedBy
	 * @return Returns the updatedBy.
	 */
	
	public Short getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * This function sets the updatedBy
	 * @param updatedBy the updatedBy to set.
	 */
	
	public void setUpdatedBy(Short updatedBy) {
		this.updatedBy = updatedBy;
	}

}
