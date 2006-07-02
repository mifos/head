/**

 * PrdOffering.java    version: 1.0

 

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

package org.mifos.application.productdefinition.util.valueobjects;

import org.mifos.application.master.util.valueobjects.PrdApplicableMaster;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class PrdOffering extends ValueObject {
	/**
	 * default constructor
	 */
	public PrdOffering() {
		super();
	}
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 2652353453451L;
	/** The composite primary key value. */
    private java.lang.Short prdOfferingId;

    /** The value of the office association. */
    private Office office;

    /** The value of the prdCategory association. */
    private ProductCategory prdCategory;

    /** The value of the prdStatus association. */
    private PrdStatus prdStatus;

    /** The value of the prdType association. */
    private ProductType prdType;

    /** The value of the prdApplicableMaster association. */
    private PrdApplicableMaster prdApplicableMaster;

    /** The value of the simple globalPrdOfferingNum property. */
    private java.lang.String globalPrdOfferingNum;

    /** The value of the simple startDate property. */
    private java.sql.Date startDate;

    /** The value of the simple endDate property. */
    private java.sql.Date endDate;

    /** The value of the simple glcodeId property. */
    private java.lang.Short glcodeId;

    /** The value of the simple recurrenceId property. */
    private java.lang.Short recurrenceId;

    /** The value of the simple prdOfferingName property. */
    private java.lang.String prdOfferingName;

    /** The value of the simple prdOfferingShortName property. */
    private java.lang.String prdOfferingShortName;

    /** The value of the simple clientOrGroups property. */
    private java.lang.Short clientOrGroups;

    /** The value of the simple description property. */
    private java.lang.String description;

    /** The value of the simple createdDate property. */
    private java.sql.Date createdDate;

    /** The value of the simple createdBy property. */
    private java.lang.Integer createdBy;

    /** The value of the simple updatedDate property. */
    private java.sql.Date updatedDate;

    /** The value of the simple updatedBy property. */
    private java.lang.Integer updatedBy;
    /** The value of the simple version no property. */
    private java.lang.Integer versionNo;

	/**
	 * @return Returns the versionNo.
	 */
	public java.lang.Integer getVersionNo() {
		return versionNo;
	}

	/**
	 * @param versionNo The versionNo to set.
	 */
	public void setVersionNo(java.lang.Integer versionNo) {
		this.versionNo = versionNo;
	}

	/**
	 * @return Returns the clientOrGroups}.
	 */
	public java.lang.Short getClientOrGroups() {
		return clientOrGroups;
	}

	/**
	 * @param clientOrGroups The clientOrGroups to set.
	 */
	public void setClientOrGroups(java.lang.Short clientOrGroups) {
		this.clientOrGroups = clientOrGroups;
	}

	/**
	 * @return Returns the createdBy}.
	 */
	public java.lang.Integer getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy The createdBy to set.
	 */
	public void setCreatedBy(java.lang.Integer createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return Returns the createdDate}.
	 */
	public java.sql.Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate The createdDate to set.
	 */
	public void setCreatedDate(java.sql.Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return Returns the description}.
	 */
	public java.lang.String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	/**
	 * @return Returns the endDate}.
	 */
	public java.sql.Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate The endDate to set.
	 */
	public void setEndDate(java.sql.Date endDate) {
		this.endDate = endDate;
	}
	
	
	/**
	 * @return Returns the glcodeId}.
	 */
	public java.lang.Short getGlcodeId() {
		return glcodeId;
	}

	/**
	 * @param glcodeId The glcodeId to set.
	 */
	public void setGlcodeId(java.lang.Short glcodeId) {
		this.glcodeId = glcodeId;
	}

	/**
	 * @return Returns the globalPrdOfferingNum}.
	 */
	public java.lang.String getGlobalPrdOfferingNum() {
		return globalPrdOfferingNum;
	}

	/**
	 * @param globalPrdOfferingNum The globalPrdOfferingNum to set.
	 */
	public void setGlobalPrdOfferingNum(java.lang.String globalPrdOfferingNum) {
		this.globalPrdOfferingNum = globalPrdOfferingNum;
	}

	/**
	 * @return Returns the office}.
	 */
	public Office getOffice() {
		return office;
	}

	/**
	 * @param office The office to set.
	 */
	public void setOffice(Office office) {
		this.office = office;
	}

	/**
	 * @return Returns the prdApplicableMaster}.
	 */
	public PrdApplicableMaster getPrdApplicableMaster() {
		return prdApplicableMaster;
	}

	/**
	 * @param prdApplicableMaster The prdApplicableMaster to set.
	 */
	public void setPrdApplicableMaster(PrdApplicableMaster prdApplicableMaster) {
		this.prdApplicableMaster = prdApplicableMaster;
	}

	/**
	 * @return Returns the prdCategory}.
	 */
	public ProductCategory getPrdCategory() {
		return prdCategory;
	}

	/**
	 * @param prdCategory The prdCategory to set.
	 */
	public void setPrdCategory(ProductCategory prdCategory) {
		this.prdCategory = prdCategory;
	}

	/**
	 * @return Returns the prdOfferingId}.
	 */
	public java.lang.Short getPrdOfferingId() {
		return prdOfferingId;
	}

	/**
	 * @param prdOfferingId The prdOfferingId to set.
	 */
	public void setPrdOfferingId(java.lang.Short prdOfferingId) {
		this.prdOfferingId = prdOfferingId;
	}

	/**
	 * @return Returns the prdOfferingName}.
	 */
	public java.lang.String getPrdOfferingName() {
		return prdOfferingName;
	}

	/**
	 * @param prdOfferingName The prdOfferingName to set.
	 */
	public void setPrdOfferingName(java.lang.String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

	/**
	 * @return Returns the prdOfferingShortName}.
	 */
	public java.lang.String getPrdOfferingShortName() {
		return prdOfferingShortName;
	}

	/**
	 * @param prdOfferingShortName The prdOfferingShortName to set.
	 */
	public void setPrdOfferingShortName(java.lang.String prdOfferingShortName) {
		this.prdOfferingShortName = prdOfferingShortName;
	}

	/**
	 * @return Returns the prdStatus}.
	 */
	public PrdStatus getPrdStatus() {
		return prdStatus;
	}

	/**
	 * @param prdStatus The prdStatus to set.
	 */
	public void setPrdStatus(PrdStatus prdStatus) {
		this.prdStatus = prdStatus;
	}
	/**
	 * @return Returns the prdType.
	 */
	public ProductType getPrdType() {
		return prdType;
	}

	/**
	 * @param prdType The prdType to set.
	 */
	public void setPrdType(ProductType prdType) {
		this.prdType = prdType;
	}

	/**
	 * @return Returns the recurrenceId}.
	 */
	public java.lang.Short getRecurrenceId() {
		return recurrenceId;
	}

	/**
	 * @param recurrenceId The recurrenceId to set.
	 */
	public void setRecurrenceId(java.lang.Short recurrenceId) {
		this.recurrenceId = recurrenceId;
	}

	/**
	 * @return Returns the startDate}.
	 */
	public java.sql.Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate The startDate to set.
	 */
	public void setStartDate(java.sql.Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * @return Returns the updatedBy}.
	 */
	public java.lang.Integer getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy The updatedBy to set.
	 */
	public void setUpdatedBy(java.lang.Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return Returns the updatedDate}.
	 */
	public java.sql.Date getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * @param updatedDate The updatedDate to set.
	 */
	public void setUpdatedDate(java.sql.Date updatedDate) {
		this.updatedDate = updatedDate;
	}
}
