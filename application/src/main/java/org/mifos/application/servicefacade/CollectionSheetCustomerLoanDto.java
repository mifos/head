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

import org.mifos.framework.util.helpers.Constants;

/**
 *
 */
public class CollectionSheetCustomerLoanDto {

    private Integer customerId;
    private Integer accountId;
    private String productShortName;
    private Short currencyId;
    private BigDecimal principalDue;
    private BigDecimal principalPaid;
    private BigDecimal interestDue;
    private BigDecimal interestPaid;
    private BigDecimal penaltyDue;
    private BigDecimal penaltyPaid;
    private BigDecimal miscFeesDue;
    private BigDecimal miscFeesPaid;
    private BigDecimal miscPenaltyDue;
    private BigDecimal miscPenaltyPaid;

    private Double totalAccountFees = Double.valueOf("0.0");
    
    // disbursement specific
    private BigDecimal disbursementAmount = BigDecimal.ZERO;
    private Short payInterestAtDisbursement = Constants.NO;
    private Double disbursementFeeAmount = Double.valueOf("0.0");
    
    public CollectionSheetCustomerLoanDto() {
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

    public String getProductShortName() {
        return this.productShortName;
    }

    public void setProductShortName(final String productShortName) {
        this.productShortName = productShortName;
    }
    
    public Short getCurrencyId() {
        return this.currencyId;
    }

    public void setCurrencyId(final Short currencyId) {
        this.currencyId = currencyId;
    }

    public void setPrincipalDue(final BigDecimal principalDue) {
        this.principalDue = principalDue;
    }

    public void setPrincipalPaid(final BigDecimal principalPaid) {
        this.principalPaid = principalPaid;
    }

    public void setInterestDue(final BigDecimal interestDue) {
        this.interestDue = interestDue;
    }

    public void setInterestPaid(final BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public void setMiscFeesDue(final BigDecimal miscFeesDue) {
        this.miscFeesDue = miscFeesDue;
    }

    public void setMiscFeesPaid(final BigDecimal miscFeesPaid) {
        this.miscFeesPaid = miscFeesPaid;
    }

    public void setMiscPenaltyDue(final BigDecimal miscPenaltyDue) {
        this.miscPenaltyDue = miscPenaltyDue;
    }

    public void setMiscPenaltyPaid(final BigDecimal miscPenaltyPaid) {
        this.miscPenaltyPaid = miscPenaltyPaid;
    }

    public void setPenaltyDue(final BigDecimal penaltyDue) {
        this.penaltyDue = penaltyDue;
    }

    public void setPenaltyPaid(final BigDecimal penaltyPaid) {
        this.penaltyPaid = penaltyPaid;
    }

    public void setTotalAccountFees(final Double totalAccountFees) {
        this.totalAccountFees = totalAccountFees;
    }

    public void setDisbursementAmount(final BigDecimal disbursementAmount) {
        this.disbursementAmount = disbursementAmount;
    }

    public Short getPayInterestAtDisbursement() {
        return this.payInterestAtDisbursement;
    }

    public void setPayInterestAtDisbursement(final Short payInterestAtDisbursement) {
        this.payInterestAtDisbursement = payInterestAtDisbursement;
    }

    public void setDisbursementFeeAmount(final Double disbursementFeeAmount) {
        this.disbursementFeeAmount = disbursementFeeAmount;
    }
    
    public Double getTotalRepaymentDue() {

        return principalDue.doubleValue()
                + interestDue.doubleValue()
                + penaltyDue.doubleValue()
                + miscFeesDue.doubleValue()
                + miscPenaltyDue.doubleValue()
                - (principalPaid.doubleValue() + interestPaid.doubleValue() + penaltyPaid.doubleValue()
                        + miscFeesPaid.doubleValue() + miscPenaltyPaid.doubleValue()) + this.totalAccountFees;
    }

    public Double getTotalDisbursement() {
        return disbursementAmount.doubleValue() + disbursementFeeAmount;
    }
}
