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

package org.mifos.accounts.financial.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.FinancialActionBO;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.activity.BaseFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.CustomerAccountRepaymentFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.CustomerAdjustmentFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.DisbursalAmountReversalFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.FeeRepaymentFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.LoanAdjustmentFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.LoanDisbursementFinantialActivity;
import org.mifos.accounts.financial.business.service.activity.LoanRepaymentFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.RescheduleFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.SavingsAdjustmentFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.SavingsDepositFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.SavingsInterestPostingFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.SavingsWithdrawalFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.WriteOffFinancialActivity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

public class FinancialBusinessService implements BusinessService {
    
    /**
     * Added by Keith for testing
     */
    public COABO getGlAccount(String glcode) throws FinancialException {
        return ChartOfAccountsCache.get(glcode);
    }

    public FinancialActionBO getFinancialAction(final FinancialActionConstants financialActionId)
            throws FinancialException {
        return FinancialActionCache.getFinancialAction(financialActionId);
    }

    public FinancialBusinessService() {
        super();
    }

    @Override
    public BusinessObject getBusinessObject(UserContext userContext) {
        return null;
    }

    public void buildAccountingEntries(AccountTrxnEntity accounttrxn) throws FinancialException {
        BaseFinancialActivity baseFinancialActivity = null;
        if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.LOAN_REPAYMENT.getValue()) {
            baseFinancialActivity = new LoanRepaymentFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == (AccountActionTypes.SAVINGS_INTEREST_POSTING
                .getValue())) {
            baseFinancialActivity = new SavingsInterestPostingFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == (AccountActionTypes.SAVINGS_WITHDRAWAL
                .getValue())) {
            baseFinancialActivity = new SavingsWithdrawalFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.SAVINGS_DEPOSIT
                .getValue()) {
            baseFinancialActivity = new SavingsDepositFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.DISBURSAL.getValue()) {
            baseFinancialActivity = new LoanDisbursementFinantialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.FEE_REPAYMENT
                .getValue()) {
            baseFinancialActivity = new FeeRepaymentFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.CUSTOMER_ACCOUNT_REPAYMENT
                .getValue()) {
            baseFinancialActivity = new CustomerAccountRepaymentFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.SAVINGS_ADJUSTMENT
                .getValue()) {
            baseFinancialActivity = new SavingsAdjustmentFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.CUSTOMER_ADJUSTMENT
                .getValue()) {
            baseFinancialActivity = new CustomerAdjustmentFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.LOAN_ADJUSTMENT
                .getValue()
                || accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.LOAN_REVERSAL
                        .getValue()) {
            baseFinancialActivity = new LoanAdjustmentFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.WRITEOFF.getValue()) {
            baseFinancialActivity = new WriteOffFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.LOAN_RESCHEDULED
                .getValue()) {
            baseFinancialActivity = new RescheduleFinancialActivity(accounttrxn);
        } else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.LOAN_DISBURSAL_AMOUNT_REVERSAL
                .getValue()) {
            baseFinancialActivity = new DisbursalAmountReversalFinancialActivity(accounttrxn);
        }
        if (baseFinancialActivity != null)
            baseFinancialActivity.buildAccountEntries();
    }

    public List<GLCodeEntity> getGLCodes(FinancialActionConstants financialAction, FinancialConstants debitCredit)
            throws SystemException, ApplicationException {
        List<GLCodeEntity> glCodeList = new ArrayList<GLCodeEntity>();
        Set<COABO> applicableCategory = null;
        FinancialActionBO finActionFees = FinancialActionCache.getFinancialAction(financialAction);
        if (debitCredit.equals(FinancialConstants.DEBIT)) {
            applicableCategory = finActionFees.getApplicableDebitCharts();
        } else if (debitCredit.equals(FinancialConstants.CREDIT)) {
            applicableCategory = finActionFees.getApplicableCreditCharts();
        }

        for (COABO chartOfAccounts : applicableCategory) {
            glCodeList.add(chartOfAccounts.getAssociatedGlcode());
        }
        return glCodeList;
    }

    public final void buildFinancialEntries(final Set<AccountTrxnEntity> accountTrxns) throws AccountException {
        try {
            for (AccountTrxnEntity accountTrxn : accountTrxns) {
                buildAccountingEntries(accountTrxn);
            }
        } catch (FinancialException e) {
            throw new AccountException("errors.unexpected", e);
        }
    }

}
