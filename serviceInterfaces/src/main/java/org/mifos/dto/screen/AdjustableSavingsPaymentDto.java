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
package org.mifos.dto.screen;

import java.io.Serializable;
import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class AdjustableSavingsPaymentDto implements Serializable {

    private static final long serialVersionUID = 6698204321144037222L;

    private final Integer paymentId;
    private final String receiptId;
    private final BigDecimal amount;
    private final LocalDate paymentDate;
    private final LocalDate receiptDate;
    private final String paymentType;
    private final boolean depositOrWithdrawal;

    public AdjustableSavingsPaymentDto(Integer paymentId, String receiptId, BigDecimal amount, LocalDate paymentDate,
            LocalDate receiptDate, String paymentType, boolean depositOrWithdrawal) {
        this.paymentId = paymentId;
        this.receiptId = receiptId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.receiptDate = receiptDate;
        this.paymentType = paymentType;
        this.depositOrWithdrawal = depositOrWithdrawal;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public LocalDate getReceiptDate() {
        return receiptDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public boolean isDeposit() {
        return depositOrWithdrawal;
    }

    public boolean isWithdrawal() {
        return !depositOrWithdrawal;
    }
}
