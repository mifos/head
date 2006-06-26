/**

 * EditLoanStatus.java    version: 1.0



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
package org.mifos.application.accounts.loan.util.valueobjects;

import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.valueobjects.AccountNotes;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.framework.util.valueobjects.ValueObject;

public class EditLoanStatus extends ValueObject{
	private Integer accountId;
	private String globalAccountNum;
	private String accountName;	
	private Short currentStatusId;
	private Integer versionNo;
	private Short newStatusId;
	private Short flagId;
	AccountNotes notes;
	
	/**
	 * This method returns the name by which a loan statsu object will be stored in the context object. Framework will set
	 * this object to request with this name. 
	 * @return The name by which the loan status object can be referred to
	 */
	public String getResultName(){
		return LoanConstants.LOAN_STATUS_VO;
	}
	public EditLoanStatus(){
		notes=new AccountNotes();
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public short getCurrentStatusId() {
		return currentStatusId;
	}
	public void setCurrentStatusId(short currentStatusId) {
		this.currentStatusId = currentStatusId;
	}
	public String getGlobalAccountNum() {
		return globalAccountNum;
	}
	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}
	public Short getFlagId() {
		return flagId;
	}
	public void setFlagId(Short flagId) {
		this.flagId = flagId;
	}
	public Short getNewStatusId() {
		return newStatusId;
	}
	public void setNewStatusId(Short newStatusId) {
		this.newStatusId = newStatusId;
	}
	public Integer getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	public void setCurrentStatusId(Short currentStatusId) {
		this.currentStatusId = currentStatusId;
	}
	public AccountNotes getNotes() {
		return notes;
	}
	public void setNotes(AccountNotes notes) {
		this.notes = notes;
	}
}
