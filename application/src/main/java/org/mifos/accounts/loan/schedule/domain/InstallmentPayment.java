/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.accounts.loan.schedule.domain;

import java.math.BigDecimal;
import java.util.Date;

import static org.mifos.accounts.loan.schedule.utils.Utilities.isGreaterThanZero;

public class InstallmentPayment {
    private Date paidDate;
    private BigDecimal principalPaid;
    private BigDecimal interestPaid;
    private BigDecimal feesPaid;
    private BigDecimal extraInterestPaid;

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public BigDecimal getPrincipalPaid() {
        return principalPaid == null ? BigDecimal.ZERO : principalPaid;
    }

    public void setPrincipalPaid(BigDecimal principalPaid) {
        this.principalPaid = principalPaid;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid == null ? BigDecimal.ZERO : interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public BigDecimal getFeesPaid() {
        return feesPaid == null ? BigDecimal.ZERO : feesPaid;
    }

    public void setFeesPaid(BigDecimal feesPaid) {
        this.feesPaid = feesPaid;
    }

    public BigDecimal getExtraInterestPaid() {
        return extraInterestPaid == null ? BigDecimal.ZERO : extraInterestPaid;
    }

    public void setExtraInterestPaid(BigDecimal extraInterestPaid) {
        this.extraInterestPaid = extraInterestPaid;
    }

    public boolean isPartialPayment() {
        return isGreaterThanZero(getFeesPaid()) || isGreaterThanZero(getInterestPaid()) || isPrincipalPayment();
    }

    public boolean isPrincipalPayment() {
        return isGreaterThanZero(getPrincipalPaid());
    }
}
