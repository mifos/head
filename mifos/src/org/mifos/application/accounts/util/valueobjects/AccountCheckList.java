/**
 
 * AccountCheckList.java    version: 1.0
 
 
 
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
package org.mifos.application.accounts.util.valueobjects;

import org.mifos.application.checklist.util.valueobjects.CheckList;
import org.mifos.framework.util.valueobjects.ValueObject;

public class AccountCheckList extends ValueObject{
	private static final long serialVersionUID=99;
	
	private CheckList checkList;
	private Short accountTypeId;
	
	private Short accountStatusId;
	
	private Short checklistId;
	
	public Short getChecklistId() {
		return checklistId;
	}

	public void setChecklistId(Short checklistId) {
		this.checklistId = checklistId;
	}

	public AccountCheckList(){}

	/**
	 * This function returns the accountStatusId
	 * @return Returns the accountStatusId.
	 */
	
	public Short getAccountStatusId() {
		return accountStatusId;
	}
	/**
	 * This function sets the accountStatusId
	 * @param accountStatusId the accountStatusId to set.
	 */
	
	public void setAccountStatusId(Short accountStatusId) {
		this.accountStatusId = accountStatusId;
	}
	/**
	 * This function returns the accountTypeId
	 * @return Returns the accountTypeId.
	 */
	
	public Short getAccountTypeId() {
		return accountTypeId;
	}
	/**
	 * This function sets the accountTypeId
	 * @param accountTypeId the accountTypeId to set.
	 */
	
	public void setAccountTypeId(Short accountTypeId) {
		this.accountTypeId = accountTypeId;
	}
	/**
	 * This function returns the checkList
	 * @return Returns the checkList.
	 */
	
	public CheckList getCheckList() {
		return checkList;
	}
	/**
	 * This function sets the checkList
	 * @param checkList the checkList to set.
	 */
	
	public void setCheckList(CheckList checkList) {
		this.checkList = checkList;
	}

}
