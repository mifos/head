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

package org.mifos.accounts.api;

import java.math.BigDecimal;
import org.joda.time.LocalDate;

/**
 * The Class AccountPaymentParametersDto is a data transfer
 * object that holds the parameters necessary to make a 
 * payment to a loan account.
 */
public class AccountPaymentParametersDto {
    
    /** The user making the payment. */
    private final UserReferenceDto userMakingPayment;
    
    /** The account the payment is made to. */
    private final AccountReferenceDto account;
    
    /** The payment amount. */
    private final BigDecimal paymentAmount;
    
    /** The payment date. */
    private final LocalDate paymentDate;
    
    /** The receipt date. */
    private final LocalDate receiptDate;
    
    /** The receipt id. */
    private final String receiptId;
    
    /** The payment type. */
    private final PaymentTypeDto paymentType;
    
    /** The comment associated with the payment. */
    private final String comment;

    /**
     * Instantiates a new account payment parameters dto.
     * 
     * @param userMakingPayment the user making payment
     * @param account the account the payment is made to
     * @param paymentAmount the payment amount
     * @param paymentDate the payment date
     * @param paymentType the payment type
     * @param comment the comment associated with the payment
     */
    public AccountPaymentParametersDto(UserReferenceDto userMakingPayment, AccountReferenceDto account,
            BigDecimal paymentAmount, LocalDate paymentDate, PaymentTypeDto paymentType, String comment) {
        this(userMakingPayment, account, paymentAmount, paymentDate, null, null, paymentType, comment);
    }

    /**
     * Instantiates a new account payment parameters dto.
     * 
     * @param userMakingPayment the user making payment
     * @param account the account the payment is made to
     * @param paymentAmount the payment amount
     * @param paymentDate the payment date
     * @param receiptDate the receipt date
     * @param receiptId the receipt id
     * @param paymentType the payment type
     * @param comment the comment associated with the payment
     */
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

    /**
     * Gets the user making payment.
     * 
     * @return the user making payment
     */
    public UserReferenceDto getUserMakingPayment() {
        return this.userMakingPayment;
    }

    /**
     * Gets the account.
     * 
     * @return the account
     */
    public AccountReferenceDto getAccount() {
        return this.account;
    }

    /**
     * Gets the payment amount.
     * 
     * @return the payment amount
     */
    public BigDecimal getPaymentAmount() {
        return this.paymentAmount;
    }

    /**
     * Gets the payment date.
     * 
     * @return the payment date
     */
    public LocalDate getPaymentDate() {
        return this.paymentDate;
    }

    /**
     * Gets the receipt date.
     * 
     * @return the receipt date
     */
    public LocalDate getReceiptDate() {
        return this.receiptDate;
    }

    /**
     * Gets the receipt id.
     * 
     * @return the receipt id
     */
    public String getReceiptId() {
        return this.receiptId;
    }

    /**
     * Gets the payment type.
     * 
     * @return the payment type
     */
    public PaymentTypeDto getPaymentType() {
        return this.paymentType;
    }

    /**
     * Gets the comment.
     * 
     * @return the comment
     */
    public String getComment() {
        return this.comment;
    }

}
