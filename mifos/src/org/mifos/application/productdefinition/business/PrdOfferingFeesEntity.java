/**

 * PrdOfferingFees.java    version: xxx

 

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

import org.mifos.application.fees.business.FeeBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

public class PrdOfferingFeesEntity extends PersistentObject {

	private final Short prdOfferingFeeId;

	private final LoanOfferingBO loanOffering;

	private final FeeBO fees;

	public PrdOfferingFeesEntity(LoanOfferingBO loanOffering, FeeBO fees) {
		this.prdOfferingFeeId = null;
		this.loanOffering = loanOffering;
		this.fees = fees;
	}

	protected PrdOfferingFeesEntity() {
		this.prdOfferingFeeId = null;
		this.loanOffering = null;
		this.fees = null;
	}

	public LoanOfferingBO getLoanOffering() {
		return loanOffering;
	}

	public FeeBO getFees() {
		return this.fees;
	}

	public Short getPrdOfferingFeeId() {
		return prdOfferingFeeId;
	}

	public boolean isFeePresent(Short feeId) {
		return fees.getFeeId().equals(feeId);
	}

	@Override
	public boolean equals(Object object) {
		PrdOfferingFeesEntity prdOfferingFees = null;
		boolean value = false;
		if (object != null) {
			prdOfferingFees = (PrdOfferingFeesEntity) object;
			if (prdOfferingFees.getPrdOfferingFeeId().equals(
					this.prdOfferingFeeId)) {
				value = true;
			}
		}
		MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER).info(
				"In Equals of loanOffering fund:" + value);
		return value;
	}
}
