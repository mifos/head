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
package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.List;

import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.COAHierarchyEntity;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.GLCategoryType;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.business.service.activity.SavingsInterestPostingFinancialActivity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link InterestPostingAccountingEntry}.
 */
@RunWith(MockitoJUnitRunner.class)
public class InterestPostingAccountingEntryTest {

    // class under test
    private InterestPostingAccountingEntry interestPostingAccountingEntry;
    
    @Mock
    private SavingsTrxnDetailEntity savingsTrxnDetail;
    
    @Mock
    private SavingsOfferingBO savingsOffering;

    @Mock
    private SavingsBO savingsBO;

    @Mock
    private GLCodeEntity codeEntity;
    
    @Mock
    private FinancialActionBO financialAction;
    
    @BeforeClass
    public static void classSetup() {
        MifosLogManager.configureLogging();
    }
    
    @Before
    public void setupAndInjectMocks() {
        
        final COAHierarchyEntity parentHierarchy = null;
        final COABO coaAsset = new COABO(2, "what");
        coaAsset.setCategoryType(GLCategoryType.ASSET);
        COAHierarchyEntity coaHierarchy = new COAHierarchyEntity(coaAsset, parentHierarchy);

        final COABO chartOfAccounts = new COABO(1, "");
        chartOfAccounts.setCoaHierarchy(coaHierarchy);
        
        interestPostingAccountingEntry = new InterestPostingAccountingEntry() {

            @Override
            protected FinancialActionBO getFinancialAction(
                    @SuppressWarnings("unused") final FinancialActionConstants financialActionId)
                    throws FinancialException {
                return financialAction;
            }
            
            @Override
            protected COABO getChartOfAccountsEntry(@SuppressWarnings("unused") final String glcode)
                    throws FinancialException {
                return chartOfAccounts;
            }
            
        };
    }
    
    @Test
    public void shouldSetActionDateOfFinancialTransationToBeTheSameAsActionDateOfSavingsTrxnDetailForDebitOrCredit()
            throws FinancialException {

        // setup
        DateMidnight savingsTrxnDetailActionDate = new DateMidnight(2009, 9, 9);
        
        // stubbing
        when(savingsTrxnDetail.getAccount()).thenReturn(savingsBO);
        when(savingsTrxnDetail.getInterestAmount()).thenReturn(new Money("10"));
        when(savingsTrxnDetail.getActionDate()).thenReturn(savingsTrxnDetailActionDate.toDate());

        when(savingsBO.getSavingsOffering()).thenReturn(savingsOffering);
        when(savingsOffering.getInterestGLCode()).thenReturn(codeEntity);
        when(savingsOffering.getDepositGLCode()).thenReturn(codeEntity);

        SavingsInterestPostingFinancialActivity financialActivity = new SavingsInterestPostingFinancialActivity(
                savingsTrxnDetail);

        // exercise test
        interestPostingAccountingEntry.buildAccountEntryForAction(financialActivity);

        // verification
        List<FinancialTransactionBO> transactions = financialActivity.getFinanacialTransaction();
        FinancialTransactionBO trans = transactions.iterator().next();

        assertThat(trans.getActionDate(), is(savingsTrxnDetailActionDate.toDate()));
    }

    @Test
    public void shouldSetPostedDateOfFinancialTransationToBeTheSameAsCreationDateOfSavingsTrxnDetailForDebitOrCredit()
            throws FinancialException {

        // setup
        DateMidnight savingsTrxnDetailCreationDate = new DateMidnight(2009, 1, 1);
        
        // stubbing
        when(savingsTrxnDetail.getAccount()).thenReturn(savingsBO);
        when(savingsTrxnDetail.getInterestAmount()).thenReturn(new Money("10"));
        when(savingsTrxnDetail.getTrxnCreatedDate()).thenReturn(
                new Timestamp(savingsTrxnDetailCreationDate.getMillis()));
        
        when(savingsBO.getSavingsOffering()).thenReturn(savingsOffering);
        when(savingsOffering.getInterestGLCode()).thenReturn(codeEntity);
        when(savingsOffering.getDepositGLCode()).thenReturn(codeEntity);

        SavingsInterestPostingFinancialActivity financialActivity = new SavingsInterestPostingFinancialActivity(
                savingsTrxnDetail);

        // exercise test
        interestPostingAccountingEntry.buildAccountEntryForAction(financialActivity);

        // verification
        List<FinancialTransactionBO> transactions = financialActivity.getFinanacialTransaction();
        FinancialTransactionBO trans = transactions.iterator().next();

        assertThat(trans.getPostedDate(), is(savingsTrxnDetailCreationDate.toDate()));
    }
}
