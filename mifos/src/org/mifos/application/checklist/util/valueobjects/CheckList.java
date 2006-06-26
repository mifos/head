/**

 * CheckList.java    version: 1.0

 

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

package org.mifos.application.checklist.util.valueobjects;


import java.util.Date;
import java.util.Set;

import org.mifos.application.accounts.util.valueobjects.AccountCheckList;
import org.mifos.application.master.util.valueobjects.SupportedLocales;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author imtiyazmb
 *
 */
/**
 * A class that represents a row in the 'checklist' table. 
 * This class may be customized as it is never re-generated 
 * after being created.
 */
public class CheckList extends ValueObject {
	/**
	 * Simple constructor of Checklist instances.
	 */
	public CheckList() {
	}

	/** The composite primary key value. */
	private Short checklistId;

	/** The value of the supportedLocale association. */
	private SupportedLocales supportedLocales;

	/** The value of the checklistDetailSet one-to-many association. */
	private Set checklistDetailSet;

	/** The value of the customerChecklistSet one-to-many association. */
	private CustomerCheckList customerChecklist;

	private AccountCheckList accountCheckList;

	/** The value of the simple checklistName property. */
	private String checklistName;

	/** The value of the simple checklistStatus property. */
	private Short checklistStatus;

	/** The value of the simple createdBy property. */
	private Short createdBy;

	/** The value of the simple createdDate property. */
	private Date createdDate;

	/** The value of the simple updatedBy property. */
	private Short updatedBy;

	/** The value of the simple updatedDate property. */
	private Date updatedDate;

	/**
	 * Return the simple primary key value that identifies this object.
	 * @return  Short
	 */
	public Short getChecklistId() {
		
		return checklistId;
	}

	/**
	 * Set the simple primary key value that identifies this object.
	 * @param checklistId
	 */
	public void setChecklistId(Short checklistId) {
		
		this.checklistId = checklistId;
	}

	/**
	 * Return the value of the CHECKLIST_NAME column.
	 * @return  String
	 */
	public String getChecklistName() {
		return this.checklistName;
	}

	/**
	 * Set the value of the CHECKLIST_NAME column.
	 * @param checklistName
	 */
	public void setChecklistName(String checklistName) {
		this.checklistName = checklistName;
	}

	/**
	 * Return the value of the CHECKLIST_STATUS column.
	 * @return  Short
	 */
	public Short getChecklistStatus() {
		return this.checklistStatus;
	}

	/**
	 * Set the value of the CHECKLIST_STATUS column.
	 * @param checklistStatus
	 */
	public void setChecklistStatus(Short checklistStatus) {
		this.checklistStatus = checklistStatus;
	}

	/**
	 * Return the value of the LOCALE_ID column.
	 * @return SupportedLocale
	 */
	public SupportedLocales getSupportedLocales() {
		return this.supportedLocales;
	}

	/**
	 * Set the value of the LOCALE_ID column.
	 * @param supportedLocale
	 */
	public void setSupportedLocales(SupportedLocales supportedLocales) {
		this.supportedLocales = supportedLocales;
	}

	/**
	 * Return the value of the CREATED_BY column.
	 * @return  Short
	 */
	public Short getCreatedBy() {
		return this.createdBy;
	}

	/**
	 * Set the value of the CREATED_BY column.
	 * @param createdBy
	 */
	public void setCreatedBy(Short createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Return the value of the CREATED_DATE column.
	 * @return java.util.Date
	 */
	public java.util.Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Set the value of the CREATED_DATE column.
	 * @param createdDate
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Return the value of the UPDATED_BY column.
	 * @return  Short
	 */
	public Short getUpdatedBy() {
		return this.updatedBy;
	}

	/**
	 * Set the value of the UPDATED_BY column.
	 * @param updatedBy
	 */
	public void setUpdatedBy(Short updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * Return the value of the UPDATED_DATE column.
	 * @return java.util.Date
	 */
	public java.util.Date getUpdatedDate() {
		return this.updatedDate;
	}

	/**
	 * Set the value of the UPDATED_DATE column.
	 * @param updatedDate
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	/**
	 * Return the value of the CHECKLIST_ID collection.
	 * @return ChecklistDetail
	 */
	public java.util.Set getChecklistDetailSet() {
		return this.checklistDetailSet;

	}

	/**
	 * Set the value of the CHECKLIST_ID collection.
	 * @param checklistDetailSet
	 */
	public void setChecklistDetailSet(java.util.Set checklistDetailSet) {
		this.checklistDetailSet = checklistDetailSet;
	}

	/**
	 * @return Returns the customerChecklist.
	 */
	public CustomerCheckList getCustomerChecklist() {
		return customerChecklist;
	}

	/**
	 * @param customerChecklist The customerChecklist to set.
	 */
	public void setCustomerChecklist(CustomerCheckList customerChecklist) {
		if (customerChecklist != null) {
			customerChecklist.setCheckList(this);
		}
		this.customerChecklist = customerChecklist;
	}

	/**
	 * @return  String
	 * 
	 */
	public String getResultName() {
		return "CheckListVO";
	}

	/**
	 * @return Returns the accountCheckList.
	 */
	public AccountCheckList getAccountCheckList() {
		return accountCheckList;
	}

	/**
	 * @param accountCheckList The accountCheckList to set.
	 */
	public void setAccountCheckList(AccountCheckList accountCheckList) {
		this.accountCheckList = accountCheckList;
	}
	
	public void addCheckListDetail(CheckListDetail checkListDetail){
		checkListDetail.setCheckList(this);
		checklistDetailSet.add(checkListDetail);
	}

}
