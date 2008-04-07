/**

 * AmountFeeBO.java    version: xxx



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

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class AmountFeeBO extends FeeBO {

	private Money feeAmount;

	/**
	 * Constructor to create one time Amount Fee. Fee Payment tells the time
	 * when fee should be charged. (upfront/time of disbursment etc.)
	 */
	public AmountFeeBO(UserContext userContext, String feeName,
			CategoryTypeEntity categoryType,
			FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCodeEntity,
			Money amount, boolean isCustomerDefaultFee,
			FeePaymentEntity feePayment) throws FeeException {
		this(userContext, feeName, categoryType, feeFrequencyType,
				glCodeEntity, amount, isCustomerDefaultFee, feePayment, null);
	}

	/**
	 * Constructor to create Periodic Amount Fee. Meeting tells the periodicity
	 * of fee.
	 */
	public AmountFeeBO(UserContext userContext, String feeName,
			CategoryTypeEntity categoryType,
			FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCodeEntity,
			Money amount, boolean isCustomerDefaultFee, MeetingBO meeting)
			throws FeeException {
		this(userContext, feeName, categoryType, feeFrequencyType,
				glCodeEntity, amount, isCustomerDefaultFee, null, meeting);
	}

	/**
	 * Addding a default constructor is hibernate's requiremnt and should not be
	 * used to create a valid AmountFee object.
	 */
	protected AmountFeeBO() {
		super();
	}

	private AmountFeeBO(UserContext userContext, String feeName,
			CategoryTypeEntity categoryType,
			FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCodeEntity,
			Money amount, boolean isCustomerDefaultFee,
			FeePaymentEntity feePayment, MeetingBO meeting) throws FeeException {
		super(userContext, feeName, categoryType, feeFrequencyType,
				glCodeEntity, isCustomerDefaultFee, feePayment, meeting);
		validateFeeAmount(amount);
		this.feeAmount = amount;
	}

	public Money getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Money feeAmount) {
		this.feeAmount = feeAmount;
	}

	@Override
	public RateAmountFlag getFeeType() {
		return RateAmountFlag.AMOUNT;
	}

	private void validateFeeAmount(Money amount) throws FeeException {
		if (amount == null || amount.getAmountDoubleValue() <= 0.0)
			throw new FeeException(FeeConstants.INVALID_FEE_AMOUNT);
	}
}
