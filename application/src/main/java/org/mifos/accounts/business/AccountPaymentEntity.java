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

package org.mifos.accounts.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Records information about a payment from client to teller/field officer.
 */
public class AccountPaymentEntity extends AbstractEntity {

    private static final Logger logger = LoggerFactory.getLogger(AccountPaymentEntity.class);

    private final Integer paymentId = null;
    private AccountBO account;
    private final PaymentTypeEntity paymentType;
    private final String receiptNumber;
    private final String voucherNumber;
    private final String checkNumber;
    private final Date receiptDate;
    private final String bankName;
    private final Date paymentDate;
    private Money amount;
    private PersonnelBO createdByUser;
    private String comment;

    private Set<AccountTrxnEntity> accountTrxns = new LinkedHashSet<AccountTrxnEntity>();

    public static AccountPaymentEntity savingsInterestPosting(SavingsBO account, Money amount, Date paymentDate) {

        String theReceiptNumber = null;
        Date theReceiptDate = null;
        PaymentTypeEntity paymentType = new PaymentTypeEntity(SavingsConstants.DEFAULT_PAYMENT_TYPE.shortValue());
        return new AccountPaymentEntity(account, amount, theReceiptNumber, theReceiptDate, paymentType, paymentDate);
    }

    protected AccountPaymentEntity() {
        this(null, null, null, null, null, new DateTime().toDate());
    }

    public AccountPaymentEntity(final AccountBO account, final Money amount, final String receiptNumber, final Date receiptDate,
            final PaymentTypeEntity paymentType, final Date paymentDate) {
        this.paymentDate = paymentDate;
        this.account = account;
        this.receiptNumber = receiptNumber;
        this.paymentType = paymentType;
        this.receiptDate = receiptDate;
        this.amount = amount;
        this.bankName = null;
        this.voucherNumber = null;
        this.checkNumber = null;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public AccountBO getAccount() {
        return account;
    }

    public PaymentTypeEntity getPaymentType() {
        return paymentType;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public Set<AccountTrxnEntity> getAccountTrxns() {
        return accountTrxns;
    }

    public void setAccountTrxns(final Set<AccountTrxnEntity> accountTrxns) {
        this.accountTrxns = accountTrxns;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(final Money amount) {
        this.amount = amount;
    }

    public void addAccountTrxn(final AccountTrxnEntity accountTrxn) {
        accountTrxns.add(accountTrxn);
    }

    public PersonnelBO getCreatedByUser() {
        return this.createdByUser;
    }

    public void setCreatedByUser(final PersonnelBO createdByUser) {
        this.createdByUser = createdByUser;
    }

    /**
     * Create reverse entries of all the transactions associated with this
     * payment and adds them to the set of transactions associated.
     */
    public List<AccountTrxnEntity> reversalAdjustment(final PersonnelBO personnel, final String adjustmentComment) throws AccountException {
        List<AccountTrxnEntity> newlyAddedTrxns = null;
        this.setAmount(getAmount().subtract(getAmount()));

        if (null != getAccountTrxns() && getAccountTrxns().size() > 0) {
            newlyAddedTrxns = new ArrayList<AccountTrxnEntity>();

            Set<AccountTrxnEntity> reverseAccntTrxns = generateReverseAccountTransactions(personnel, adjustmentComment);

            for (AccountTrxnEntity reverseAccntTrxn : reverseAccntTrxns) {
                addAccountTrxn(reverseAccntTrxn);
            }

            newlyAddedTrxns.addAll(reverseAccntTrxns);
        }

        return newlyAddedTrxns;
    }

    private Set<AccountTrxnEntity> generateReverseAccountTransactions(final PersonnelBO personnel, final String adjustmentComment)
            throws AccountException {
        Set<AccountTrxnEntity> reverseAccntTrxns = new HashSet<AccountTrxnEntity>();
        for (AccountTrxnEntity accntTrxn : getAccountTrxns()) {
            logger.debug("Generating reverse transactions for transaction id " + accntTrxn.getAccountTrxnId());
            AccountTrxnEntity reverseAccntTrxn = accntTrxn.generateReverseTrxn(personnel, adjustmentComment);
            logger.debug("Amount associated with reverse transaction is "
                    + reverseAccntTrxn.getAmount());
            reverseAccntTrxns.add(reverseAccntTrxn);
            logger.debug("After succesfully adding the reverse transaction");
        }
        return reverseAccntTrxns;
    }

    @Override
    public String toString() {
        return "{" + paymentId + ", " + account + ", " + paymentType + ", " + amount + "}";
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setAccount(AccountBO account) {
        this.account = account;
    }
}