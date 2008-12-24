/**

 * FeeFrequency.java    version: xxx



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

import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.PersistentObject;

public class FeeFrequencyEntity extends PersistentObject {

	private final Short feeFrequencyId;

	private final FeeFrequencyTypeEntity feeFrequencyType;

	private final FeePaymentEntity feePayment;

	private final FeeBO fee;

	private final MeetingBO feeMeetingFrequency;

	protected FeeFrequencyEntity() {
		this.feeFrequencyId = null;
		this.feeFrequencyType = null;
		this.feePayment = null;
		this.fee = null;
		this.feeMeetingFrequency = null;
	}

	protected FeeFrequencyEntity(FeeFrequencyTypeEntity feeFrequencyType, FeeBO fee, FeePaymentEntity feePayment, MeetingBO feeFrequency) throws FeeException{
		validateFields(feeFrequencyType, fee, feePayment, feeFrequency);
		this.feeFrequencyId = null;
		this.feeFrequencyType = feeFrequencyType;
		this.fee = fee;
		this.feePayment = feePayment;
		this.feeMeetingFrequency = feeFrequency;
	}

	public FeeFrequencyTypeEntity getFeeFrequencyType() {
		return feeFrequencyType;
	}

	public MeetingBO getFeeMeetingFrequency() {
		return feeMeetingFrequency;
	}

	public FeePaymentEntity getFeePayment() {
		return feePayment;
	}
	
	public boolean isPeriodic() {
		return getFeeFrequencyType().isPeriodic();
	}

	public boolean isOneTime() {
		return getFeeFrequencyType().isOneTime();
	}

	public boolean isTimeOfDisbursement() {
		return isOneTime() && getFeePayment().isTimeOfDisbursement();
	}
	
	private void validateFields(FeeFrequencyTypeEntity frequencyType, FeeBO fee, FeePaymentEntity feePayment,
			MeetingBO feeFrequency)throws FeeException{
		if(fee==null)
			throw new FeeException(FeeConstants.INVALID_FEE);
		if(frequencyType==null)
			throw new FeeException(FeeConstants.INVALID_FEE_FREQUENCY_TYPE);
		if(frequencyType.isOneTime() && feePayment==null)
			throw new FeeException(FeeConstants.INVALID_FEE_PAYEMENT_TYPE);
		if(frequencyType.isPeriodic() && feeFrequency==null)
			throw new FeeException(FeeConstants.INVALID_FEE_FREQUENCY);
	}
}
