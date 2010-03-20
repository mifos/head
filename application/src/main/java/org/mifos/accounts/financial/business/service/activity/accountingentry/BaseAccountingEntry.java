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

import java.util.Iterator;
import java.util.Set;

import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.FinancialActionBO;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.accounts.financial.business.service.activity.BaseFinancialActivity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.framework.util.helpers.Money;

/**
 * The abstract base class for all classes that create accounting entries from financial activities.
 * These accounting entries -- <code>FinancialTransactionBO</code> instances -- are used to post the
 * financial activity to one or more general ledger accounts.
 * <p>
 * Subclasses create accounting entries by side effect on a passed-in <code>BaseFinancialActivity</code> instance:
 * When <code>buildAccountEntryForAction(financialActivity)</code>
 * is called, the subclass creates a list of <code>{@link FinancialTransactionBO}</code>
 * instances and stores it in the financialActivity. The caller can then retrieve the list using
 * <code>financialActivity.getFinancialTransactions()</code>.
 * <p>
 * Subclasses depend on the following classes for correct operation (protected methods using these classes
 * are defined here). Subclasses may have additional dependencies that are documented there.
 * <ul>
 *     <li>The parameter <code>BaseFinancialActivity</code> knows its <code>AccountTrxnEntity</code> instance</li>
 *     <li> The <code>AccountTrxnEntity</code> instance knows its field values that it passes through to
 *          the created account entries:</li>
 *         <ul>
 *             <li> actionDate</li>
 *             <li> personnel</li>
 *             <li> comments</li>
 *             <li> createDate</li>
 *         </ul>
 *     <li><code>ChartOfAccountsCache</code> (static) retrieves the correct GL accounts for a transaction.</li>
 *     <li><code>FinancialActionCache</code> (static) retrieves a <code>FinancialActionBO</code> instance for a
 *         financial action id.</li>
 *     <li><code>COABO</code>  instances (GL accounts) can find their top-level (head) category.</li>
 *     <li>concrete subclass instances of this class compute <code>FinancialTransdactionBO</code> instances
 *         from information in the financialActivity and invoke <code>addAccountEntryDetails()</code> for each
 *         instance.</li>
 * </ul>
 *
 *  Also see
 * <a href="http://www.mifos.org/knowledge/functional-specifications/system-setup/chart-of-accounts">
 * chart-of-accounts </a> page for details.
 * <p>
 * <h3>Post-conditions</h3>
 * <p>
 * <code>financialActivity.getFinancialTransactions()</code> returns a list of <code>FinancialTransactionBO</code>
 * instances according to the functional specifications for the specific financial activity. See the
 * concrete subclasses for specifics.
 *
 */
public abstract class BaseAccountingEntry {
    protected BaseFinancialActivity financialActivity;

    protected FinancialBusinessService financialBusinessService = new FinancialBusinessService();

    protected FinancialBusinessService getFinancialBusinessService() {
        return this.financialBusinessService;
    }

    public void setFinancialBusinessService(FinancialBusinessService financialBusinessService) {
        this.financialBusinessService = financialBusinessService;
    }


    /*
     * Factor out access to the static cache to allow this method to be overridden for testing.
     * Globals like FinancialActionCache should be eliminated or refactored to be injectable
     * [KeithP] FinancialActionCache is now encapsulated in FinancialBusinessService, where it can
     * be refactored away without affecting this class.
     */
    protected FinancialActionBO getFinancialAction(final FinancialActionConstants financialActionId)
            throws FinancialException {
        // FinancialActionCache.getFinancialAction(financialActionId);
        return financialBusinessService.getFinancialAction(financialActionId);
    }

   /*
    * Factor out access to the static cache to allow this method to be overridden for testing.
    * Globals like ChartOfAccountsCache should be eliminated or refactored to be injectable.
    *
    * [KeithP] ChartOfAccountsCache is now encapsulated in FinancialBusinessService, where it can
    * be refactored away without affecting this class.
    */
    protected COABO getChartOfAccountsEntry(final String glcode) throws FinancialException {
        // return ChartOfAccountsCache.get(glcode);
        return financialBusinessService.getGlAccount(glcode);
    }

    public void buildAccountEntryForAction(final BaseFinancialActivity financialActivity) throws FinancialException {
        this.financialActivity = financialActivity;
        applySpecificAccountActionEntry();
    }

    protected void addAccountEntryDetails(final Money postedMoney, final FinancialActionBO financialAction, final GLCodeEntity glcode,
            final FinancialConstants debitCredit) {
        if (postedMoney.isNonZero()) {
            Money amountToPost = removeSign(postedMoney);
            FinancialTransactionBO financialTransaction = new FinancialTransactionBO(
                    financialActivity.getAccountTrxn(), null, financialAction, glcode, financialActivity
                            .getAccountTrxn().getActionDate(), financialActivity.getAccountTrxn().getPersonnel(),
                    (short) 1, amountToPost, financialActivity.getAccountTrxn().getComments(), debitCredit.getValue(),
                    financialActivity.getAccountTrxn().getTrxnCreatedDate());
            financialActivity.addFinancialTransaction(financialTransaction);
        }
    }

    protected abstract void applySpecificAccountActionEntry() throws FinancialException;

    /**
     * gets the general ledger code (an entity object) for the account to be credited or debited, from a set of COABO
     * objects. If the set is empty, return null. If the set contains exactly one COABO object, return its associated
     * GLCodeEntity. If the set contains two or more COABO objects, return the associated GLCodeEntity for one chosen at
     * random (determined by the implementation of the set's iterator.)
     * <p>
     * The account returned must be at the lowest level in the chart-of-accounts hierarchy (see this <a href="http://www.mifos.org/knowledge/functional-specifications/system-setup/chart-of-accounts#debit-credit-financial-transactions"
     * > functional specification</a>, "The entries should be made against the lowest level GL Code under a category".
     * Subclasses enforces this rule by creating the set by invoking <code>COABO.getAssociatedChartOfAccounts()</code>
     * which creates a set of only lowest-level COABO objects below the give COABO.
     * <p>
     * This random behavior occurs when the GL code mapped to by a financial action is not at the lowest level. For
     * example, in the default financial action mapping,
     *
     * @param a set of GL accounts
     * @return the GL account code entity of the member of the account set, if it contains just one account. If the set
     *         is empty, return null. If the set contains two or more accounts, return an account code chosen at random.
     */
    protected GLCodeEntity getGLcode(final Set<COABO> chartsOfAccounts) {
        Iterator<COABO> iter = chartsOfAccounts.iterator();
        GLCodeEntity glcode = null;
        while (iter.hasNext()) {
            glcode = iter.next().getAssociatedGlcode();
        }
        return glcode;

    }

    protected Money removeSign(final Money amount) {
        if (amount != null && amount.isLessThanZero()) {
            return amount.negate();
        }
        return amount;
    }
}
