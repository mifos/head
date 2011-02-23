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

package org.mifos.clientportfolio.newloan.domain;

import java.util.Date;

import org.joda.time.LocalDate;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.framework.util.helpers.Money;

public class LoanScheduleRepaymentItemImpl implements LoanScheduleRepaymentItem {

    private final Integer installmentNumber;
    private final Date dueOnDate;
    private final Money principal;
    private final Money interest;
    private Money principalPaid;
    private Money interestPaid;
    private PaymentStatus paymentStatus;
    private Date paymentMadeOnDate;

    public LoanScheduleRepaymentItemImpl(Integer installmentNumber, LocalDate dueOnDate, Money principal, Money interest) {
        this.installmentNumber = installmentNumber;
        this.dueOnDate = dueOnDate.toDateMidnight().toDate();
        this.principal = principal;
        this.interest = interest;
        this.principalPaid = Money.zero(principal.getCurrency());
        this.interestPaid = Money.zero(principal.getCurrency());
        this.paymentStatus = PaymentStatus.UNPAID;
        this.paymentMadeOnDate = null;
    }

    public Money getPrincipal() {
        return this.principal;
    }

    public Money getInterest() {
        return this.interest;
    }

    public Money getPrincipalPaid() {
        return this.principalPaid;
    }

    public Money getInterestPaid() {
        return this.interestPaid;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getPaymentMadeOnDate() {
        return new LocalDate(this.paymentMadeOnDate);
    }

    public void setPaymentMadeOnDate(LocalDate paymentMadeOnDate) {
        this.paymentMadeOnDate = paymentMadeOnDate.toDateMidnight().toDate();
    }

    public Integer getInstallmentNumber() {
        return this.installmentNumber;
    }

    public LocalDate getDueOnDate() {
        return new LocalDate(this.dueOnDate);
    }

    public void setPrincipalPaid(Money principalPaid) {
        this.principalPaid = principalPaid;
    }

    public void setInterestPaid(Money interestPaid) {
        this.interestPaid = interestPaid;
    }
}