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

package org.mifos.accounts.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;

public abstract class AccountTrxnEntity extends PersistentObject {

    private final Integer accountTrxnId = null;

    private final AccountBO account;

    private final AccountPaymentEntity accountPayment;

    private final PersonnelBO personnel;

    private final AccountActionEntity accountActionEntity;

    private final Money amount;

    private final Date dueDate;

    private final String comments;

    private final Date actionDate;

    private final CustomerBO customer;

    private final Timestamp trxnCreatedDate;

    private final Set<FinancialTransactionBO> financialTransactions;

    private final Short installmentId;

    private final AccountTrxnEntity relatedTrxn;

    protected AccountTrxnEntity() {
        trxnCreatedDate = new Timestamp(new DateTimeService().getCurrentJavaDateTime().getTime());
        financialTransactions = new HashSet<FinancialTransactionBO>();
        accountActionEntity = null;
        installmentId = null;
        dueDate = null;
        customer = null;
        personnel = null;
        actionDate = null;
        account = null;
        relatedTrxn = null;
        amount = null;
        accountPayment = null;
        comments = null;
    }

    public AccountTrxnEntity(final AccountPaymentEntity accountPayment, final AccountActionTypes accountActionType,
            final Short installmentId, final Date dueDate, final PersonnelBO personnel, final CustomerBO customer, final Date actionDate,
            final Money amount, final String comments, final AccountTrxnEntity relatedTrxn, final Persistence persistence) {
        this(accountPayment, accountActionType,
                installmentId, dueDate, personnel, customer, actionDate,
                amount, comments, relatedTrxn, persistence,
                new Timestamp(new DateTimeService().getCurrentDateTime().getMillis()));
    }

    public AccountTrxnEntity(final AccountPaymentEntity accountPayment, final AccountActionTypes accountActionType,
            final Short installmentId, final Date dueDate, final PersonnelBO personnel, final CustomerBO customer, final Date actionDate,
            final Money amount, final String comments, final AccountTrxnEntity relatedTrxn, final Persistence persistence,
            final Date transactionCreatedDate) {
        trxnCreatedDate = new Timestamp(transactionCreatedDate.getTime());
        financialTransactions = new HashSet<FinancialTransactionBO>();
        this.account = accountPayment.getAccount();
        try {
            this.accountActionEntity = (AccountActionEntity) persistence.getPersistentObject(AccountActionEntity.class,
                    accountActionType.getValue());
        } catch (PersistenceException e) {
            // this should not happen because we are passing an enumerated type that maps to the AccountActionEntity
            throw new MifosRuntimeException(e);
        }
        this.installmentId = installmentId;
        this.dueDate = dueDate;
        if (customer == null) {
            this.customer = account.getCustomer();
        } else {
            this.customer = customer;
        }
        this.personnel = personnel;
        this.actionDate = actionDate;
        this.amount = amount;
        this.relatedTrxn = relatedTrxn;
        this.accountPayment = accountPayment;
        this.comments = comments;
    }

    public AccountBO getAccount() {
        return account;
    }

    public AccountActionEntity getAccountActionEntity() {
        return accountActionEntity;
    }

    public AccountActionTypes getAccountAction() {
        return AccountActionTypes.fromInt(accountActionEntity.getId());
    }

    public AccountPaymentEntity getAccountPayment() {
        return accountPayment;
    }

    public Integer getAccountTrxnId() {
        return accountTrxnId;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public Money getAmount() {
        return amount;
    }

    public String getComments() {
        return comments;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public PersonnelBO getPersonnel() {
        return personnel;
    }

    public void addFinancialTransction(final FinancialTransactionBO financialTransaction) {
        this.financialTransactions.add(financialTransaction);
    }

    public Set<FinancialTransactionBO> getFinancialTransactions() {
        return financialTransactions;
    }

    public Short getInstallmentId() {
        return installmentId;
    }

    public AccountTrxnEntity getRelatedTrxn() {
        return relatedTrxn;
    }

    public Timestamp getTrxnCreatedDate() {
        return trxnCreatedDate;
    }

    protected abstract AccountTrxnEntity generateReverseTrxn(PersonnelBO personnel, String adjustmentComment)
            throws AccountException;

    @Override
    public String toString() {
        return "{" + accountTrxnId + ", " + account + ", " + accountActionEntity + ", " + amount + ", " + installmentId
                + "}";
    }
}
