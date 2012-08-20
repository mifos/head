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

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.AdminDocumentDto;

public class AccountPaymentDto {
    private final Integer paymentId;
    private final AccountReferenceDto account;
    private final PaymentTypeDto paymentType;
    private final BigDecimal amount;
    private final LocalDate paymentDate;
    private final String receiptId;
    private final LocalDate receiptDate;

    List<AdminDocumentDto> adminDocuments;

    public AccountPaymentDto(Integer paymentId, AccountReferenceDto account, PaymentTypeDto paymentType, BigDecimal amount,
            LocalDate paymentDate, String receiptId, LocalDate receiptDate, List<AdminDocumentDto> adminDocuments) {
        super();
        this.paymentId = paymentId;
        this.account = account;
        this.paymentType = paymentType;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.receiptId = receiptId;
        this.receiptDate = receiptDate;
        this.adminDocuments = adminDocuments;
    }

    public AccountPaymentDto(Integer paymentId, AccountReferenceDto account, PaymentTypeDto paymentType, BigDecimal amount,
            LocalDate paymentDate, String receiptId, LocalDate receiptDate) {
        super();
        this.paymentId = paymentId;
        this.account = account;
        this.paymentType = paymentType;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.receiptId = receiptId;
        this.receiptDate = receiptDate;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public AccountReferenceDto getAccount() {
        return account;
    }

    public PaymentTypeDto getPaymentType() {
        return paymentType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public LocalDate getReceiptDate() {
        return receiptDate;
    }

    public List<AdminDocumentDto> getAdminDocuments() {
        return adminDocuments;
    }

    public void setAdminDocuments(List<AdminDocumentDto> adminDocuments) {
        this.adminDocuments = adminDocuments;
    }

}
