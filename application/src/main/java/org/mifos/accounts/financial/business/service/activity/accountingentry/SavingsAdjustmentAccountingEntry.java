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

package org.mifos.accounts.financial.business.service.activity.accountingentry;

import org.mifos.accounts.financial.business.FinancialActionBO;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;

/**
 * Create accounting entries for an adjustment to a deposit or withdrawal on a savings account.
 * <p>
 * Additional dependencies and collaborations:
 * <ul>
 *     <li><code>BaseFinancialActivity</code> (parameter) -- knows the
 *                         <code>SavingsTrxnDetailEntity</code> instance for the activity.
 *     <li><code>SavingsTrxnDetailEntity</code> instance knows
 *         <ul>
 *             <li>the <code>SavingsBO</code> instance
 *             <li>the deposit or withdrawal amount of the adjustment
 *         </ul>
 *     <li><code>SavingsBO</code> instance knows
 *         <ul>
 *             <li>its last payment (the one to be adjusted) (<code>AccountPaymentEntity</code>)
 *             <li>whether it is a mandatory or voluntary account.
 *             <li>its associated <code>SavingsOfferingBO</code> instance
 *         </ul>
 *     <li>the <code>SavingsOfferingBO</code> knows its deposit and withdrawal GL codes.
 *     <li><code>SavingsHelper</code> instance knows the payment action type (deposit or withdrawal) of the payment.
 *     <li><code>
 * </ul>
 *
 * <h3>Post-conditions</h3>
 * <p>
 * Creates two <code>FinancialTransactionBO</code> and adds them to the financialActivity. These values
 * are set for both transactions:
 *     <table border=1 cellspacing=0 cellpadding=5>
 *       <tr><td>*accountTrxn         </td><td>financialActivity.getAccountTrxn()c
 *       <tr><td>*relatedFinancialTrxn</td><td>null</td></tr>
 *        <tr><td>*actionDate          </td><td>financialActivity.getAccountTrxn().getActionDate()</td></tr>
 *        <tr><td>vpostedDate          </td><td> is unspecified</td></tr>
 *        <tr><td>*postedBy            </td><td>financialActivity.getAccountTrxn().getPersonnel()</td></tr>
 *        <tr><td>*accountingUpdated   </td><td>(short) 1</td></tr>
 *        <tr><td>balanceAmount        </td><td>is unspecified</td></tr>
 *        <tr><td>*notes               </td><td>financialActivity.getAccountTrxn().getComments()</td></tr>
 *      </table>
 * <p>
 * If the adjustment is to a savings withdrawal, these two transactions are created with these
 * additional field values:
 *
 *   <ul>
 *     <li>a FinancialTransactionBO debiting the savings account where:
 *
 * <p>
 *         <table border=1 cellspacing=0 cellpadding=5 vspace=10>
 *            <tr><td>postedAmount         </td><td> <code>savingsTrxn.getWithdrawlAmount()</code>, adjusted for sign per specs<br/>
 *                                         (see <code>BaseAccountingEntry.getAmountToPost()</code></td></tr>
 *            <tr><td>financialAction      </td><td> the FinancialActionBO corresponding to one of<br/>
 *                                            FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT
 *                                            <br/>or FinancialActionConstants.VOLUNTORYWITHDRAWAL_ADJUSTMENT
 *                                         <br/>depending on the type of savings account.</td></tr>
 *            <tr><td>glCode               </td><td> savings.getSavingsOffering().getDepositGLCode()</td></tr>
 *            <tr><td>postedAmount         </td><td> SavingsTrxnDetailEntity.withdrawalAmount</td></tr>
 *            <tr><td>debitCreditFlag      </td><td> FinancialConstants.DEBIT</td></tr>
 *         </table>
 *
 * <p>
 *     <li>a financialTransactionBO crediting a savings or deposit liability account:
 * <p>
 *         <table border=1 cellspacing=0 cellpadding=5 vspace=10>
 *            <tr><td>  postedAmount         </td><td> <code>savingsTrxn.getWithdrawlAmount()</code>, adjusted for sign per specs <br/>
 *                                         (see <code>BaseAccountingEntry.getAmountToPost()</code></td></tr>
 *            <tr><td>  financialAction      </td><td> the FinancialActionBO corresponding to one o <br/>
 *                                            FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT <br/>
 *                                            or FinancialActionConstants.VOLUNTORYWITHDRAWAL_ADJUSTMENT <br/>
 *                                         depending on the type of savings account.</td><tr>
 *            <tr><td>  glCode               </td><td> **the GLCodeEntity of the GL account to debit for the above financial action</td><tr>
 *            <tr><td>  postedAmount         </td><td> SavingsTrxnDetailEntity.withdrawalAmount</td><tr>
 *            <tr><td>  debitCreditFlag      </td><td> FinancialConstants.CREDIT</td><tr>
 *         </table>
 *    </ul>
 *
 * <p>
 * If the adjustment is to a savings deposit, these two transactions are created with these
 * additional field values:
 *   <ul>
 *     <li>a FinancialTransactionBO debiting the savings account where:
 * <p>
 *         <table border=1 cellspacing=0 cellpadding=5 vspace=10>
 *            <tr><td>  postedAmount         </td><td> <code>savingsTrxn.getDepositAmount()</code>, adjusted for sign per specs <br/>
 *                                         (see <code>BaseAccountingEntry.getAmountToPost()</code> </td><tr>
 *            <tr><td>  financialAction      </td><td> the FinancialActionBO corresponding to one o <br/>
 *                                            FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT <br/>
 *                                            or FinancialActionConstants.VOLUNTORYDEPOSIT_ADJUSTMENT <br/>
 *                                         depending on the type of savings account.</td><tr>
 *            <tr><td>  glCode               </td><td> savings.getSavingsOffering().getDepositGLCode()</td><tr>
 *            <tr><td>  debitCreditFlag      </td><td> FinancialConstants.DEBIT</td><tr>
 *         </table>
 *  <p>
 *     <li>a financialTransactionBO crediting a savings or deposit liability account:
 *     <p>
 *         <table border=1 cellspacing=0 cellpadding=5 vspace=10>
 *            <tr><td>  postedAmount         </td><td> <code>savingsTrxn.getDepositAmount()</code>, adjusted for sign per specs <br/>
 *                                         (see <code>BaseAccountingEntry.getAmountToPost()</code></td><tr>
 *            <tr><td>  financialAction      </td><td> the FinancialActionBO corresponding to one <br/>
 *                                            FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT<br/>
 *                                            or FinancialActionConstants.VOLUNTORYDEPOSIT_ADJUSTMENT<br/>
 *                                         depending on the type of savings account.</td><tr>
 *            <tr><td>  glCode               </td><td> **the GLCodeEntity of the GL account to credit for the above financial action</td><tr>
 *            <tr><td>  debitCreditFlag      </td><td> FinancialConstants.CREDIT</td><tr>
 *         </table>
 *    </ul>
 * <p>
 * Notes:
 * <dl>
 *     <dd>*Set by the superclass
 *     <dd>**In most cases, the GL code mapped to by <code>FinancialRules</code> configuration. However, if the financial
 *         action maps to a GL code that is not at the lowest level of hierarchy in the chart of accounts, the result
 *         is a GL code chosen randomly among the lowest level descendants of the mapped GL code.
 *  </dl>
 *
 *
 */
