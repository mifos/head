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

package org.mifos.test.acceptance.loan;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.EditPreviewLoanAccountPage;
import org.mifos.test.acceptance.framework.loan.GLIMClient;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"smoke","loan","acceptance","ui"})
public class CreateGLIMLoanAccountTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    private LoanAccountPage creationCheck(boolean creationType){

        //When
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("MyGroup1233266297718");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

        List<GLIMClient> clients= new ArrayList<GLIMClient>();
        clients.add(new GLIMClient(0,"Stu1233266299995 Client1233266299995 \n Client Id: 0006-000000051", "9999.9", "0700-Marriage"));
        clients.add(new GLIMClient(1, "Stu1233266309851 Client1233266309851 \n Client Id: 0006-000000052", "99999.9", "1008-Hospital"));
        clients.add(new GLIMClient(2, "Stu1233266319760 Client1233266319760 \n Client Id: 0006-000000053", "99999.9", "1010-Education"));

        for (GLIMClient glimClient : clients) {
            loanAccountEntryPage.selectGLIMClients(glimClient.getClientNumber(), glimClient.getClientName(), glimClient.getLoanAmount(), glimClient.getLoanPurpose());
        }
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage;
        if(creationType==true) {
            createLoanAccountConfirmationPage = loanAccountEntryPage.submitAndNavigateToGLIMLoanAccountConfirmationPage();
        } else {
            createLoanAccountConfirmationPage = loanAccountEntryPage.submitAndNavigateToGLIMLoanAccountConfirmationPageSaveForLaterButton();
        }
        LoanAccountPage loanAccountPage =  createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
        //Then
        if(creationType==true) {
            loanAccountPage.verifyLoanIsPendingApproval();
        } else {
            loanAccountPage.verifyLoanIsInPartialApplication();
        }
        EditLoanAccountInformationPage editLoanAccountInformationPage = loanAccountPage.navigateToEditAccountInformation();

        for (GLIMClient glimClient : clients) {
            editLoanAccountInformationPage.verifyGLIMClient(glimClient.getClientNumber(), glimClient.getClientName(), glimClient.getLoanAmount(), glimClient.getLoanPurpose());
        }

        editLoanAccountInformationPage.navigateBack();

        return loanAccountPage;
    }

    /*
     * This test is to verify that you can edit a GLIM loan account after it has been
     * dibursed without getting an invalid disbursal date error. See MIFOS-2597.
     */
    @Test(enabled=true)
    // http://mifosforge.jira.com/browse/MIFOSTEST-132
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void checkGLIMInvalidDisbursementDateWhenEditingLoan() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,11,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_011_dbunit.xml", dataSource, selenium);
        //When
        LoanAccountPage loanAccountPage = creationCheck(true);

        String accountId = loanAccountPage.getAccountId();

        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        statusParameters.setNote("Test");

        loanTestHelper.changeLoanAccountStatus(accountId, statusParameters);
        //Then
        //When
        DisburseLoanParameters params = new DisburseLoanParameters();

        params.setDisbursalDateDD("11");
        params.setDisbursalDateMM("07");
        params.setDisbursalDateYYYY("2009");
        params.setPaymentType(DisburseLoanParameters.CASH);

        loanTestHelper.disburseLoan(accountId, params);
        dateTimeUpdaterRemoteTestingService.setDateTime(new LocalDate(2009,7,20).toDateTimeAtStartOfDay());

        EditLoanAccountInformationParameters editLoanAccountInformationParameters = new EditLoanAccountInformationParameters();
        editLoanAccountInformationParameters.setExternalID("ID2323ID");

        loanAccountPage = editLoanAccount(accountId, editLoanAccountInformationParameters);
        //Then
        assertTextFoundOnPage(editLoanAccountInformationParameters.getExternalID());
   }

    @Test(enabled=true)
    // http://mifosforge.jira.com/browse/MIFOSTEST-133
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void checkLoanCreatedBySaveForLater() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,11,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_011_dbunit.xml", dataSource, selenium);

        creationCheck(false);
    }


    private LoanAccountPage editLoanAccount(String accountID, EditLoanAccountInformationParameters params) {
        NavigationHelper helper = new NavigationHelper(selenium);

        LoanAccountPage loanAccountPage = helper.navigateToLoanAccountPage(accountID);

        EditLoanAccountInformationPage editAccountInformationPage = loanAccountPage.navigateToEditAccountInformation();
        editAccountInformationPage.editExternalID(params);
        EditPreviewLoanAccountPage editPreviewLoanAccountPage = editAccountInformationPage.submitAndNavigateToAccountInformationPreviewPage();
        loanAccountPage = editPreviewLoanAccountPage.submitAndNavigateToLoanAccountPage();

        return loanAccountPage;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newWeeklyGLIMAccount() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_011_dbunit.xml", dataSource, selenium);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("MyGroup1233266297718");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

        loanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 \n Client Id: 0006-000000051", "301");
        loanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 \n Client Id: 0006-000000053", "401");

        loanAccountEntryPage.submitAndNavigateToGLIMLoanAccountConfirmationPage();
   }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void checkGLIMAccountTotalCalculationWithDecimal() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_011_dbunit.xml", dataSource, selenium);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("MyGroup1233266297718");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

        loanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 \n Client Id: 0006-000000051", "9999.9");
        loanAccountEntryPage.selectGLIMClients(1, "Stu1233266309851 Client1233266309851 \n Client Id: 0006-000000052", "99999.9");
        loanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 \n Client Id: 0006-000000053", "99999.9");

        loanAccountEntryPage.checkTotalAmount("209999.7");

   }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void checkGLIMAccountTotalCalculationWholeNumber() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_011_dbunit.xml", dataSource, selenium);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("MyGroup1233266297718");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

        loanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 \n Client Id: 0006-000000051", "9999");
        loanAccountEntryPage.selectGLIMClients(1, "Stu1233266309851 Client1233266309851 \n Client Id: 0006-000000052", "99999");
        loanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 \n Client Id: 0006-000000053", "99999");

        loanAccountEntryPage.checkTotalAmount("209997");

   }

}
