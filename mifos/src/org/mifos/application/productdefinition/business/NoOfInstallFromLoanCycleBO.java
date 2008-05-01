/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.application.productdefinition.business;

import org.mifos.framework.business.PersistentObject;

/**
 * A loan product is a set of rules (interest rate, number of installments,
 * maximum amount, etc) which describes a particular kind of loan that
 * an MFI offers.
 * 
 * Although we may sometimes call these "offerings", it is probably better
 * to call them "loan products" (as that seems to be the terminology in
 * the functional spec and elsewhere).
 */
public class NoOfInstallFromLoanCycleBO extends PersistentObject {

	private Short minNoOfInstall;
	private Short maxNoOfInstall;
	private Short defaultNoOfInstall;
	// FIXME: what is this member for?
	private Short rangeIndex;
	private final LoanOfferingBO loanOffering;
	private final Short noOfInstallFromLoanCycleID;
	
	public NoOfInstallFromLoanCycleBO(Short minNoOfInstall, Short maxNoOfInstall, Short defaultNoOfInstall, Short rangeIndex,LoanOfferingBO loanOffering) {
		this.minNoOfInstall = minNoOfInstall;
		this.maxNoOfInstall = maxNoOfInstall;
		this.defaultNoOfInstall = defaultNoOfInstall;
		this.rangeIndex = rangeIndex;
		this.loanOffering = loanOffering;
		this.noOfInstallFromLoanCycleID = null;
	}
	public NoOfInstallFromLoanCycleBO(){
		this.minNoOfInstall = null;
		this.maxNoOfInstall = null;
		this.defaultNoOfInstall = null;
		this.loanOffering = null;
		this.noOfInstallFromLoanCycleID = null;
	}
	public Short getDefaultNoOfInstall() {
		return defaultNoOfInstall;
	}

	public void setDefaultNoOfInstall(Short defaultNoOfInstall) {
		this.defaultNoOfInstall = defaultNoOfInstall;
	}

	public Short getMaxNoOfInstall() {
		return maxNoOfInstall;
	}

	public void setMaxNoOfInstall(Short maxNoOfInstall) {
		this.maxNoOfInstall = maxNoOfInstall;
	}

	public Short getMinNoOfInstall() {
		return minNoOfInstall;
	}

	public void setMinNoOfInstall(Short minNoOfInstall) {
		this.minNoOfInstall = minNoOfInstall;
	}
	public Short getRangeIndex() {
		return rangeIndex;
	}
	public void setRangeIndex(Short rangeIndex) {
		this.rangeIndex = rangeIndex;
	}

}
