/*
 * Copyright (c) 2005-2009 Grameen Foundation import java.math.BigDecimal;
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
public class CollectionSheetCustomerAccountCollectionDto {
    
    private Integer customerId;
    private Integer accountId;
    private Short currencyId;
    private BigDecimal miscFeesDue = BigDecimal.ZERO;
    private BigDecimal miscFeesPaid = BigDecimal.ZERO;
    private BigDecimal miscPenaltyDue = BigDecimal.ZERO;
    private BigDecimal miscPenaltyPaid = BigDecimal.ZERO;
    
    // fees
    private BigDecimal feeAmountDue = BigDecimal.ZERO;
    private BigDecimal feeAmountPaid = BigDecimal.ZERO;
    
    public CollectionSheetCustomerAccountCollectionDto() {
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

    public BigDecimal getMiscFeesDue() {
        return this.miscFeesDue;
    }

    public void setMiscFeesDue(final BigDecimal miscFeesDue) {
        this.miscFeesDue = miscFeesDue;
    }

    public BigDecimal getMiscFeesPaid() {
        return this.miscFeesPaid;
    }

    public void setMiscFeesPaid(final BigDecimal miscFeesPaid) {
        this.miscFeesPaid = miscFeesPaid;
    }

    public BigDecimal getMiscPenaltyDue() {
        return this.miscPenaltyDue;
    }

    public void setMiscPenaltyDue(final BigDecimal miscPenaltyDue) {
        this.miscPenaltyDue = miscPenaltyDue;
    }

    public BigDecimal getMiscPenaltyPaid() {
        return this.miscPenaltyPaid;
    }

    public void setMiscPenaltyPaid(final BigDecimal miscPenaltyPaid) {
        this.miscPenaltyPaid = miscPenaltyPaid;
    }

    public Double getAccountCollectionPayment() {
        return miscFeesDue.doubleValue() + miscPenaltyDue.doubleValue()
                - (miscFeesPaid.doubleValue() + miscPenaltyPaid.doubleValue());
    }

    public BigDecimal getFeeAmountDue() {
        return this.feeAmountDue;
    }

    public void setFeeAmountDue(final BigDecimal feeAmountDue) {
        
        if (feeAmountDue != null) {
            this.feeAmountDue = feeAmountDue;
        }
    }

    public BigDecimal getFeeAmountPaid() {
        return this.feeAmountPaid;
    }

    public void setFeeAmountPaid(final BigDecimal feeAmountPaid) {
        
        if (feeAmountPaid != null) {
            this.feeAmountPaid = feeAmountPaid;
        }
    }
    
    public Double getTotalFeeAmountDue() {
        return feeAmountDue.subtract(feeAmountPaid).doubleValue();
    }
}
