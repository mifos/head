package org.mifos.test.acceptance.loan;


import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.admin.FeeTestHelper;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage.CreateHolidaySubmitParameters;
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


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loanproduct", "acceptance", "ui", "no_db_unit"})
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
        new FeeTestHelper(dataSetup, new NavigationHelper(selenium)).createPeriodicFee(feeName, FeesCreatePage.SubmitFormParameters.LOAN, FeesCreatePage.SubmitFormParameters.WEEKLY_FEE_RECURRENCE, 1, 100);
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
        String[][] tableOnOriginalInstallment = OriginalScheduleData.FLAT_LOAN_SCHEDULE;
        createLoanAccount(systemDateTime.plusDays(1), systemDateTime, true);
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.makePayment(systemDateTime.plusDays(5), "100");
        verifyOriginalSchedule(tableOnOriginalInstallment);
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForVariableInstallmentLoanEarlyDisbursal() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE;
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(interestType);
        loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption("30","1","100").
                submitAndGotoNewLoanProductPreviewPage().submit();
        
        navigationHelper.navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters()).
                setDisbursalDate(systemDateTime.plusDays(1)).
                clickContinue().clickPreviewAndGoToReviewLoanAccountPage().submit().navigateToLoanAccountDetailsPage();
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        loanTestHelper.approveLoan();
        loanTestHelper.disburseLoan(systemDateTime);
        
        String[][] tableOnOriginalInstallment = OriginalScheduleData.VARIABLE_LOAN_EARLY_DISBURSAL_SCHEDULE;
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.makePayment(systemDateTime.plusDays(5), "100");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        applicationDatabaseOperation.updateLSIM(0);
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForVariableInstallmentLoanLateDisbursal() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE;
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(interestType);
        loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption("20","1","100").
                submitAndGotoNewLoanProductPreviewPage().submit();
        String[][] tableOnOriginalInstallment = OriginalScheduleData.VARIABLE_LOAN_LATE_DISBURSAL_SCHEDULE;
        createLoanAccount(systemDateTime, systemDateTime.plusDays(1), false);
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.makePayment(systemDateTime.plusDays(15), "100");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        applicationDatabaseOperation.updateLSIM(0);
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForDecBalIntReCalcLoanEarlyDisbursalLSIMOn() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
        applicationDatabaseOperation.updateLSIM(1);
        createLoanProduct(interestType);
        
        navigationHelper.navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters()).
                setDisbursalDate(systemDateTime.plusDays(1)).
                clickContinue().clickPreviewAndGoToReviewLoanAccountPage().submit().navigateToLoanAccountDetailsPage();
        
        ChargeParameters chargeParameters = new ChargeParameters();
        chargeParameters.setType(feeName);
        new LoanAccountPage(selenium).navigateToApplyCharge().applyFeeAndConfirm(chargeParameters);
        
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        loanTestHelper.approveLoan();
        loanTestHelper.disburseLoan(systemDateTime);
        
        String[][] tableOnOriginalInstallment = OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_EARLY_DISBURSAL_SCHEDULE_ON;
        verifyOriginalSchedule(tableOnOriginalInstallment);
        
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.makePayment(systemDateTime.plusDays(15), "100");
        verifyOriginalSchedule(tableOnOriginalInstallment);
//        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
//        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
//        verifyOriginalSchedule(tableOnOriginalInstallment);
        applicationDatabaseOperation.updateLSIM(0);
    }

    // FIXME - this test fails after merge
    @Test(enabled=false)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForDecBalIntReCalcLoanLateDisbursalLSIMOn() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
        applicationDatabaseOperation.updateLSIM(1);
        createLoanProduct(interestType);
        verifyLoanAccountOriginalSchedule(systemDateTime, systemDateTime.plusDays(1), OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_ON, true, systemDateTime.plusDays(15));
        applyChargesAndVerifySchedule(OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_ON);
        applicationDatabaseOperation.updateLSIM(0);
    }

    /**
     * FIXME - keithw
     */
    @Test(enabled=false, groups={"loanproduct"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForDecBalIntReCalcLoanEarlyDisbursalLSIMOff() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
        applicationDatabaseOperation.updateLSIM(0);
        createLoanProduct(interestType);
        verifyLoanAccountOriginalSchedule(systemDateTime.plusDays(1), systemDateTime, OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_EARLY_DISBURSAL_SCHEDULE_OFF, true, systemDateTime.plusDays(15));
        applyChargesAndVerifySchedule(OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_EARLY_DISBURSAL_SCHEDULE_OFF);
    }

    /**
     * FIXME - keithw
     */
    @Test(enabled=false, groups={"loanproduct"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForDecBalIntReCalcLoanLateDisbursalLSIMOff() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
        applicationDatabaseOperation.updateLSIM(0);
        createLoanProduct(interestType);
        verifyLoanAccountOriginalSchedule(systemDateTime.plusDays(1), systemDateTime.plusDays(8), OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_OFF, true, systemDateTime.plusDays(15));
        applyChargesAndVerifySchedule(OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_OFF);
    }

    private void verifyLoanAccountOriginalSchedule(DateTime creationDisbursalDate, DateTime disbursalDate, String[][] tableOnOriginalInstallment, boolean needApplyFee, DateTime paymentDate) throws UnsupportedEncodingException {
        createLoanAccount(creationDisbursalDate, disbursalDate, needApplyFee);
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.makePayment(paymentDate, "100");
        verifyOriginalSchedule(tableOnOriginalInstallment);
    }


    private void applyChargesAndVerifySchedule(String[][] loanSchedule) {
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        verifyOriginalSchedule(loanSchedule);
    }

    private void createLoanAccount(DateTime creationDisbursalDate, DateTime actualDisbursalDate, boolean needApplyFee) throws UnsupportedEncodingException {
        navigationHelper.navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters()).
                setDisbursalDate(creationDisbursalDate).
                clickContinue().clickPreviewAndGoToReviewLoanAccountPage().submit().navigateToLoanAccountDetailsPage();
        if (needApplyFee) {
            ChargeParameters chargeParameters = new ChargeParameters();
            chargeParameters.setType(feeName);
            new LoanAccountPage(selenium).navigateToApplyCharge().applyFeeAndConfirm(chargeParameters);
        }
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        loanTestHelper.approveLoan();
        loanTestHelper.disburseLoan(actualDisbursalDate);
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
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
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