public class SavingsAdjustmentAccountingEntry extends BaseAccountingEntry {

        private SavingsHelper savingsHelper = new SavingsHelper(); //use fresh instance when not being injected

    @Override
    protected void applySpecificAccountActionEntry() throws FinancialException {
        SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) financialActivity.getAccountTrxn();

        SavingsBO savings = (SavingsBO) savingsTrxn.getAccount();
        if (isAdjustmentForWithdrawal(savings)) {
            adjustWithdrawal(savings, savingsTrxn);
        } else {
            adjustDeposit(savings, savingsTrxn);
        }
    }

    /**
     * Added so that a mocked SavingsHelper can be injected by a unit test
     */
    public SavingsHelper getSavingsHelper() {
        return savingsHelper;
    }

    public void setSavingsHelper(SavingsHelper helper) {
        this.savingsHelper = helper;
    }

    protected boolean isAdjustmentForWithdrawal(SavingsBO savings) {
        return (getSavingsHelper().getPaymentActionType(savings.getLastPmnt())
                .equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()));
    }


    private void adjustWithdrawal(SavingsBO savings, SavingsTrxnDetailEntity savingsTrxn) throws FinancialException {
        FinancialActionBO finActionWithrawal = null;
        if (savings.getSavingsType().getId().equals(SavingsType.MANDATORY.getValue())) {
            finActionWithrawal = getFinancialAction(FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT);
        }
        if (savings.getSavingsType().getId().equals(SavingsType.VOLUNTARY.getValue())) {
            finActionWithrawal = getFinancialAction(FinancialActionConstants.VOLUNTORYWITHDRAWAL_ADJUSTMENT);
        }
        addAccountEntryDetails(savingsTrxn.getWithdrawlAmount(), finActionWithrawal, savings
                .getSavingsOffering().getDepositGLCode(), FinancialConstants.CREDIT);
        addAccountEntryDetails(savingsTrxn.getWithdrawlAmount(), finActionWithrawal,
                getGLcode(finActionWithrawal.getApplicableDebitCharts()), FinancialConstants.DEBIT);
    }

    private void adjustDeposit(SavingsBO savings, SavingsTrxnDetailEntity savingsTrxn) throws FinancialException {
        FinancialActionBO finActionDeposit = null;
        if (savings.getSavingsType().getId().equals(SavingsType.MANDATORY.getValue())) {
            finActionDeposit = getFinancialAction(FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT);
        }
        if (savings.getSavingsType().getId().equals(SavingsType.VOLUNTARY.getValue())) {
            finActionDeposit = getFinancialAction(FinancialActionConstants.VOLUNTORYDEPOSIT_ADJUSTMENT);
        }
        addAccountEntryDetails(savingsTrxn.getDepositAmount(), finActionDeposit, getGLcode(finActionDeposit
                .getApplicableCreditCharts()), FinancialConstants.CREDIT);

        addAccountEntryDetails(savingsTrxn.getDepositAmount(), finActionDeposit, savings
                .getSavingsOffering().getDepositGLCode(), FinancialConstants.DEBIT);
    }
}
