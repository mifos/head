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
package org.mifos.application.servicefacade;

import java.math.BigDecimal;

/**
 *
 */
public class CollectionSheetLoanFeeDto {

    private Integer customerId;
    private Integer accountId;
    private Short currencyId;
    private BigDecimal feeAmountDue = BigDecimal.ZERO;
    private BigDecimal feeAmountPaid = BigDecimal.ZERO;

    public CollectionSheetLoanFeeDto() {
        // default constructor for hibernate
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(final Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getAccountId() {
        return this.accountId;
    }

    public void setAccountId(final Integer accountId) {
        this.accountId = accountId;
    }

    public Short getCurrencyId() {
        return this.currencyId;
    }

    public void setCurrencyId(final Short currencyId) {
        this.currencyId = currencyId;
    }

    public BigDecimal getFeeAmountDue() {
        return this.feeAmountDue;
    }

    public void setFeeAmountDue(final BigDecimal feeAmountDue) {
        this.feeAmountDue = feeAmountDue;
    }

    public BigDecimal getFeeAmountPaid() {
        return this.feeAmountPaid;
    }

    public void setFeeAmountPaid(final BigDecimal feeAmountPaid) {

        if (feeAmountPaid == null) {
            this.feeAmountPaid = BigDecimal.ZERO;
        } else {
            this.feeAmountPaid = feeAmountPaid;
        }
    }

    public Double getTotalFeeAmountDue() {
        return feeAmountDue.subtract(feeAmountPaid).doubleValue();
    }
}
