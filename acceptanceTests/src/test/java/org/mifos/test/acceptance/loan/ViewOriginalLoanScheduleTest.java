package org.mifos.test.acceptance.loan;


import org.joda.time.DateTime;
import org.mifos.test.acceptance.admin.FeeTestHelper;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import static org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage.CreateHolidaySubmitParameters;


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"loanproduct", "acceptance", "ui"})
public class ViewOriginalLoanScheduleTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    private static final String officeName = "test_office";
    private static final String userLoginName = "test_user";
    private static final String userName="test user";
    private static final String clientName = "test client";
    private LoanProductTestHelper loanProductTestHelper;
    private String loanProductName;
    private LoanTestHelper loanTestHelper;
    private DateTime systemDateTime;
    private NavigationHelper navigationHelper;
    String feeName = "loanWeeklyFee";
    boolean isSetUpDone = false;

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        if (isSetUpDone) {
            systemDateTime = new DateTime(2011, 10, 10, 10, 0, 0, 0);
            loanTestHelper.setApplicationTime(systemDateTime);
            return;
        }
//        // TODO: please ensure that the database contains Mrs Salutation!
//        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
//        // ---
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        loanTestHelper = new LoanTestHelper(selenium);
        loanTestHelper.setApplicationTime(systemDateTime);
        TestDataSetup dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        dataSetup.createBranch(OfficeParameters.BRANCH_OFFICE, officeName, "Off");
        dataSetup.createUser(userLoginName, userName, officeName);
        dataSetup.createClient(clientName, officeName, userName);
        systemDateTime = new DateTime(2011, 10, 10, 10, 0, 0, 0);
        loanTestHelper.setApplicationTime(systemDateTime);
        createHolidays(dataSetup);
        new FeeTestHelper(dataSetup).createPeriodicFee(feeName, FeesCreatePage.SubmitFormParameters.LOAN, FeesCreatePage.SubmitFormParameters.WEEKLY_FEE_RECURRENCE, 1, 100);
        isSetUpDone=true;
    }

    private void createHolidays(TestDataSetup dataSetup) throws SQLException {
        dataSetup.createHoliday(systemDateTime.plusDays(7), null, CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT); //17/10/2011
        dataSetup.createHoliday(systemDateTime.plusDays(14), null, CreateHolidaySubmitParameters.NEXT_WORKING_DAY);//24/10/2011
        dataSetup.createHoliday(systemDateTime.plusDays(21), null, CreateHolidaySubmitParameters.SAME_DAY);//31/10/2011
        dataSetup.createHoliday(systemDateTime.plusDays(15), null, CreateHolidaySubmitParameters.SAME_DAY); //25/10/2011
        dataSetup.createHoliday(systemDateTime.plusDays(22), null, CreateHolidaySubmitParameters.NEXT_WORKING_DAY);//01/11/2011
        dataSetup.createHoliday(systemDateTime.plusDays(36), null, CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);//15/11/2011
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyForFlatLoanEarlyDisbursal() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.FLAT;
        applicationDatabaseOperation.updateLSIM(0);
        createLoanProduct(interestType);
        String accountId = verifyLoanAccountOriginalSchedule(systemDateTime.plusDays(1), systemDateTime, OriginalScheduleData.FLAT_LOAN_SCHEDULE, true, systemDateTime.plusDays(5));
        applyChargesAndVerifySchedule(accountId, OriginalScheduleData.FLAT_LOAN_SCHEDULE);
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForVariableInstallmentLoanEarlyDisbursal() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE;
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(interestType);
        loanProductTestHelper.
                navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption("30","1","100").
                submitAndGotoNewLoanProductPreviewPage().submit();
        verifyLoanAccountOriginalSchedule(systemDateTime.plusDays(1), systemDateTime, OriginalScheduleData.VARIABLE_LOAN_EARLY_DISBURSAL_SCHEDULE, false, systemDateTime.plusDays(5));
    }

    @Test(enabled=false)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForVariableInstallmentLoanLateDisbursal() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE;
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(interestType);
        loanProductTestHelper.
                navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption("20","1","100").
                submitAndGotoNewLoanProductPreviewPage().submit();
        verifyLoanAccountOriginalSchedule(systemDateTime, systemDateTime.plusDays(1), OriginalScheduleData.VARIABLE_LOAN_LATE_DISBURSAL_SCHEDULE, false, systemDateTime.plusDays(15));
    }

    /**
     * FIXME - KEITHW - This test is disabled as its failing for case where loan when disbursed early with LSIM on seems to miss the nearest installment date but
     * I cannot replicate this manually
     */
    @Test(enabled=false)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForDecBalIntReCalcLoanEarlyDisbursalLSIMOn() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
        applicationDatabaseOperation.updateLSIM(1);
        createLoanProduct(interestType);
        String accountId = verifyLoanAccountOriginalSchedule(systemDateTime.plusDays(1), systemDateTime.plusDays(10), OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_ON, true, systemDateTime.plusDays(15));
        applyChargesAndVerifySchedule(accountId, OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_ON);
    }

    private void applyChargesAndVerifySchedule(String accountId, String[][] loanSchedule) {
        ChargeParameters feeParameters = new ChargeParameters();
        feeParameters.setAmount("10");
        feeParameters.setType(ChargeParameters.MISC_FEES);
        loanTestHelper.applyCharge(accountId, feeParameters);
        feeParameters.setType(ChargeParameters.MISC_PENALTY);
        loanTestHelper.applyCharge(accountId, feeParameters);
        verifyOriginalSchedule(loanSchedule);
    }
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForDecBalIntReCalcLoanEarlyDisbursalLSIMOff() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
        applicationDatabaseOperation.updateLSIM(0);
        createLoanProduct(interestType);
        String accountId = verifyLoanAccountOriginalSchedule(systemDateTime.plusDays(1), systemDateTime, OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_EARLY_DISBURSAL_SCHEDULE_OFF, true, systemDateTime.plusDays(15));
        applyChargesAndVerifySchedule(accountId, OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_EARLY_DISBURSAL_SCHEDULE_OFF);
    }
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForDecBalIntReCalcLoanLateDisbursalLSIMOff() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
        applicationDatabaseOperation.updateLSIM(0);
        createLoanProduct(interestType);
        String accountId = verifyLoanAccountOriginalSchedule(systemDateTime.plusDays(1), systemDateTime.plusDays(8), OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_OFF, true, systemDateTime.plusDays(15));
        applyChargesAndVerifySchedule(accountId, OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_OFF);
    }

    private String verifyLoanAccountOriginalSchedule(DateTime creationDisbursalDate, DateTime disbursalDate, String[][] tableOnOriginalInstallment, boolean needApplyFee, DateTime paymentDate) throws UnsupportedEncodingException {
        String accountId = createLoanAccount(creationDisbursalDate, disbursalDate, needApplyFee);
        verifyOriginalSchedule(tableOnOriginalInstallment);
        ChargeParameters feeParameters = new ChargeParameters();
        feeParameters.setAmount("10");
        feeParameters.setType(ChargeParameters.MISC_FEES);
        loanTestHelper.applyCharge(accountId, feeParameters);
        feeParameters.setType(ChargeParameters.MISC_PENALTY);
        loanTestHelper.applyCharge(accountId, feeParameters);

        verifyOriginalSchedule(tableOnOriginalInstallment);

        loanTestHelper.makePayment(paymentDate, "100");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        return accountId;
    }

    private String createLoanAccount(DateTime creationDisbursalDate, DateTime actualDisbursalDate, boolean needApplyFee) throws UnsupportedEncodingException {
        navigationHelper.navigateToHomePage();
        LoanAccountPage loanAccountPage = loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters()).
                setDisbursalDate(creationDisbursalDate).
                clickContinue().clickPreviewAndGoToReviewLoanAccountPage().submit().navigateToLoanAccountDetailsPage();
        if (needApplyFee) {
            ChargeParameters chargeParameters = new ChargeParameters();
            chargeParameters.setType(feeName);
            new LoanAccountPage(selenium).navigateToApplyCharge().applyFeeAndConfirm(chargeParameters);
        }
        String accountId = loanAccountPage.getAccountId();
        ChargeParameters feeParameters = new ChargeParameters();
        feeParameters.setAmount("10");
        feeParameters.setType(ChargeParameters.MISC_FEES);
        loanTestHelper.applyCharge(accountId, feeParameters);
        feeParameters.setType(ChargeParameters.MISC_PENALTY);
        loanTestHelper.applyCharge(accountId, feeParameters);
        loanTestHelper.approveLoan();
        loanTestHelper.disburseLoan(actualDisbursalDate);
        return accountId;
    }

    private LoanAccountPage verifyOriginalSchedule(String[][] tableOnOriginalInstallment) {
        return new LoanAccountPage(selenium).
                navigateToRepaymentSchedulePage().
                navigateToViewOriginalSchedulePage().
                verifyScheduleTable(tableOnOriginalInstallment).
                returnToRepaymentSchedule().navigateToLoanAccountPage();
    }

    private void createLoanProduct(int interestType) {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(interestType);
        loanProductTestHelper.
                navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters).
                submitAndGotoNewLoanProductPreviewPage().submit();
    }

    private DefineNewLoanProductPage.SubmitFormParameters defineLoanProductParameters(int interestType) {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setDefInstallments(String.valueOf(5));
        formParameters.setDefaultLoanAmount(String.valueOf(1000));
        formParameters.setInterestTypes(interestType);
        formParameters.setDefaultInterestRate(String.valueOf(20));
        loanProductName = formParameters.getOfferingName();
        return formParameters;
    }

    private CreateLoanAccountSearchParameters setLoanSearchParameters() {
        CreateLoanAccountSearchParameters accountSearchParameters = new CreateLoanAccountSearchParameters();
        accountSearchParameters.setLoanProduct(loanProductName);
        accountSearchParameters.setSearchString(clientName);
        return accountSearchParameters;
    }
}

