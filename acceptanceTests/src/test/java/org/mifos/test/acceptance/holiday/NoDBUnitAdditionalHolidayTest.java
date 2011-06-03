package org.mifos.test.acceptance.holiday;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayConfirmationPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = { "holiday", "schedules", "acceptance", "ui", "no_db_unit" })
public class NoDBUnitAdditionalHolidayTest extends UiTestCaseBase {
    private AppLauncher appLauncher;

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    private LoanTestHelper loanTestHelper;

    private NavigationHelper navigationHelper;

    private DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();

        dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(
                selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void logOut() {
        new MifosPage(selenium).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    /*
     * loan creation for setup is stepping into questionnaire page when it shouldnt
     */
    @Test(enabled=true)
    public void createHolidayOnAMeetingWithRepaymentSameDay() throws Exception {
        //Given
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2041,1,1,13,0,0,0));
        // create loan paid on the 1st of every month and then create a holiday on 1st July
        LoanAccountPage page = createMonthlyLoan("2041");
        String loanId = page.getAccountId();
        page.navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("01-Apr-2041","01-May-2041","01-Jun-2041","01-Jul-2041","01-Aug-2041","02-Sep-2041","01-Oct-2041","01-Nov-2041","02-Dec-2041","01-Jan-2042");

        // create a holiday on 1st of july
        createHolidayOn1stJuly(CreateHolidayEntryPage.CreateHolidaySubmitParameters.SAME_DAY, "2041");

        runHolidayBatchJob();

        navigationHelper.navigateToLoanAccountPage(loanId).navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("01-Apr-2041","01-May-2041","01-Jun-2041","01-Jul-2041","01-Aug-2041","02-Sep-2041","01-Oct-2041","01-Nov-2041","02-Dec-2041","01-Jan-2042");
    }

    private void runHolidayBatchJob() throws SQLException {
        applicationDatabaseOperation.cleanBatchJobTables();
        List<String> jobsToRun = new ArrayList<String>();
        jobsToRun.add("ApplyHolidayChangesTaskJob");
        new BatchJobHelper(selenium).runSomeBatchJobs(jobsToRun);
    }

    private LoanAccountPage createMonthlyLoan(final String year) {

        // create a loan that has its repayment on the 1st of every month
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct("MonthlyClientFlatLoan1stOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1234.0");
        submitAccountParameters.setDd("01");
        submitAccountParameters.setMm("03");
        submitAccountParameters.setYy(year);

        LoanAccountPage page =  loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);

        return page;
    }

    private void createHolidayOn1stJuly(final String repaymentRule, final String year) {
        CreateHolidayEntryPage.CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();

        params.setName("Canada Day");
        params.setFromDateDD("1");
        params.setFromDateMM("07");
        params.setFromDateYYYY(year);
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



    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials().navigateToAdminPage();
    }
}
