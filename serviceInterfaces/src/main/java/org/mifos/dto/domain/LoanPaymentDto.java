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

package org.mifos.dto.domain;

import org.joda.time.LocalDate;

public class LoanPaymentDto {

    private final Short paymentTypeId;
    private final LocalDate paymentDate;
    private final String amount;
    private final Short paidByUserId;

    public LoanPaymentDto(String amount, LocalDate transactionDate, Short paymentTypeId, Short paidByUserId) {
        this.amount = amount;
        this.paymentDate = transactionDate;
        this.paymentTypeId = paymentTypeId;
        this.paidByUserId = paidByUserId;
    }

    public Short getPaymentTypeId() {
        return this.paymentTypeId;
    }

    public LocalDate getPaymentDate() {
        return this.paymentDate;
    }

    public String getAmount() {
        return this.amount;
    }

    public Short getPaidByUserId() {
        return this.paidByUserId;
    }
}