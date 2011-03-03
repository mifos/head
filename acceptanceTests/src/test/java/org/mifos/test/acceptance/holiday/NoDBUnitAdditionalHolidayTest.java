package org.mifos.test.acceptance.holiday;

import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayConfirmationPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = { "holiday", "schedules", "acceptance", "ui", "no_db_unit" })
public class NoDBUnitAdditionalHolidayTest extends UiTestCaseBase {
    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    private static final String LOAN_SCHEDULE = "LOAN_SCHEDULE";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(
                selenium);
        DateTime targetTime = new DateTime(2009, 3, 11, 0, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void logOut() {
        new MifosPage(selenium).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createHolidayOnAMeetingWithRepaymentSameDay() throws Exception {

        // create loan paid on the 1st of every month and then create a holiday on 1st July
        createMonthlyLoanScheduleAndAHoliday(CreateHolidayEntryPage.CreateHolidaySubmitParameters.SAME_DAY);

        List<String> jobsToRun = new ArrayList<String>();
        jobsToRun.add("ApplyHolidayChangesTaskJob");
        new BatchJobHelper(selenium).runSomeBatchJobs(jobsToRun);

        verifyLoanSchedule("AdditionalHolidayTest_010_result_dbunit.xml");
    }

    private void createMonthlyLoanScheduleAndAHoliday(final String repaymentRule) {

        // create a loan that has its repayment on the 1st of every month
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct("MonthlyClientFlatLoan1stOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1234.0");
        submitAccountParameters.setDd("01");
        submitAccountParameters.setMm("03");
        submitAccountParameters.setYy("2010");

        createLoan(searchParameters, submitAccountParameters);

        // create a holiday on 1st of july
        createHolidayOn1stJuly(repaymentRule);
    }

    private void createHolidayOn1stJuly(final String repaymentRule) {
        CreateHolidayEntryPage.CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();

        params.setName("Canada Day");
        params.setFromDateDD("1");
        params.setFromDateMM("07");
        params.setFromDateYYYY("2009");
        params.setRepaymentRule(repaymentRule);
        params.setSelectedOfficeIds("1");

        createHoliday(params);
    }

    private void createHoliday(final CreateHolidayEntryPage.CreateHolidaySubmitParameters params) {
        logOut();
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        CreateHolidayEntryPage createHolidayEntryPage = adminPage.navigateToDefineHolidayPage();

        CreateHolidayConfirmationPage confirmationPage = createHolidayEntryPage
                .submitAndNavigateToHolidayConfirmationPage(params);
        confirmationPage.submitAndNavigateToViewHolidaysPage();
    }

    private void createLoan(final CreateLoanAccountSearchParameters searchParameters,
            final CreateLoanAccountSubmitParameters submitAccountParameters) {
        logOut();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = navigateToCreateLoanAccountSearchPage();
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage
                .searchAndNavigateToCreateLoanAccountPage(searchParameters);
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountEntryPage
                .submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters);
        createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
    }

    private CreateLoanAccountSearchPage navigateToCreateLoanAccountSearchPage() {
        LoginPage loginPage = appLauncher.launchMifos();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        return clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyLoanSchedule(final String resultDataSet) throws Exception {
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSet);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { LOAN_SCHEDULE });

        dbUnitUtilities.verifyTable(LOAN_SCHEDULE, databaseDataSet, expectedDataSet);
    }

    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials().navigateToAdminPage();
    }
}
