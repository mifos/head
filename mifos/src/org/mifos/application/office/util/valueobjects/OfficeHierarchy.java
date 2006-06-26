/**

 * OfficeHierarchy.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class is value object for the officeHierarchy
 * @author rajenders
 *
 */
public class OfficeHierarchy extends ValueObject{
	private static final long serialVersionUID=666666666666l;
	/**
	 * This would hold the hierarchyId
	 */
	private Integer hierarchyId;
	/**
	 * This would hold the start date for this hierarchy
	 */
	private Date startDate;
	/**
	 * This would hold the end date for this hierarchy
	 */
	private Date endDate;
	/**
	 * This would hold the update date for this hierarchy
	 */
	private Date updatedDate;	
	/**
	 * This would hold the officeid for this hierarchy
	 */
	private Office office;
	/**
	 * This would hold the parentOffice for this hierarchy
	 */
	private Office parentOffice;
	/**
	 * This would hold the status for this hierarchy
	 */
	private Short status;

	/**
	 * this would hold the updatedBy
	 */
	private Short updatedBy;
	/**
	 * This Function returns the endDate
	 * @return Returns the endDate.
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * This function sets the endDate
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * This Function returns the hierarchyId
	 * @return Returns the hierarchyId.
	 */
	public Integer getHierarchyId() {
		return hierarchyId;
	}
	/**
	 * This function sets the hierarchyId
	 * @param hierarchyId The hierarchyId to set.
	 */
	public void setHierarchyId(Integer hierarchyId) {
		this.hierarchyId = hierarchyId;
	}
	/**
	 * This Function returns the office
	 * @return Returns the office.
	 */
	public Office getOffice() {
		return office;
	}
	/**
	 * This function sets the office
	 * @param office The office to set.
	 */
	public void setOffice(Office office) {
		this.office = office;
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
	public void setParentOffice(Office parentOffice) {
		this.parentOffice = parentOffice;
	}
	/**
	 * This Function returns the startDate
	 * @return Returns the startDate.
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * This function sets the startDate
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * This Function returns the status
	 * @return Returns the status.
	 */
	public Short getStatus() {
		return status;
	}
	/**
	 * This function sets the status
	 * @param status The status to set.
	 */
	public void setStatus(Short status) {
		this.status = status;
	}
	/**
	 * This Function returns the updatedDate
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
	/**
	 * This function returns the updatedBy
	 * @return Returns the updatedBy.
	 */
	public Short getUpdatedBy() {
		return updatedBy;
	}
	/**
	 * This function sets the updatedBy
	 * @param updatedBy The updatedBy to set.
	 */
	public void setUpdatedBy(Short updatedBy) {
		this.updatedBy = updatedBy;
	}

}
