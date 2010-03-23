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

package org.mifos.accounts.loan.util.helpers;

import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.business.LoanAmountOption;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.config.AccountingRules;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.framework.TestUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MultipleLoanCreationViewHelperTest {

    // system under test (SUT)
    private MultipleLoanCreationViewHelper multipleLoanCreationViewHelper;

    @Mock
    private ClientBO client;

    @Mock
    private LoanAmountOption loanAmountOption;

    @Mock
    private LoanOfferingInstallmentRange installmentOption;

    @Before
    public void setUp(){
        multipleLoanCreationViewHelper = new MultipleLoanCreationViewHelper(client,loanAmountOption,installmentOption,TestUtils.RUPEE);
    }

    @After
    public void tearDownUp(){
        multipleLoanCreationViewHelper = null;
    }

    @Test
    public void testLoanAmountAndMaxMinRangeWithDigitAfterDecimalIsZero() {
        Short savedDigitAfterDecimal = AccountingRules.getDigitsAfterDecimal();
        AccountingRules.setDigitsAfterDecimal(Short.valueOf("0"));
        when(loanAmountOption.getDefaultLoanAmount()).thenReturn(5000.0);
        when(loanAmountOption.getMaxLoanAmount()).thenReturn(10000.0);
        when(loanAmountOption.getMinLoanAmount()).thenReturn(1000.0);

        Assert.assertEquals("5000", multipleLoanCreationViewHelper.getDefaultLoanAmount().toString());
        Assert.assertEquals("10000", multipleLoanCreationViewHelper.getMaxLoanAmount().toString());
        Assert.assertEquals("1000", multipleLoanCreationViewHelper.getMinLoanAmount().toString());
        AccountingRules.setDigitsAfterDecimal(savedDigitAfterDecimal);
    }

    @Test
    public void testLoanAmountAndMaxMinRangeWithDigitAfterDecimalIsOne() {
        Short savedDigitAfterDecimal = AccountingRules.getDigitsAfterDecimal();
        AccountingRules.setDigitsAfterDecimal(Short.valueOf("1"));
        when(loanAmountOption.getDefaultLoanAmount()).thenReturn(5000.0);
        when(loanAmountOption.getMaxLoanAmount()).thenReturn(10000.0);
        when(loanAmountOption.getMinLoanAmount()).thenReturn(1000.0);

        Assert.assertEquals("5000.0", multipleLoanCreationViewHelper.getDefaultLoanAmount().toString());
        Assert.assertEquals("10000.0", multipleLoanCreationViewHelper.getMaxLoanAmount().toString());
        Assert.assertEquals("1000.0", multipleLoanCreationViewHelper.getMinLoanAmount().toString());
        AccountingRules.setDigitsAfterDecimal(savedDigitAfterDecimal);
    }

    @Test
    public void testLoanAmountAndMaxMinRangeWithDigitAfterDecimalIsTwo() {
        Short savedDigitAfterDecimal = AccountingRules.getDigitsAfterDecimal();
        AccountingRules.setDigitsAfterDecimal(Short.valueOf("2"));
        when(loanAmountOption.getDefaultLoanAmount()).thenReturn(5000.0);
        when(loanAmountOption.getMaxLoanAmount()).thenReturn(10000.0);
        when(loanAmountOption.getMinLoanAmount()).thenReturn(1000.0);

        Assert.assertEquals("5000.00", multipleLoanCreationViewHelper.getDefaultLoanAmount().toString());
        Assert.assertEquals("10000.00", multipleLoanCreationViewHelper.getMaxLoanAmount().toString());
        Assert.assertEquals("1000.00", multipleLoanCreationViewHelper.getMinLoanAmount().toString());
        AccountingRules.setDigitsAfterDecimal(savedDigitAfterDecimal);
    }

}
