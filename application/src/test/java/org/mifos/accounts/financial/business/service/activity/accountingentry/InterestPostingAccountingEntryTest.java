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
package org.mifos.accounts.financial.business.service.activity.accountingentry;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.COAHierarchyEntity;
import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.financial.business.GLCategoryType;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.activity.SavingsInterestPostingFinancialActivity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link InterestPostingAccountingEntry}.
 */
@RunWith(MockitoJUnitRunner.class)
public class InterestPostingAccountingEntryTest extends BaseAccountingEntryTestCase {

    private final String interestAmount = "10";

    // class under test
    private InterestPostingAccountingEntry interestPostingAccountingEntry;

    @Mock
    private SavingsTrxnDetailEntity savingsTrxnDetail;

    @Mock
    private SavingsOfferingBO savingsOffering;

    @Mock
    private SavingsBO savingsBO;

    @Mock
    private PersonnelBO transactionCreator;

    COABO coaLiability;
    COABO coaSavingsInterestPayable;
    COABO coaClientsSavings;

    @Mock
    private FinancialActionTypeEntity financialAction;

    @Before
    public void setupAndInjectMocks() {

        // Build a little Chart of Accounts
        coaLiability = makeCategory((short) 1, GLCategoryType.LIABILITY, "22000");
        coaSavingsInterestPayable = makeChildCoaboOf(coaLiability, (short) 2, "SavingsInterest Payable", "22100");
        coaClientsSavings = makeChildCoaboOf(coaLiability, (short) 3, "ClientsSavings", "22200");

        interestPostingAccountingEntry = new InterestPostingAccountingEntry() {

            @Override
            protected FinancialActionTypeEntity getFinancialAction(
                    @SuppressWarnings("unused") final FinancialActionConstants financialActionId)
                    throws FinancialException {
                return financialAction;
            }

            @Override
            protected COABO getChartOfAccountsEntry(final String glcode) throws FinancialException {
                if (glcode.equals("22000")) {
                    return coaLiability;
                } else if (glcode.equals("22100")) {
                    return coaSavingsInterestPayable;
                } else if (glcode.equals("22200")) {
                    return coaClientsSavings;
                } else {
                    throw new FinancialException("unexpected glcode: " + glcode);
                }

            }

        };
    }

    @Test
    public void testBuildAccountEntryForAction() throws FinancialException {

        // setup
        DateMidnight savingsTrxnDetailActionDate = new DateMidnight(2009, 9, 9);
        DateMidnight savingsTrxnDetailCreationDate = new DateMidnight(2009, 1, 1);
        MifosCurrency currency = TestUtils.RUPEE;

        // stubbing
        when(savingsTrxnDetail.getAccount()).thenReturn(savingsBO);
        when(savingsTrxnDetail.getInterestAmount()).thenReturn(new Money(currency, interestAmount));
        when(savingsTrxnDetail.getActionDate()).thenReturn(savingsTrxnDetailActionDate.toDate());
        when(savingsTrxnDetail.getTrxnCreatedDate()).thenReturn(
                new Timestamp(savingsTrxnDetailCreationDate.getMillis()));
        when(savingsTrxnDetail.getPersonnel()).thenReturn(transactionCreator);

        when(savingsBO.getSavingsOffering()).thenReturn(savingsOffering);

        when(savingsOffering.getInterestGLCode()).thenReturn(coaSavingsInterestPayable.getAssociatedGlcode());
        when(savingsOffering.getDepositGLCode()).thenReturn(coaClientsSavings.getAssociatedGlcode());

        SavingsInterestPostingFinancialActivity financialActivity = new SavingsInterestPostingFinancialActivity(
                savingsTrxnDetail);

        // exercise test
        interestPostingAccountingEntry.buildAccountEntryForAction(financialActivity);

        // verification
        List<FinancialTransactionBO> transactions = financialActivity.getFinanacialTransaction();
        assertThat(transactions.size(), is(2));

        /*
         * Sort by GLCode strings so "22100" (interest payable tran) should be first, followed by "22200" (client
         * savings tran)
         */
        Collections.sort(transactions, new GLCodeComparator());
        Iterator<FinancialTransactionBO> it = transactions.iterator();


        FinancialTransactionBO interestPostingTrans = it.next();
        assertThat(interestPostingTrans.getActionDate(), is(savingsTrxnDetailActionDate.toDate()));
        assertThat(interestPostingTrans.getPostedDate(), is(savingsTrxnDetailCreationDate.toDate()));
        assertThat(interestPostingTrans.getPostedAmount(), is(new Money(currency, interestAmount)));
        assertThat(interestPostingTrans.getGlcode().getGlcode(), is(coaSavingsInterestPayable.getGlCode()));
        assertThat(interestPostingTrans.getDebitCreditFlag(), is(FinancialConstants.DEBIT.getValue()));
        assertThat(interestPostingTrans.getPostedBy(), is(transactionCreator));

        FinancialTransactionBO savingsPostingTrans = it.next();
        assertThat(savingsPostingTrans.getActionDate(), is(savingsTrxnDetailActionDate.toDate()));
        assertThat(savingsPostingTrans.getPostedDate(), is(savingsTrxnDetailCreationDate.toDate()));
        assertThat(savingsPostingTrans.getPostedAmount(), is(new Money(currency, interestAmount)));
        assertThat(savingsPostingTrans.getGlcode().getGlcode(), is(coaClientsSavings.getGlCode()));
        assertThat(savingsPostingTrans.getDebitCreditFlag(), is(FinancialConstants.CREDIT.getValue()));
        assertThat(savingsPostingTrans.getPostedBy(), is(transactionCreator));
    }

    protected COABO makeCategory(Short categoryId, GLCategoryType categoryType, String glCode) {
        COABO category = new COABO(categoryId, categoryType.name(), new GLCodeEntity(categoryId, glCode));
        COAHierarchyEntity hierarchy = new COAHierarchyEntity(category, null);
        category.setCoaHierarchy(hierarchy);
        category.setCategoryType(categoryType);
        return category;
    }

    /**
     * Establish child-parent relationship between two COABO instances.
     *
     * <p>
     * ASSUMPTION: the parentCoa's hierarchy has been created. In other words, build the hierarchy from top down.
     *
     * @throws RuntimeException  if parentCoa has no associated COAHierarchy.
     */
    protected COABO makeChildCoaboOf(COABO parentCoa, Short accountId, String accountName, String glCode) {
        COAHierarchyEntity parentCoah = parentCoa.getCoaHierarchy();
        if (parentCoah == null) {
            throw new RuntimeException("ParentCoa.coaHierarchy has not been defined");
        }
        COABO childCoa = new COABO(accountId, accountName, new GLCodeEntity(accountId, glCode));
        COAHierarchyEntity hierarchy = new COAHierarchyEntity(childCoa, parentCoa.getCoaHierarchy());
        childCoa.setCoaHierarchy(hierarchy);
        return childCoa;
    }

    /**
     * Compare GLCodes lexicographically by GL Code string values
     *
     */
    protected class GLCodeComparator implements Comparator<FinancialTransactionBO> {
        public int compare(final FinancialTransactionBO tran1, final FinancialTransactionBO tran2) {
            return tran1.getGlcode().getGlcode().compareTo(tran2.getGlcode().getGlcode());
        }
    }
}
