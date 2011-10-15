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

package org.mifos.accounts.loan.struts.uihelpers;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.util.Date;
import java.util.Locale;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class PaymentDataHtmlBean implements PaymentDataTemplate {
    private String amount;
    private String date;
    private Locale locale;
    private Short paymentTypeId;
    private PersonnelBO personnel;

    private RepaymentScheduleInstallment installment;

    public PaymentDataHtmlBean(Locale locale, PersonnelBO personnel, RepaymentScheduleInstallment installment) {
        this.locale = locale;
        this.personnel = personnel;
        this.paymentTypeId = PaymentTypes.CASH.getValue();

        long currentTime = new DateTimeService().getCurrentJavaDateTime().getTime();
        this.date = installment.getDueDate();
        if (installment.getDueDateValue().getTime() <= currentTime) {
            this.amount = installment.getTotalValue().toString();
        }

        this.installment = installment;
    }

    @Override
	public boolean hasValidAmount() {
        return isNotEmpty(this.amount);
    }

    @Override
	public Money getTotalAmount() {
        if (getAmount() == null || getAmount().equals("")) {
            return null;
        } else {
            return new Money(installment.getPrincipal().getCurrency(), amount);
        }
    }

    public Money paymentAmountAsMoney() {
        Money result = new Money(installment.getPrincipal().getCurrency());
        if (isNotEmpty(amount)) {
            try {
                result = new Money(installment.getPrincipal().getCurrency(), amount);
            } catch (NumberFormatException nfe) {
                return result;
            }
        }
        return result;

    }

    public Money totalAsMoney() {
        Money result = new Money(installment.getPrincipal().getCurrency());
        if (isNotEmpty(installment.getTotal())) {
            try {
                result = new Money(installment.getPrincipal().getCurrency(), installment.getTotal());
            } catch (NumberFormatException nfe) {
                return result;
            }
        }
        return result;
    }

    @Override
	public PersonnelBO getPersonnel() {
        return personnel;
    }

    @Override
	public Short getPaymentTypeId() {
        return this.paymentTypeId;
    }

    @Override
	public Date getTransactionDate() {
        String date = getDate();
        if (date != null && !date.equals("")) {
            return DateUtils.getDate(date, locale, DateUtils.getShortDateFormat(locale));
        }
        return null;
    }

    public RepaymentScheduleInstallment getInstallment() {
        return this.installment;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDueDate() {
        return this.installment.getDueDate();
    }

    public String getTotal() {
        return this.installment.getTotal();
    }

    public void setDueDate(String dueDate) {
        this.installment.setDueDate(dueDate);
    }

    public void setTotal(String total) {
        this.installment.setTotal(total);
    }

    public void setPaymentTypeId(Short paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public boolean hasTransactionDate() {
        return isNotEmpty(date);
    }

    public boolean hasTotalAmount() {
        return isNotEmpty(getTotal());
    }

    public String getInstallmentNumber() {
        return this.installment.getInstallmentNumberAsString();
    }

    public boolean hasNoTransactionDate() {
        return !hasTransactionDate();
    }
}
