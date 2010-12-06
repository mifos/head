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

import org.apache.commons.lang.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstallmentPayments {
    private List<InstallmentPayment> installmentPayments;

    public InstallmentPayments() {
        installmentPayments = new ArrayList<InstallmentPayment>();
    }

    public BigDecimal getMiscPenaltyPaid() {
        BigDecimal miscPenaltyPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            miscPenaltyPaid = miscPenaltyPaid.add(installmentPayment.getMiscPenaltyPaid());
        }
        return miscPenaltyPaid;
    }

    public BigDecimal getPenaltyPaid() {
        BigDecimal penaltyPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            penaltyPaid = penaltyPaid.add(installmentPayment.getPenaltyPaid());
        }
        return penaltyPaid;
    }

    public BigDecimal getMiscFeesPaid() {
        BigDecimal miscFeesPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            miscFeesPaid = miscFeesPaid.add(installmentPayment.getMiscFeesPaid());
        }
        return miscFeesPaid;
    }

    public BigDecimal getFeesPaid() {
        BigDecimal feesPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            feesPaid = feesPaid.add(installmentPayment.getFeesPaid());
        }
        return feesPaid;
    }

    public BigDecimal getInterestPaid() {
        BigDecimal interestPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            interestPaid = interestPaid.add(installmentPayment.getInterestPaid());
        }
        return interestPaid;
    }

    public BigDecimal getPrincipalPaid() {
        BigDecimal principalPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            principalPaid = principalPaid.add(installmentPayment.getPrincipalPaid());
        }
        return principalPaid;
    }

    public BigDecimal getExtraInterestPaid() {
        BigDecimal extraInterestPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            extraInterestPaid = extraInterestPaid.add(installmentPayment.getExtraInterestPaid());
        }
        return extraInterestPaid;
    }

    public void addPayment(InstallmentPayment installmentPayment) {
        installmentPayments.add(installmentPayment);
    }

    public Date getRecentPartialPaymentDate() {
        Date lastPaymentDate = null;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            if (installmentPayment.isPartialPayment()) {
                lastPaymentDate = (Date) ObjectUtils.max(lastPaymentDate, installmentPayment.getPaidDate());
            }
        }
        return lastPaymentDate;
    }

    public Date getRecentPrincipalPaidDate() {
        Date lastPaymentDate = null;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            if (installmentPayment.isPrincipalPayment()) {
                lastPaymentDate = (Date) ObjectUtils.max(lastPaymentDate, installmentPayment.getPaidDate());
            }
        }
        return lastPaymentDate;
    }

    public InstallmentPayment getRecentPrincipalPayment() {
        InstallmentPayment result = null;
        Date lastPaymentDate = new Date(1);
        for (InstallmentPayment installmentPayment : installmentPayments) {
            if (installmentPayment.isPrincipalPayment()) {
                Date paidDate = installmentPayment.getPaidDate();
                if (paidDate.compareTo(lastPaymentDate) >= 0) {
                    lastPaymentDate = paidDate;
                    result = installmentPayment;
                }
            }
        }
        return result;
    }
}
