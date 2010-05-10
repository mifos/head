/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.fees.servicefacade;

import java.io.Serializable;

import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.application.meeting.util.helpers.RecurrenceType;

/**
 * @author angshus
 *
 */
public class FeeCreateRequest implements Serializable {

    private FeeCategory categoryType;
    private FeeFrequencyType feeFrequencyType;
    private Short glCode;
    private FeePayment feePaymentType;
    private FeeFormula feeFormula;
    private String feeName;
    private boolean rateFee;
    private boolean customerDefaultFee;
    private Short currencyId;
    private Double rate;
    private String amount;
    private RecurrenceType feeRecurrenceType;
    private Short monthRecurAfter;
    private Short weekRecurAfter;

    public FeeCreateRequest(
            FeeCategory feeCategory, FeeFrequencyType feeFrequencyType, Short glCode,
            FeePayment feePayment, FeeFormula feeFormula, String feeName,
            boolean isRateFee, boolean isCustomerDefaultFee, Double rate,
            Short currencyId, String amount, RecurrenceType feeRecurrenceType,
            Short monthRecurAfter, Short weekRecurAfter) {

                this.categoryType = feeCategory;
                this.feeFrequencyType = feeFrequencyType;
                this.glCode = glCode;
                this.feePaymentType = feePayment;
                this.feeFormula = feeFormula;
                this.feeName = feeName;
                this.rateFee = isRateFee;
                this.customerDefaultFee = isCustomerDefaultFee;
                this.rate = rate;
                this.currencyId = currencyId;
                this.amount = amount;
                this.feeRecurrenceType = feeRecurrenceType;
                this.monthRecurAfter = monthRecurAfter;
                this.weekRecurAfter = weekRecurAfter;

    }

    public FeeCategory getCategoryType() {
        return this.categoryType;
    }

    public FeeFrequencyType getFeeFrequencyType() {
        return this.feeFrequencyType;
    }

    public Short getGlCode() {
        return this.glCode;
    }

    public FeePayment getFeePaymentType() {
        return this.feePaymentType;
    }

    public FeeFormula getFeeFormula() {
        return this.feeFormula;
    }

    public String getFeeName() {
        return this.feeName;
    }

    public boolean isRateFee() {
        return this.rateFee;
    }

    public boolean isCustomerDefaultFee() {
        return this.customerDefaultFee;
    }

    public Short getCurrencyId() {
        return this.currencyId;
    }

    public Double getRate() {
        return this.rate;
    }

    public String getAmount() {
        return this.amount;
    }

    public RecurrenceType getFeeRecurrenceType() {
        return this.feeRecurrenceType;
    }

    public Short getMonthRecurAfter() {
        return this.monthRecurAfter;
    }

    public Short getWeekRecurAfter() {
        return this.weekRecurAfter;
    }
}
