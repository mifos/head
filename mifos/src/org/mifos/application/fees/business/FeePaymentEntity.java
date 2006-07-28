/**

 * FeePayment.java    version: xxx



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

package org.mifos.application.fees.business;

import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.master.business.MasterDataEntity;

public class FeePaymentEntity extends MasterDataEntity {

	protected FeePaymentEntity() {
	}

	public FeePaymentEntity(FeePayment feePayment){
		super(feePayment.getValue());
	}
	
	public boolean isTimeOfDisbursement() {
		return getId().equals(FeePayment.TIME_OF_DISBURSMENT.getValue());
	}
	
	public FeePayment getFeePayment() {
		FeePayment[] feePayments = FeePayment.values();
		for (FeePayment feePayment : feePayments) {
			if (String.valueOf(getId()).equals(feePayment.getValue()))
				return feePayment;
		}
		return null;
	}
}
