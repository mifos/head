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

package org.mifos.test.acceptance.loan;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"loan","acceptance","ui"})
public class CreateGroupLoanAccountTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newWeeklyGroupLoanAccount() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml.zip", dataSource, selenium);
        
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("MyGroup1233266297718");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("2765.0");
        
        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        loanAccountPage.verifyPage();
    }
    
    @Test( groups = {"smoke"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newMonthlyGroupLoanAccountWithMeetingOnSpecificDayOfMonth() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_005_dbunit.xml.zip", dataSource, selenium);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("GroupfirstOfMonth");
        searchParameters.setLoanProduct("FlatMonthlyGroupLoan");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1000.0");
        
        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        loanAccountPage.verifyPage();
    }
    
    @Test( groups = {"smoke"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newMonthlyGroupLoanAccountWithMeetingOnSameWeekAndWeekdayOfMonth() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_005_dbunit.xml.zip", dataSource, selenium);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Group3rdFriday");
        searchParameters.setLoanProduct("FlatMonthlyGroupLoan");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1000.0");
        
        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        loanAccountPage.verifyPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void tryCreateGroupLoanWithoutMandatoryPurposeOfLoan() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_011_dbunit.xml.zip", dataSource, selenium);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("MyGroup1233266297718");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

        loanAccountEntryPage.selectTwoClientsForGlim();
        loanAccountEntryPage.clickContinue();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void tryCreateGroupLoanWithMandatoryPurposeOfLoan() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_012_dbunit.xml.zip", dataSource, selenium);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("MyGroup1233266297718");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

        loanAccountEntryPage.selectTwoClientsForGlim();
        loanAccountEntryPage.selectPurposeForGlim();
        loanAccountEntryPage.clickContinue();
    }
}
