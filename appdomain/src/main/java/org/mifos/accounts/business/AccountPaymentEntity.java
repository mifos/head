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

package org.mifos.accounts.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.PaymentDto;
import org.mifos.dto.screen.AccountPaymentDto;
import org.mifos.dto.screen.PaymentTypeDto;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;

/**
 * Records information about a payment from client to teller/field officer.
 */
public class AccountPaymentEntity extends AbstractEntity {

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
    private AccountPaymentEntity otherTransferPayment;

    private Set<AccountTrxnEntity> accountTrxns = new LinkedHashSet<AccountTrxnEntity>();

    public static AccountPaymentEntity savingsInterestPosting(SavingsBO account, Money amount, Date paymentDate, PersonnelBO loggedInUser) {

        String theReceiptNumber = null;
        Date theReceiptDate = null;
        PaymentTypeEntity paymentType = new PaymentTypeEntity(SavingsConstants.DEFAULT_PAYMENT_TYPE.shortValue());
        AccountPaymentEntity interestPostingPayment = new AccountPaymentEntity(account, amount, theReceiptNumber, theReceiptDate, paymentType, paymentDate);
        interestPostingPayment.setCreatedByUser(loggedInUser);
        return interestPostingPayment;
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
        this.otherTransferPayment = null;
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

    public AccountPaymentEntity getOtherTransferPayment() {
        return otherTransferPayment;
    }

    public void setOtherTransferPayment(AccountPaymentEntity otherTransferPayment) {
        this.otherTransferPayment = otherTransferPayment;
    }

    /**
     * Create reverse entries of all the transactions associated with this
     * payment and adds them to the set of transactions associated.
     */
    public List<AccountTrxnEntity> reversalAdjustment(final PersonnelBO personnel, final String adjustmentComment) throws AccountException {
        List<AccountTrxnEntity> newlyAddedTrxns = null;
        this.setAmount(Money.zero(amount.getCurrency()));

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
            AccountTrxnEntity reverseAccntTrxn = accntTrxn.generateReverseTrxn(personnel, adjustmentComment);
            reverseAccntTrxns.add(reverseAccntTrxn);
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

    public boolean isSavingsDepositOrWithdrawal() {
        return isSavingsDeposit() || isSavingsWithdrawal();
    }

    public boolean isSavingsDeposit() {
        boolean savingsDeposit = false;
        for (AccountTrxnEntity transaction : this.accountTrxns) {
            if (transaction.isSavingsDeposit()) {
                savingsDeposit = true;
            }
        }
        return savingsDeposit;
    }

    public boolean isSavingsWithdrawal() {
        boolean savingsWithdrawal = false;
        for (AccountTrxnEntity transaction : this.accountTrxns) {
            if (transaction.isSavingsWithdrawal()) {
                savingsWithdrawal = true;
            }
        }
        return savingsWithdrawal;
    }
    
    public boolean isSavingsInterestPosting() {
        boolean savingsInterestPosting = false;
        for (AccountTrxnEntity trxn : this.accountTrxns) {
            if (trxn.isSavingsInterestPosting()) {
                savingsInterestPosting = true;
                break;
            }
        }
        return savingsInterestPosting;
    }
    
    public boolean isLoanDisbursment() {
        boolean loanDisbursment = false;
        for (AccountTrxnEntity trxn : this.accountTrxns) {
            if (trxn.isLoanDisbursal()) {
                loanDisbursment = true;
                break;
            }
        }
        return loanDisbursment;
    }

    public PaymentDto getOtherTransferPaymentDto() {
        return (otherTransferPayment == null) ? null :
            new PaymentDto(otherTransferPayment.getPaymentId(), otherTransferPayment.getAccount().getAccountId(),
                    otherTransferPayment.getAmount().getAmount(), new LocalDate(otherTransferPayment.getPaymentDate()),
                    otherTransferPayment.getPaymentType().getId(), otherTransferPayment.isSavingsDepositOrWithdrawal());
    }

    public PaymentDto toDto() {
        return new PaymentDto(paymentId, account.getAccountId(), amount.getAmount(), new LocalDate(paymentDate),
                paymentType.getId(), isSavingsDepositOrWithdrawal());
    }

    public AccountPaymentDto toScreenDto() {
        PaymentTypeDto paymentTypeDto = new PaymentTypeDto(paymentType.getId(), paymentType.getName());
        return new AccountPaymentDto(paymentId, new AccountReferenceDto(account.getAccountId()), paymentTypeDto,
                amount.getAmount(), new LocalDate(paymentDate), receiptNumber, new LocalDate(receiptDate));
    }
}