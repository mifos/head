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

import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.security.util.UserContext;

public class RateFeeBO extends FeeBO {

    private Double rate;

    private final FeeFormulaEntity feeFormula;

    /**
     * Constructor to create one time Rate Fee. Fee Payment tells the time when
     * fee should be charged. (upfront/time of disbursement etc.)
     */
    public RateFeeBO(UserContext userContext, String feeName, CategoryTypeEntity categoryType,
            FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCodeEntity, Double rate,
            FeeFormulaEntity feeFormula, boolean isCustomerDefaultFee, FeePaymentEntity feePayment) throws FeeException {
        this(userContext, feeName, categoryType, feeFrequencyType, glCodeEntity, rate, feeFormula,
                isCustomerDefaultFee, feePayment, null);
    }

    /**
     * Constructor to create Periodic Rate Fee. Meeting tells the periodicity of
     * fee.
     */
    public RateFeeBO(UserContext userContext, String feeName, CategoryTypeEntity categoryType,
            FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCodeEntity, Double rate,
            FeeFormulaEntity feeFormula, boolean isCustomerDefaultFee, MeetingBO feeMeeting) throws FeeException {

        this(userContext, feeName, categoryType, feeFrequencyType, glCodeEntity, rate, feeFormula,
                isCustomerDefaultFee, null, feeMeeting);
    }

    /**
     * Addding a default constructor is hibernate's requiremnt and should not be
     * used to create a valid RateFee object.
     */
    protected RateFeeBO() {
        super();
        this.feeFormula = null;
    }

    private RateFeeBO(UserContext userContext, String feeName, CategoryTypeEntity categoryType,
            FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCodeEntity, Double rate,
            FeeFormulaEntity feeFormula, boolean isCustomerDefaultFee, FeePaymentEntity feePayment, MeetingBO feeMeeting)
            throws FeeException {
        super(userContext, feeName, categoryType, feeFrequencyType, glCodeEntity, isCustomerDefaultFee, feePayment,
                feeMeeting);
        validateFields(rate, feeFormula);
        this.feeFormula = feeFormula;
        this.rate = rate;
    }

    private void validateFields(Double rate, FeeFormulaEntity feeFormula) throws FeeException {
        if (rate == null || rate.doubleValue() <= 0.0 || feeFormula == null)
            throw new FeeException(FeeConstants.INVALID_FEE_RATE_OR_FORMULA);
    }

    public FeeFormulaEntity getFeeFormula() {
        return feeFormula;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Override
    public RateAmountFlag getFeeType() {
        return RateAmountFlag.RATE;
    }

    @Override
    public boolean doesFeeInvolveFractionalAmounts() {
        return true;
    }

    public FeeChangeType calculateNewFeeChangeType(Double otherRate, FeeStatusEntity otherStatus) {
        if (!rate.equals(otherRate)) {
            if (!getFeeStatus().getId().equals(otherStatus.getId())) {
                return FeeChangeType.AMOUNT_AND_STATUS_UPDATED;
            } else {
                return FeeChangeType.AMOUNT_UPDATED;
            }
        } else if (!getFeeStatus().getId().equals(otherStatus.getId())) {
            return FeeChangeType.STATUS_UPDATED;
        } else {
            return FeeChangeType.NOT_UPDATED;
        }
    }
}
