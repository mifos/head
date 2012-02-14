package org.mifos.test.acceptance.loan;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.admin.FeeTestHelper;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage.CreateHolidaySubmitParameters;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountPreviewPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountReviewInstallmentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
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
    public void verifyForFlatVariableInstallmentLoanEarlyDisbursal() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.FLAT;
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(interestType);
        loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption("20","1","100").
                submitAndGotoNewLoanProductPreviewPage().submit();
        String[][] tableOnOriginalInstallment = OriginalScheduleData.FLAT_VARIABLE_LOAN_EARLY_DISBURSAL_SCHEDULE;
        createLoanAccount(systemDateTime.plusDays(1), systemDateTime, false);
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.makePayment(systemDateTime.plusDays(5), "100");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        applicationDatabaseOperation.updateLSIM(0);
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-1163
    @Test(enabled=false) //blocked by http://mifosforge.jira.com/browse/MIFOS-5026 - ldomzalski
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForVariableInstallmentLoanEarlyDisbursal() throws Exception {
    	//Given
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE;
        DateTime disbursalDate = systemDateTime.plusDays(1);
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = defineLoanProductParameters(interestType);
        CreateLoanAccountSubmitParameters accountSubmitParameters = new CreateLoanAccountSubmitParameters();
        accountSubmitParameters.setAmount("1000.0");
        accountSubmitParameters.setInterestRate("20");
        accountSubmitParameters.setNumberOfInstallments("5");
        accountSubmitParameters.setDd("11");
        accountSubmitParameters.setMm("10");
        accountSubmitParameters.setYy("2011");
        String[] fees = {"fixedFeePerAmountAndInterest", "fixedFeePerInterest"};
        EditLoanAccountInformationParameters editAccountParameters = new EditLoanAccountInformationParameters();
        editAccountParameters.setGracePeriod("0");
        formParameters.addFee("fixedFeePerAmountAndInterest");
        formParameters.addFee("fixedFeePerInterest");
        
        //When
        DefineNewLoanProductPage defineNewLoanProductPage = loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                fillVariableInstalmentOption("30","1","100");
        defineNewLoanProductPage.submitWithErrors("fee cannot be applied to variable installment loan product");
        defineNewLoanProductPage.setInterestRateType(DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION);
        defineNewLoanProductPage.submitWithErrors("The selected interest type is invalid for variable installment loan product");
        defineNewLoanProductPage.setInterestRateType(interestType);
        defineNewLoanProductPage.submitAndGotoNewLoanProductPreviewPage().submit();
        
        List<String> errors = new ArrayList<String>();
        errors.add("fixedFeePerAmountAndInterest fee cannot be applied to loan with variable installments");
        errors.add("fixedFeePerInterest fee cannot be applied to loan with variable installments.");
        
        navigationHelper.navigateToHomePage();
        CreateLoanAccountReviewInstallmentPage createLoanAccountReviewInstallmentPage = loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters()).
                setDisbursalDate(disbursalDate).
                applyAdditionalFees(fees).
                submitWithErrors(errors).
                unselectAdditionalFees().
                clickContinue();
        createLoanAccountReviewInstallmentPage.isDueDatesEditable(Integer.parseInt(formParameters.getDefInstallments()));
        createLoanAccountReviewInstallmentPage.isTotalsEditable(Integer.parseInt(formParameters.getDefInstallments()));
        String total = createLoanAccountReviewInstallmentPage.getTotalForInstallment(1);
        createLoanAccountReviewInstallmentPage.setTotalForInstallment(1, "3");
        createLoanAccountReviewInstallmentPage.submitWithErrors("has total amount less than the sum of interest and fees");
        createLoanAccountReviewInstallmentPage.setTotalForInstallment(1, "5");
        createLoanAccountReviewInstallmentPage.submitWithErrors("has total amount less than the allowed value");
        createLoanAccountReviewInstallmentPage.setTotalForInstallment(1, total);
        createLoanAccountReviewInstallmentPage.validate();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2011, 9, 13);
        String prevDueDate = createLoanAccountReviewInstallmentPage.getDueDateForInstallment(1);
        createLoanAccountReviewInstallmentPage.typeInstallmentDueDateByPicker(1, calendar);
        createLoanAccountReviewInstallmentPage.setDueDateForInstallment(1, prevDueDate);
        prevDueDate = createLoanAccountReviewInstallmentPage.getDueDateForInstallment(5);
        createLoanAccountReviewInstallmentPage.setDueDateForInstallment(5, "07/11/11");
        createLoanAccountReviewInstallmentPage.submitWithErrors("Gap between the due dates of installment 5 and the previous installment is less than allowed");
        createLoanAccountReviewInstallmentPage.setDueDateForInstallment(5, prevDueDate);
        List<String> totals = createLoanAccountReviewInstallmentPage.getTotalsInstallments(Integer.parseInt(formParameters.getDefInstallments()));
        List<String> dueDates = createLoanAccountReviewInstallmentPage.getDueDatesInstallments(Integer.parseInt(formParameters.getDefInstallments()));
        CreateLoanAccountPreviewPage createLoanAccountPreviewPage =createLoanAccountReviewInstallmentPage
                .clickPreviewAndGoToReviewLoanAccountPage();
        createLoanAccountPreviewPage.verifyInstallmentsSchedule(totals, dueDates, Integer.parseInt(formParameters.getDefInstallments()));
        LoanAccountPage loanAccountPage = createLoanAccountPreviewPage.submit()
                .navigateToLoanAccountDetailsPage();
        loanAccountPage.verifyDisbursalDate(disbursalDate);
        ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountPage.navigateToRepaymentSchedulePage();
        viewRepaymentSchedulePage.verifyScheduleAndAmounts(totals, dueDates);
        viewRepaymentSchedulePage.navigateBack();
        EditLoanAccountInformationPage editLoanAccountInformationPage = loanAccountPage.navigateToEditAccountInformation();
        editLoanAccountInformationPage.verifyAccountParams(accountSubmitParameters, editAccountParameters);
        editLoanAccountInformationPage.navigateBack();
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
        //Then
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

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForDecBalIntReCalcLoanLateDisbursalLSIMOn() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
        applicationDatabaseOperation.updateLSIM(1);
        createLoanProduct(interestType);
        String[][] tableOnOriginalInstallment = OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_ON;
        createLoanAccount(systemDateTime, systemDateTime.plusDays(1), true);
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.makePayment(systemDateTime.plusDays(15), "100");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        
//        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
//        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
//        verifyOriginalSchedule(OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_ON);
        applicationDatabaseOperation.updateLSIM(0);
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForDecBalIntReCalcLoanEarlyDisbursalLSIMOff() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
        applicationDatabaseOperation.updateLSIM(0);
        createLoanProduct(interestType);
        String[][] tableOnOriginalInstallment = OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_EARLY_DISBURSAL_SCHEDULE_OFF;
        createLoanAccount(systemDateTime.plusDays(1), systemDateTime, true);
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.makePayment(systemDateTime.plusDays(15), "100");
        verifyOriginalSchedule(tableOnOriginalInstallment);
//        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
//        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
//        verifyOriginalSchedule(OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_EARLY_DISBURSAL_SCHEDULE_OFF);
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyForDecBalIntReCalcLoanLateDisbursalLSIMOff() throws Exception {
        int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
        applicationDatabaseOperation.updateLSIM(0);
        createLoanProduct(interestType);
        String[][] tableOnOriginalInstallment = OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_OFF;
        createLoanAccount(systemDateTime.plusDays(1), systemDateTime.plusDays(8), true);
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        loanTestHelper.makePayment(systemDateTime.plusDays(15), "100");
        verifyOriginalSchedule(tableOnOriginalInstallment);
        
//        loanTestHelper.applyCharge(ChargeParameters.MISC_FEES, "10");
//        loanTestHelper.applyCharge(ChargeParameters.MISC_PENALTY, "10");
//        verifyOriginalSchedule(OriginalScheduleData.DEC_BAL_INT_RECALC_LOAN_LATE_DISBURSAL_SCHEDULE_OFF);
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