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

package org.mifos.accounts.loan.util.helpers;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class RepaymentScheduleInstallment implements Serializable {

    private Integer installment;

    private Date dueDateValue;

    private Money principal;

    private Money interest;

    private Money fees;

    private Money miscFees;

    private Money miscPenalty;

    private Locale locale;

    private Money totalValue;

    private String total;

    private String dueDate;

    public static RepaymentScheduleInstallment createForScheduleCopy(Integer installmentNumber, String principal, String interest, LocalDate dueDate, Locale locale, MifosCurrency currency) {
        Money feess = null;
        Money miscFeess = null;
        Money miscPenaltys = null;
        return new RepaymentScheduleInstallment(installmentNumber, new java.sql.Date(dueDate.toDateMidnight().toDate().getTime()), new Money(currency, principal), new Money(currency, interest), feess, miscFeess, miscPenaltys, locale);
    }

    public RepaymentScheduleInstallment(int installment, Date dueDateValue, Money principal, Money interest, Money fees,
                                        Money miscFees, Money miscPenalty, Locale locale) {
        this.installment = installment;
        this.principal = principal;
        this.interest = interest;
        this.fees = fees;
        this.miscFees = miscFees;
        this.miscPenalty = miscPenalty;
        setTotalAndTotalValue(this.principal.add(this.interest).add(this.fees).add(this.miscFees).add(this.miscPenalty));
        this.locale = locale;
        this.dueDateValue = dueDateValue;
        this.dueDate = DateUtils.formatDate(dueDateValue);
    }

    @Deprecated
    public RepaymentScheduleInstallment(Locale locale) {
        this.locale = locale;
    }

    public void setInstallment(Integer installment) {
        this.installment = installment;
    }

    public void setDueDateValue(Date dueDateValue) {
        this.dueDateValue = dueDateValue;
    }

    public void setPrincipal(Money principal) {
        this.principal = principal;
    }

    public void setInterest(Money interest) {
        this.interest = interest;
    }

    public void setFees(Money fees) {
        if (this.fees == null) {
            this.fees = fees;
        } else {
            this.fees = this.fees.add(fees);
        }
    }

    public Integer getInstallment() {
        return installment;
    }

    public Date getDueDateValue() {
        return dueDateValue;
    }

    public Money getPrincipal() {
        return principal;
    }

    public Money getInterest() {
        return interest;
    }

    public Money getFees() {
        return fees;
    }

    public Money getTotalValue() {
        return totalValue;
    }

    public Locale getLocale() {
        return locale;
    }

    public Money getMiscFees() {
        return miscFees;
    }

    public void setMiscFees(Money miscFees) {
        this.miscFees = miscFees;
    }

    public Money getMiscPenalty() {
        return miscPenalty;
    }

    public void setMiscPenalty(Money miscPenalty) {
        this.miscPenalty = miscPenalty;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = new Money(getCurrency(), totalValue);
    }

    public MifosCurrency getCurrency() {
        return principal.getCurrency();
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getInstallmentNumberAsString() {
        return String.valueOf(installment);
    }

    public Calendar getDueDateValueAsCalendar() {
        if (dueDateValue == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dueDateValue);
        return calendar;
    }

    public void setTotalAndTotalValue(Money total) {
        this.totalValue = total;
        this.total = total.toString();
    }

    public void setPrincipalAndInterest(Money interest, Money principal) {
        setPrincipal(principal);
        setInterest(interest);
    }

    public boolean isTotalAmountInValid() {
        if (totalValue == null) {
            return true;
        }
        if (interest != null && fees != null) {
            Money minPayable = interest.add(fees);
            return totalValue.compareTo(minPayable) < 0;
        }
        return totalValue.isLessThanOrEqualZero();
    }

    public boolean isTotalAmountLessThan(Money minInstallmentAmount) {
        return minInstallmentAmount != null && (totalValue == null || totalValue.isLessThan(minInstallmentAmount));
    }

    public boolean isTotalAmountLessThan(BigDecimal minInstallmentAmount) {
        return minInstallmentAmount != null && (totalValue == null || totalValue.getAmount().doubleValue() < minInstallmentAmount.doubleValue());
    }

    public Money getFeesWithMiscFee() {
        return getFees().add(getMiscFees());
    }
}