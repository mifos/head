/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.dto.domain;

import java.util.Locale;

import org.joda.time.LocalDate;

public class SavingsWithdrawalDto {

    private final Long savingsId;
    private final Long customerId;
    private final LocalDate dateOfWithdrawal;
    private final Double amount;
    private final Integer modeOfPayment;
    private final String receiptId;
    private final LocalDate dateOfReceipt;
    private final Locale preferredLocale;

    public SavingsWithdrawalDto(Long savingsId, Long customerId, LocalDate dateOfWithdrawal, Double amount, Integer modeOfPayment,
            String receiptId, LocalDate dateOfReceipt, Locale preferredLocale) {
        this.savingsId = savingsId;
        this.customerId = customerId;
        this.dateOfWithdrawal = dateOfWithdrawal;
        this.amount = amount;
        this.modeOfPayment = modeOfPayment;
        this.receiptId = receiptId;
        this.dateOfReceipt = dateOfReceipt;
        this.preferredLocale = preferredLocale;
    }

    public Long getSavingsId() {
        return this.savingsId;
    }

    public Double getAmount() {
        return this.amount;
    }

    public Integer getModeOfPayment() {
        return this.modeOfPayment;
    }

    public String getReceiptId() {
        return this.receiptId;
    }

    public LocalDate getDateOfReceipt() {
        return this.dateOfReceipt;
    }

    public Locale getPreferredLocale() {
        return this.preferredLocale;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public LocalDate getDateOfWithdrawal() {
        return this.dateOfWithdrawal;
    }
}