/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static org.mifos.accounts.loan.schedule.utils.Utilities.isGreaterThanZero;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class InstallmentPayment {
    private Date paidDate;
    private Map<InstallmentComponent, BigDecimal> paymentMap;

    public InstallmentPayment() {
        paymentMap = new LinkedHashMap<InstallmentComponent, BigDecimal>();
        resetPaymentComponents();
    }

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public BigDecimal getPrincipalPaid() {
        return paymentMap.get(InstallmentComponent.PRINCIPAL);
    }

    public void setPrincipalPaid(BigDecimal principalPaid) {
        paymentMap.put(InstallmentComponent.PRINCIPAL, principalPaid);
    }

    public BigDecimal getInterestPaid() {
        return paymentMap.get(InstallmentComponent.INTEREST);
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        paymentMap.put(InstallmentComponent.INTEREST, interestPaid);
    }

    public BigDecimal getExtraInterestPaid() {
        return paymentMap.get(InstallmentComponent.EXTRA_INTEREST);
    }

    public void setExtraInterestPaid(BigDecimal extraInterestPaid) {
        paymentMap.put(InstallmentComponent.EXTRA_INTEREST, extraInterestPaid);
    }

    public BigDecimal getPenaltyPaid() {
        return paymentMap.get(InstallmentComponent.PENALTY);
    }

    public void setPenaltyPaid(BigDecimal penaltyPaid) {
        paymentMap.put(InstallmentComponent.PENALTY, penaltyPaid);
    }

    public void setMiscPenaltyPaid(BigDecimal miscPenaltyPaid) {
        paymentMap.put(InstallmentComponent.MISC_PENALTY, miscPenaltyPaid);
    }

    public BigDecimal getMiscPenaltyPaid() {
        return paymentMap.get(InstallmentComponent.MISC_PENALTY);
    }

    public BigDecimal getMiscFeesPaid() {
        return paymentMap.get(InstallmentComponent.MISC_FEES);
    }

    public void setMiscFeesPaid(BigDecimal miscFeesPaid) {
        paymentMap.put(InstallmentComponent.MISC_FEES, miscFeesPaid);
    }

    public BigDecimal getFeesPaid() {
        return paymentMap.get(InstallmentComponent.FEES);
    }

    public void setFeesPaid(BigDecimal feesPaid) {
        paymentMap.put(InstallmentComponent.FEES, feesPaid);
    }

    public boolean isPartialPayment() {
        return isGreaterThanZero(getFeesPaid()) || isGreaterThanZero(getInterestPaid()) || isPrincipalPayment();
    }

    public boolean isPrincipalPayment() {
        return isGreaterThanZero(getPrincipalPaid());
    }

    private void resetPaymentComponents() {
        paymentMap.put(InstallmentComponent.PRINCIPAL, BigDecimal.ZERO);
        paymentMap.put(InstallmentComponent.INTEREST, BigDecimal.ZERO);
        paymentMap.put(InstallmentComponent.EXTRA_INTEREST, BigDecimal.ZERO);
        paymentMap.put(InstallmentComponent.FEES, BigDecimal.ZERO);
        paymentMap.put(InstallmentComponent.MISC_FEES, BigDecimal.ZERO);
        paymentMap.put(InstallmentComponent.PENALTY, BigDecimal.ZERO);
        paymentMap.put(InstallmentComponent.MISC_PENALTY, BigDecimal.ZERO);
    }

    /**
        * @deprecated Use the corresponding setters for allotting payment components
        */
    @Deprecated
    void setAmount(InstallmentComponent component, BigDecimal amount) {
        paymentMap.put(component, amount);
    }

}
