/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.fees.business;

import java.util.Date;

import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

public class AmountFeeBO extends FeeBO {

    private Money feeAmount;

    /**
     * Adding a default constructor is hibernate's requirement and should not be
     * used to create a valid AmountFee object.
     */
    protected AmountFeeBO() {
        super();
    }

    /**
     * TODO - keithw - work in progress
     * 
     * minimal legal constructor
     * 
     * @param office
     */
    public AmountFeeBO(final Money feeAmount, final String name, final FeeCategory category,
            final FeeFrequencyType feeFrequencyType, final GLCodeEntity feeGLCode, final MeetingBO meetingPeriodicity,
            final OfficeBO office, final Date createdDate, final Short createdByUserId) {
        super(name, category, feeFrequencyType, feeGLCode, meetingPeriodicity, office, createdDate, createdByUserId);
        this.feeAmount = feeAmount;
    }

    /**
     * Constructor to create one time Amount Fee. Fee Payment tells the time
     * when fee should be charged. (upfront/time of disbursement etc.)
     */
    public AmountFeeBO(final UserContext userContext, final String feeName, final CategoryTypeEntity categoryType,
            final FeeFrequencyTypeEntity feeFrequencyType, final GLCodeEntity glCodeEntity, final Money amount,
            final boolean isCustomerDefaultFee, final FeePaymentEntity feePayment) throws FeeException {
        this(userContext, feeName, categoryType, feeFrequencyType, glCodeEntity, amount, isCustomerDefaultFee,
                feePayment, null);
    }

    /**
     * Constructor to create Periodic Amount Fee. Meeting tells the periodicity
     * of fee.
     */
    public AmountFeeBO(final UserContext userContext, final String feeName, final CategoryTypeEntity categoryType,
            final FeeFrequencyTypeEntity feeFrequencyType, final GLCodeEntity glCodeEntity, final Money amount,
            final boolean isCustomerDefaultFee, final MeetingBO meeting) throws FeeException {
        this(userContext, feeName, categoryType, feeFrequencyType, glCodeEntity, amount, isCustomerDefaultFee, null,
                meeting);
    }

    private AmountFeeBO(final UserContext userContext, final String feeName, final CategoryTypeEntity categoryType,
            final FeeFrequencyTypeEntity feeFrequencyType, final GLCodeEntity glCodeEntity, final Money amount,
            final boolean isCustomerDefaultFee, final FeePaymentEntity feePayment, final MeetingBO meeting) throws FeeException {
        super(userContext, feeName, categoryType, feeFrequencyType, glCodeEntity, isCustomerDefaultFee, feePayment,
                meeting);
        validateFeeAmount(amount);
        this.feeAmount = amount;
    }

    public Money getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(final Money feeAmount) {
        this.feeAmount = feeAmount;
    }

    @Override
    public RateAmountFlag getFeeType() {
        return RateAmountFlag.AMOUNT;
    }

    @Override
    public boolean doesFeeInvolveFractionalAmounts() {
        return !MoneyUtils.isRoundedAmount(this.getFeeAmount());
    }

    private void validateFeeAmount(final Money amount) throws FeeException {
        if (amount == null || amount.isLessThanOrEqualZero()) {
            throw new FeeException(FeeConstants.INVALID_FEE_AMOUNT);
        }
    }

    public FeeChangeType calculateNewFeeChangeType(final Money otherAmount, final FeeStatusEntity otherStatus) {
        if (!feeAmount.equals(otherAmount)) {
            if (!getFeeStatus().getId().equals(otherStatus.getId())) {
                return FeeChangeType.AMOUNT_AND_STATUS_UPDATED;
            }
            return FeeChangeType.AMOUNT_UPDATED;

        } else if (!getFeeStatus().getId().equals(otherStatus.getId())) {
            return FeeChangeType.STATUS_UPDATED;
        } else {
            return FeeChangeType.NOT_UPDATED;
        }
    }

}
