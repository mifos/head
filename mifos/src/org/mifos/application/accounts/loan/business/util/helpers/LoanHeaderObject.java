/**

 * LoanHeaderObject.java    version: xxx

 

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

package org.mifos.application.accounts.loan.business.util.helpers;

import java.io.Serializable;
import java.util.List;

import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.framework.business.util.helpers.HeaderObject;

/**
 * This class is used to build header for loan account pages.
 */
public class LoanHeaderObject implements HeaderObject,Serializable {

	/**
	 * 
	 */
	public LoanHeaderObject() {
		super();
		
	}
	
	private List<CustomerMaster> customerMasterList;
	
	private String officeName;
	
	private Short officeId;
	
	//as of now, loanAccountName is not being used anywhere
	private String loanAccountName;
	
	private Integer loanAccountId;

	/**
	 * @return Returns the customerMasterList}.
	 */
	public List<CustomerMaster> getCustomerMasterList() {
		return customerMasterList;
	}

	/**
	 * @param customerMasterList The customerMasterList to set.
	 */
	public void setCustomerMasterList(List<CustomerMaster> customerMasterList) {
		this.customerMasterList = customerMasterList;
	}

	/**
	 * @return Returns the loanAccountId}.
	 */
	public Integer getLoanAccountId() {
		return loanAccountId;
	}

	/**
	 * @param loanAccountId The loanAccountId to set.
	 */
	public void setLoanAccountId(Integer loanAccountId) {
		this.loanAccountId = loanAccountId;
	}

	/**
	 * @return Returns the loanAccountName}.
	 */
	public String getLoanAccountName() {
		return loanAccountName;
	}

	/**
	 * @param loanAccountName The loanAccountName to set.
	 */
	public void setLoanAccountName(String loanAccountName) {
		this.loanAccountName = loanAccountName;
	}

	/**
	 * @return Returns the officeId}.
	 */
	public Short getOfficeId() {
		return officeId;
	}

	/**
	 * @param officeId The officeId to set.
	 */
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}

	/**
	 * @return Returns the officeName}.
	 */
	public String getOfficeName() {
		return officeName;
	}

	/**
	 * @param officeName The officeName to set.
	 */
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

}
