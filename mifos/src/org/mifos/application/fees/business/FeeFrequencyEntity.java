/**

 * FeeFrequency.java    version: xxx



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

package org.mifos.application.fees.business;

import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.PersistentObject;

public class FeeFrequencyEntity extends PersistentObject {

	private Short feeFrequencyId;

	private FeeFrequencyTypeEntity feeFrequencyType;

	private FeePaymentEntity feePayment;

	private FeesBO fee;

	private MeetingBO feeMeetingFrequency;

	public FeeFrequencyEntity() {
		feeFrequencyType = new FeeFrequencyTypeEntity();
		feePayment = new FeePaymentEntity();
		feeMeetingFrequency = new MeetingBO();
	}

	public void setFee(FeesBO fee) {
		this.fee = fee;
	}

	public FeesBO getFee() {
		return fee;
	}

	public void setFeeMeetingFrequency(MeetingBO feeMeetingFrequency) {
		this.feeMeetingFrequency = feeMeetingFrequency;
	}

	public MeetingBO getFeeMeetingFrequency() {
		return feeMeetingFrequency;
	}

	public void setFeeFrequencyId(Short feeFrequencyId) {
		this.feeFrequencyId = feeFrequencyId;
	}

	public Short getFeeFrequencyId() {
		return feeFrequencyId;
	}

	public FeeFrequencyTypeEntity getFeeFrequencyType() {
		return feeFrequencyType;
	}

	public void setFeeFrequencyType(FeeFrequencyTypeEntity feeFrequencyType) {
		this.feeFrequencyType = feeFrequencyType;
	}

	public FeePaymentEntity getFeePayment() {
		return feePayment;
	}

	public void setFeePayment(FeePaymentEntity feePayment) {
		this.feePayment = feePayment;
	}

	public void buildFeeFrequency() {
		if (isPeriodic()) {
			setFeePayment(null);
			feeMeetingFrequency.getMeetingType().setMeetingTypeId(
					FeesConstants.FEEMEETING);
			feeMeetingFrequency.setMeetingPlace("");
		} else
			setFeeMeetingFrequency(null);
	}

	public boolean isPeriodic() {
		if (feeFrequencyType.getFeeFrequencyTypeId().equals(
				FeesConstants.PERIODIC))
			return true;
		return false;
	}

}
