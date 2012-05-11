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

import java.io.Serializable;
import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class PaymentDto implements Serializable {

    private static final long serialVersionUID = -8181847082932670831L;

    private final Integer paymentId;
    private final Integer accountId;
    private final BigDecimal amount;
    private final LocalDate paymentDate;
    private final Short paymentTypeId;
    private final boolean savingsPayment;

    public PaymentDto(Integer paymentId, Integer accountId, BigDecimal amount, LocalDate paymentDate,
            Short paymentTypeId, boolean savingsPayment) {
        this.paymentId = paymentId;
        this.accountId = accountId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentTypeId = paymentTypeId;
        this.savingsPayment = savingsPayment;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public Short getPaymentTypeId() {
        return paymentTypeId;
    }

    public boolean isSavingsPayment() {
        return savingsPayment;
    }
}
