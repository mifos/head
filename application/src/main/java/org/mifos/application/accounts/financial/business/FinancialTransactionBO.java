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

package org.mifos.application.accounts.financial.business;

import java.util.Date;

import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;

public class FinancialTransactionBO extends BusinessObject {

    private final Integer trxnId;

    private final AccountTrxnEntity accountTrxn;

    private final FinancialTransactionBO relatedFinancialTrxn;

    private final FinancialActionBO financialAction;

    private final GLCodeEntity glcode;

    private final Date actionDate;

    private final Date postedDate;

    private final PersonnelBO postedBy;

    private final Short accountingUpdated;

    private final Money postedAmount;

    private final Money balanceAmount;

    private final String notes;

    private final Short debitCreditFlag;

    protected FinancialTransactionBO() {
        this.trxnId = null;
        this.accountTrxn = null;
        this.relatedFinancialTrxn = null;
        this.financialAction = null;
        this.glcode = null;
        this.actionDate = null;
        this.postedDate = null;
        this.postedBy = null;
        this.accountingUpdated = null;
        this.postedAmount = null;
        this.balanceAmount = null;
        this.notes = null;
        this.debitCreditFlag = null;
    }

    public FinancialTransactionBO(AccountTrxnEntity accountTrxn, FinancialTransactionBO relatedFinancialTrxn,
    FinancialActionBO financialAction, GLCodeEntity glcode, Date actionDate, PersonnelBO postedBy,
    Short accountingUpdated, Money postedAmount, String notes, Short debitCreditFlag) {
        this(accountTrxn, relatedFinancialTrxn, financialAction, glcode, actionDate, postedBy,
                accountingUpdated, postedAmount, notes, debitCreditFlag, new DateTimeService().getCurrentJavaDateTime());
    }

    public FinancialTransactionBO(AccountTrxnEntity accountTrxn, FinancialTransactionBO relatedFinancialTrxn,
            FinancialActionBO financialAction, GLCodeEntity glcode, Date actionDate, PersonnelBO postedBy,
            Short accountingUpdated, Money postedAmount, String notes, Short debitCreditFlag, Date postedDate) {
        this.trxnId = null;
        this.accountTrxn = accountTrxn;
        this.relatedFinancialTrxn = relatedFinancialTrxn;
        this.financialAction = financialAction;
        this.glcode = glcode;
        this.actionDate = actionDate;
        this.postedDate = postedDate;
        this.postedBy = postedBy;
        this.accountingUpdated = accountingUpdated;
        this.postedAmount = postedAmount;
        this.balanceAmount = postedAmount;
        this.notes = notes;
        this.debitCreditFlag = debitCreditFlag;
    }

    public boolean isDebitEntry() {
        return this.debitCreditFlag.equals(FinancialConstants.DEBIT.getValue());
    }

    public boolean isCreditEntry() {
        return this.debitCreditFlag.equals(FinancialConstants.CREDIT.getValue());
    }

    public Short getDebitCreditFlag() {
        return debitCreditFlag;
    }

    public String getNotes() {
        return notes;
    }

    public Short getAccountingUpdated() {
        return accountingUpdated;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public FinancialActionBO getFinancialAction() {
        return financialAction;
    }

    public GLCodeEntity getGlcode() {
        return glcode;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public FinancialTransactionBO getRelatedFinancialTrxn() {
        return relatedFinancialTrxn;
    }

    public Money getBalanceAmount() {
        return balanceAmount;
    }

    public Money getPostedAmount() {
        return postedAmount;
    }

    public Integer getTrxnId() {
        return trxnId;
    }

    public AccountTrxnEntity getAccountTrxn() {
        return accountTrxn;
    }

    public PersonnelBO getPostedBy() {
        return postedBy;
    }

    @Override
    public String toString() {
        return "{" + trxnId + ", " + actionDate + ", " + postedDate + ", " + postedAmount + ", " + balanceAmount + ", "
                + postedBy + ", " + glcode + ", " + debitCreditFlag + "}";

    }
}
