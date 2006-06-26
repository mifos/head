/**
 
 * AccountStatusChangeHistory.java    version: 1.0
 
 
 
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
package org.mifos.application.accounts.util.valueobjects;

import java.sql.Date;
import java.util.Locale;

import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.valueobjects.ValueObject;

public class AccountStatusChangeHistory extends ValueObject{
	/** The value of the ACCOUNT_STATUS_CHANGE_ID column. */
	private Integer accountStatusChangeId;
	/** The value of the ACCOUNT_ID column. */
	private Integer accountId;
	/** The value of the OLD_STATUS column. It holds the status Id of old status*/
	private Short oldStatus;
	/** The value of the NEW_STATUS column. It holds the status Id of new status*/
	private Short newStatus;
	/** The id of the person */
	private Short personnelId;
	/** The value of the CHANGED_DATE column. It holds the value on which status was changed*/
	private Date changedDate;
	/** The is the name of old status that will be populated according to user locale when history is to be displayed */
	private String oldStatusName;
	/** The is the name of new status that will be populated according to user locale when history is to be displayed */
	private String newStatusName;
	/** The name of the personnel, who changed the status */
	private String personnelName;
	
	private String userPrefferedDate=null; 
	private Locale userLocale=null;
	
	public AccountStatusChangeHistory(){}
	public AccountStatusChangeHistory(String oldStatus,String newStatus,java.util.Date changedDate,String personnelName){
		this.oldStatusName=oldStatus;
		this.newStatusName=newStatus;
		this.changedDate=new Date(changedDate.getTime());
		this.personnelName=personnelName;
	}
	
	/**
	 * @return Returns the userLocale.
	 */
	public Locale getLocale() {
		return userLocale;
	}
	/**
	 * @param userLocale The userLocale to set.
	 */
	public void setLocale(Locale userLocale) {
		this.userLocale = userLocale;
	}
	/**
	 * @return Returns the mfiDate.
	 */
	public String getUserPrefferedDate() {
		return DateHelper.getUserLocaleDate(getLocale(),getChangedDate().toString()); 
	}
	
	/**
	 * This function returns the accountId
	 * @return Returns the accountId.
	 */
	public Integer getAccountId() {
		return accountId;
	}
	
	/**
	 * This function sets the accountId
	 * @param accountId the accountId to set.
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	
	/**
	 * This function returns the accountStatusChangeId
	 * @return Returns the accountStatusChangeId.
	 */
	public Integer getAccountStatusChangeId() {
		return accountStatusChangeId;
	}
	
	/**
	 * This function sets the accountStatusChangeId
	 * @param accountStatusChangeId the accountStatusChangeId to set.
	 */
	public void setAccountStatusChangeId(Integer accountStatusChangeId) {
		this.accountStatusChangeId = accountStatusChangeId;
	}
	
	/**
	 * This function returns the changedDate
	 * @return Returns the changedDate.
	 */
	public Date getChangedDate() {
		return changedDate;
	}
	
	/**
	 * This function sets the changedDate
	 * @param changedDate the changedDate to set.
	 */
	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}
	
	/**
	 * This function returns the newStatus
	 * @return Returns the newStatus.
	 */
	public Short getNewStatus() {
		return newStatus;
	}
	
	/**
	 * This function sets the newStatus
	 * @param newStatus the newStatus to set.
	 */
	public void setNewStatus(Short newStatus) {
		this.newStatus = newStatus;
	}
	
	/**
	 * This function returns the oldStatus
	 * @return Returns the oldStatus.
	 */
	public Short getOldStatus() {
		return oldStatus;
	}
	
	/**
	 * This function sets the oldStatus
	 * @param oldStatus the oldStatus to set.
	 */
	public void setOldStatus(Short oldStatus) {
		this.oldStatus = oldStatus;
	}
	
	/**
	 * This function returns the personnelName
	 * @return Returns the personnelName.
	 */
	public String getPersonnelName() {
		return personnelName;
	}
	
	/**
	 * This function sets the personnelName
	 * @param personnelName the personnelName to set.
	 */
	public void setPersonnelName(String personnelName) {
		this.personnelName = personnelName;
	}
	
	/**
	 * This function returns the personnelId
	 * @return Returns the personnelId.
	 */
	public Short getPersonnelId() {
		return personnelId;
	}
	
	/**
	 * This function sets the personnelId
	 * @param personnelId the personnelId to set.
	 */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}
	
	/**
	 * This function returns the newStatusName
	 * @return Returns the newStatusName.
	 */
	public String getNewStatusName() {
		return newStatusName;
	}
	
	/**
	 * This function sets the newStatusName
	 * @param newStatusName the newStatusName to set.
	 */
	public void setNewStatusName(String newStatusName) {
		this.newStatusName = newStatusName;
	}
	
	/**
	 * This function returns the oldStatusName
	 * @return Returns the oldStatusName.
	 */
	public String getOldStatusName() {
		return oldStatusName;
	}
	
	/**
	 * This function sets the oldStatusName
	 * @param oldStatusName the oldStatusName to set.
	 */
	public void setOldStatusName(String oldStatusName) {
		this.oldStatusName = oldStatusName;
	}
}
