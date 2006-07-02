/**

 * LoanOfferingFund.java    version: xxx

 

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

package org.mifos.application.productdefinition.business;

import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.framework.business.PersistentObject;

/**
 * This class acts as value object for LoanOfferingFund. This denotes the funds
 * for loan offering.
 * 
 * @author ashishsm
 * 
 */
public class LoanOfferingFundEntity extends PersistentObject {

	private Short loanOfferingFundId;

	private Fund fund;

	private LoanOfferingBO loanOffering;

	public LoanOfferingFundEntity() {
	}

	public Fund getFund() {
		return fund;
	}

	public void setFund(Fund fund) {
		this.fund = fund;
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}

	public void setLoanOffering(LoanOfferingBO loanOffering) {
		this.loanOffering = loanOffering;
	}

	public Short getLoanOfferingFundId() {
		return loanOfferingFundId;
	}

	public void setLoanOfferingFundId(Short loanOfferingFundId) {
		this.loanOfferingFundId = loanOfferingFundId;
	}

	public boolean equals(Object object) {
		LoanOfferingFundEntity loanOfferingFund = null;
		boolean value = false;
		if (object != null) {
			loanOfferingFund = (LoanOfferingFundEntity) object;
			if (loanOfferingFund.getLoanOfferingFundId().equals(
					this.loanOfferingFundId)) {
				value = true;
			}
		}
		return value;
	}

}
