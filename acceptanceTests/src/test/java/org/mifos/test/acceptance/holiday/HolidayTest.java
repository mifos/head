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

package org.mifos.test.acceptance.holiday;

import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayConfirmationPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage;
import org.mifos.test.acceptance.framework.holiday.ViewHolidaysPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage.CreateHolidaySubmitParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"smoke","holiday","acceptance","ui"})
public class HolidayTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private AppLauncher appLauncher;

    public static final String HOLIDAY = "HOLIDAY";
    private static final String HOLIDAY_RESULT_DATA_SET = "HolidayTest_001_result_dbunit.xml";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,2,23,2,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        appLauncher = new AppLauncher(selenium);

    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @Test(enabled = false)
    public void createHoliday() throws Exception {
        dbUnitUtilities.loadDataFromFile("acceptance_default_003_dbunit.xml", dataSource);
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        CreateHolidayEntryPage createHolidayEntryPage = adminPage.navigateToDefineHolidayPage();
        createHolidayEntryPage.verifyPage();

        CreateHolidaySubmitParameters params = this.getHolidayParameters();
        CreateHolidayConfirmationPage confirmationPage = createHolidayEntryPage.submitAndNavigateToHolidayConfirmationPage(params);
        confirmationPage.verifyPage();
        confirmationPage.submitAndNavigateToViewHolidaysPage();

        verifyHolidayData(HOLIDAY_RESULT_DATA_SET);

        // try to create second holiday with the same date
        createHolidayEntryPage = adminPage.navigateToDefineHolidayPage();
        createHolidayEntryPage.verifyPage();
        params.setName("Test Holiday 2");
        confirmationPage = createHolidayEntryPage.submitAndNavigateToHolidayConfirmationPage(params);
        confirmationPage.verifyPage();
        assertTextFoundOnPage("Holiday with the same date already exists: Test Holiday",
                "Text about duplicated holidays was not found.");
    }

    public void viewHolidays() {
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        ViewHolidaysPage viewHolidays = adminPage.navigateToViewHolidays();
        viewHolidays.verifyPage();
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-79
    public void holidaysRepaymentRule() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,1,31,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        //When
        createHolidayForInstallments(getHolidayParametersForNextMeeting());
        createHolidayForInstallments(getHolidayParametersForNextMeeting2());

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1232993852651 Client1232993852651");
        searchParameters.setLoanProduct("MyLoanProduct1232993826860");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1423.0");
        submitAccountParameters.setGracePeriodTypeNone(true);
        submitAccountParameters.setDd("31");
        submitAccountParameters.setMm("01");
        submitAccountParameters.setYy("2011");

        //Then
        loanTestHelper.reviewInstallments(searchParameters, submitAccountParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyHolidayData(String resultDataSetFile) throws Exception {
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSetFile);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { HOLIDAY });

        dbUnitUtilities.verifyTable(HOLIDAY, databaseDataSet, expectedDataSet);
    }

    private CreateHolidaySubmitParameters getHolidayParameters() {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        params.setName("Test Holiday");
        params.setFromDateDD("13");
        params.setFromDateMM("06");
        params.setFromDateYYYY("2009");
        params.setThruDateDD("18");
        params.setThruDateMM("06");
        params.setThruDateYYYY("2009");
        params.setRepaymentRule(CreateHolidaySubmitParameters.NEXT_WORKING_DAY);
        params.setSelectedOfficeIds("1");
        return params;
    }


    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher
         .launchMifos()
         .loginSuccessfullyUsingDefaultCredentials()
         .navigateToAdminPage();
     }

    private void createHolidayForInstallments(CreateHolidaySubmitParameters params){
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();

        CreateHolidayEntryPage createHolidayEntryPage = adminPage.navigateToDefineHolidayPage();
        createHolidayEntryPage.verifyPage();
        CreateHolidayConfirmationPage confirmationPage = createHolidayEntryPage.submitAndNavigateToHolidayConfirmationPage(params);
        confirmationPage.verifyPage();
        confirmationPage.submitAndNavigateToViewHolidaysPage();
    }

    private CreateHolidaySubmitParameters getHolidayParametersForNextMeeting() {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        params.setName("Holiday Test");
        params.setFromDateDD("01");
        params.setFromDateMM("02");
        params.setFromDateYYYY("2011");
        params.setThruDateDD("14");
        params.setThruDateMM("02");
        params.setThruDateYYYY("2011");
        params.setRepaymentRule(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);
        params.setSelectedOfficeIds("1");
        return params;
    }
//
    private CreateHolidaySubmitParameters getHolidayParametersForNextMeeting2() {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        params.setName("Holiday Test2");
        params.setFromDateDD("01");
        params.setFromDateMM("03");
        params.setFromDateYYYY("2011");
        params.setThruDateDD("14");
        params.setThruDateMM("03");
        params.setThruDateYYYY("2011");
        params.setRepaymentRule(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);
        params.setSelectedOfficeIds("1");
        return params;
    }
}