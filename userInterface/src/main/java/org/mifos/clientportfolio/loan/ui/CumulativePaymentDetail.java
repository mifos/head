/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.clientportfolio.loan.ui;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class CumulativePaymentDetail implements Serializable {

    private BigDecimal remainingPayment = BigDecimal.ZERO;
    private BigDecimal cumulativeFeesPaid = BigDecimal.ZERO;
    private BigDecimal cumulativeInterestPaid = BigDecimal.ZERO;
    private BigDecimal cumulativeTotalPaid = BigDecimal.ZERO;

    public CumulativePaymentDetail() {
        this.remainingPayment = BigDecimal.ZERO;
        this.cumulativeFeesPaid = BigDecimal.ZERO;
        this.cumulativeInterestPaid = BigDecimal.ZERO;
        this.cumulativeTotalPaid = BigDecimal.ZERO;
    }
    
    public CumulativePaymentDetail(BigDecimal remainingPayment, BigDecimal cumulativeTotalPaid, BigDecimal cumulativeInterestPaid,
            BigDecimal cumulativeFeesPaid) {
        this.remainingPayment = remainingPayment;
        this.cumulativeTotalPaid = cumulativeTotalPaid;
        this.cumulativeInterestPaid = cumulativeInterestPaid;
        this.cumulativeFeesPaid = cumulativeFeesPaid;
    }

    public BigDecimal getRemainingPayment() {
        return remainingPayment;
    }

    public void setRemainingPayment(BigDecimal remainingPayment) {
        this.remainingPayment = remainingPayment;
    }

    public BigDecimal getCumulativeFeesPaid() {
        return cumulativeFeesPaid;
    }

    public void setCumulativeFeesPaid(BigDecimal cumulativeFeesPaid) {
        this.cumulativeFeesPaid = cumulativeFeesPaid;
    }

    public BigDecimal getCumulativeInterestPaid() {
        return cumulativeInterestPaid;
    }

    public void setCumulativeInterestPaid(BigDecimal cumulativeInterestPaid) {
        this.cumulativeInterestPaid = cumulativeInterestPaid;
    }

    public BigDecimal getCumulativeTotalPaid() {
        return cumulativeTotalPaid;
    }

    public void setCumulativeTotalPaid(BigDecimal cumulativeTotalPaid) {
        this.cumulativeTotalPaid = cumulativeTotalPaid;
    }
}