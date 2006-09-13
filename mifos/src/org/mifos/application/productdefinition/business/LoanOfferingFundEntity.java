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

public class LoanOfferingFundEntity extends PersistentObject {

	private final Short loanOfferingFundId;

	private final Fund fund;

	private final LoanOfferingBO loanOffering;

	protected LoanOfferingFundEntity() {
		this.loanOfferingFundId = null;
		this.fund = null;
		this.loanOffering = null;
	}

	protected LoanOfferingFundEntity(Fund fund, LoanOfferingBO loanOffering) {
		this.loanOfferingFundId = null;
		this.fund = fund;
		this.loanOffering = loanOffering;
	}

	public Fund getFund() {
		return fund;
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}

	public Short getLoanOfferingFundId() {
		return loanOfferingFundId;
	}

	@Override
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
