/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.api.accounts;

import java.math.BigDecimal;
import org.joda.time.LocalDate;

public class AccountPaymentParametersDto {
    private final UserReferenceDto userMakingPayment;
    private final AccountReferenceDto account;
    private final BigDecimal paymentAmount;
    private final LocalDate paymentDate;
    private final LocalDate receiptDate;
    private final String receiptId;
    private final PaymentTypeDto paymentType;
    private final String comment;

    public AccountPaymentParametersDto(UserReferenceDto userMakingPayment, AccountReferenceDto account,
            BigDecimal paymentAmount, LocalDate paymentDate, PaymentTypeDto paymentType, String comment) {
        this.userMakingPayment = userMakingPayment;
        this.account = account;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
        this.receiptDate = null;
        this.receiptId = null;
        this.comment = comment;
    }

    public AccountPaymentParametersDto(UserReferenceDto userMakingPayment, AccountReferenceDto account,
            BigDecimal paymentAmount, LocalDate paymentDate, LocalDate receiptDate, String receiptId,
            PaymentTypeDto paymentType, String comment) {
        super();
        this.userMakingPayment = userMakingPayment;
        this.account = account;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.receiptDate = receiptDate;
        this.receiptId = receiptId;
        this.paymentType = paymentType;
        this.comment = comment;
    }

    public UserReferenceDto getUserMakingPayment() {
        return this.userMakingPayment;
    }

    public AccountReferenceDto getAccount() {
        return this.account;
    }

    public BigDecimal getPaymentAmount() {
        return this.paymentAmount;
    }

    public LocalDate getPaymentDate() {
        return this.paymentDate;
    }

    public LocalDate getReceiptDate() {
        return this.receiptDate;
    }

    public String getReceiptId() {
        return this.receiptId;
    }

    public PaymentTypeDto getPaymentType() {
        return this.paymentType;
    }

    public String getComment() {
        return this.comment;
    }

}
