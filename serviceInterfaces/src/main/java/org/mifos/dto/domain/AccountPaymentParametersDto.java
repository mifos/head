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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;

/**
 * The Class AccountPaymentParametersDto is a data transfer
 * object that holds the parameters necessary to make a
 * payment to a loan account.
 */
public class AccountPaymentParametersDto {

    public enum TransactionType {
        PAYMENT, LOAN_DISBURSAL;
    }

    public enum PaymentOptions {
        ALLOW_OVERPAYMENTS
    }

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
    private PaymentTypeDto paymentType;

    /** The comment associated with the payment. */
    private final String comment;

    /** Customer making the payment */
    private CustomerDto customer;

    /** Optional transaction type indicator */
    TransactionType transactionType;

    /** Optional payment options */
    private Set<PaymentOptions> paymentOptions;

    /** ids and amounts of payment for member accounts*/
    private Map<Integer, String> memberInfo;
    
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
        this(userMakingPayment, account, paymentAmount, paymentDate, paymentType, comment, null, null, null);
    }
    
    public AccountPaymentParametersDto(UserReferenceDto userMakingPayment, AccountReferenceDto account,
            BigDecimal paymentAmount, LocalDate paymentDate, PaymentTypeDto paymentType, String comment, String receiptId) {
        this(userMakingPayment, account, paymentAmount, paymentDate, paymentType, comment, null, receiptId, null);
    }


    /**
     * Instantiates a new account payment parameters dto.
     *
     * @param userMakingPayment the user making payment
     * @param account the account the payment is made to
     * @param paymentAmount the payment amount
     * @param paymentDate the payment date
     * @param paymentType the payment type
     * @param comment the comment associated with the payment
     * @param receiptDate the receipt date
     * @param receiptId the receipt id
     * @param customer a customer making payment
     */
    public AccountPaymentParametersDto(UserReferenceDto userMakingPayment, AccountReferenceDto account,
            BigDecimal paymentAmount, LocalDate paymentDate, PaymentTypeDto paymentType, String comment,
            LocalDate receiptDate, String receiptId, CustomerDto customer) {
        super();
        if (null == userMakingPayment) {
            throw new IllegalArgumentException("userMakingPayment cannot be null");
        }
        this.userMakingPayment = userMakingPayment;

        if (null == account) {
            throw new IllegalArgumentException("account cannot be null");
        }
        this.account = account;

        if (null == paymentAmount) {
            throw new IllegalArgumentException("paymentAmount cannot be null");
        }
        this.paymentAmount = paymentAmount;

        if (null == paymentDate) {
            throw new IllegalArgumentException("paymentDate cannot be null");
        }
        this.paymentDate = paymentDate;

        this.paymentType = paymentType;

        if (null == comment) {
            throw new IllegalArgumentException("comment cannot be null");
        }
        this.comment = comment;

        this.receiptDate = receiptDate;
        this.receiptId = receiptId;
        this.customer = customer;
    }
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public AccountPaymentParametersDto(UserReferenceDto userMakingPayment, AccountReferenceDto account,
            BigDecimal paymentAmount, LocalDate paymentDate, PaymentTypeDto paymentType, String comment,
            LocalDate receiptDate, String receiptId, CustomerDto customer, Map<Integer, String> memberInfo) {
        super();
        if (null == userMakingPayment) {
            throw new IllegalArgumentException("userMakingPayment cannot be null");
        }
        this.userMakingPayment = userMakingPayment;

        if (null == account) {
            throw new IllegalArgumentException("account cannot be null");
        }
        this.account = account;

        if (null == paymentAmount) {
            throw new IllegalArgumentException("paymentAmount cannot be null");
        }
        this.paymentAmount = paymentAmount;

        if (null == paymentDate) {
            throw new IllegalArgumentException("paymentDate cannot be null");
        }
        this.paymentDate = paymentDate;

        this.paymentType = paymentType;

        if (null == comment) {
            throw new IllegalArgumentException("comment cannot be null");
        }
        this.comment = comment;

        this.receiptDate = receiptDate;
        this.receiptId = receiptId;
        this.customer = customer;
        this.memberInfo = memberInfo;
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

    public int getAccountId() {
        return account.getAccountId();
    }

    public CustomerDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDto customer) {
        this.customer = customer;
    }

    public void setPaymentType(PaymentTypeDto paymentType) {
        this.paymentType = paymentType;
    }

    public TransactionType getTransactionType() {
        TransactionType result = null;
        if (transactionType == null) {
            result = TransactionType.PAYMENT;
        } else {
            result = transactionType;
        }
        return result;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Set<PaymentOptions> getPaymentOptions() {
        if (paymentOptions == null) {
            paymentOptions = new HashSet<PaymentOptions>();
        }
        return paymentOptions;
    }

    public void addPaymentOption(PaymentOptions option) {
        if (paymentOptions == null) {
            paymentOptions = new HashSet<PaymentOptions>();
        }
        paymentOptions.add(option);
    }
    
    public Map<Integer, String> getMemberInfo() {
        return memberInfo;
    }

    public void setMemberInfo(Map<Integer, String> memberInfo) {
        this.memberInfo = memberInfo;
    }
}
