/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.test.acceptance.loan.lsim;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.account.AccountStatus;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class CreateLSIMClientLoanAccountTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    private LoanProductTestHelper loanProductTestHelper;
    private String expectedDate;
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        applicationDatabaseOperation.updateLSIM(1);
        loanTestHelper = new LoanTestHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2010, 1, 22, 10, 55, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() throws SQLException {
        applicationDatabaseOperation.updateLSIM(0);
        (new MifosPage(selenium)).logout();
    }

    @Test(groups = {"loan", "acceptance", "ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-127
    public void newWeeklyLSIMClientLoanAccount() throws Exception {

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233171716380 Client1233171716380");
        searchParameters.setLoanProduct("WeeklyFlatLoanWithOneTimeFees");
        expectedDate = "29-Jan-2010";
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("9012.0");
        submitAccountParameters.setLsimFrequencyWeeks("on");
        submitAccountParameters.setLsimWeekFrequency("1");
        submitAccountParameters.setLsimWeekDay("Friday");

        createLSIMLoanAndCheckAmountAndInstallmentDate(searchParameters, submitAccountParameters, expectedDate);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void newMonthlyClientLoanAccountWithMeetingOnSpecificDayOfMonth() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly1");
        searchParameters.setLoanProduct("MonthlyClientFlatLoan1stOfMonth");
        expectedDate = "05-Feb-2010";
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1234.0");
        // create LSIM loan that has repayments on 5th of every month
        submitAccountParameters.setLsimFrequencyMonths("on");
        submitAccountParameters.setLsimMonthTypeDayOfMonth("on");
        submitAccountParameters.setLsimDayOfMonth("5");

        createLSIMLoanAndCheckAmountAndInstallmentDate(searchParameters, submitAccountParameters, expectedDate);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @Test(enabled=false)
    public void newMonthlyClientLoanAccountWithMeetingOnSameWeekAndWeekday() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Monthly3rdFriday");
        searchParameters.setLoanProduct("MonthlyClientFlatLoanThirdFridayOfMonth");
        expectedDate = "11-Mar-2010";

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("2765.0");
        // create LSIM loan that has repayments on 2nd Thursday of each month
        submitAccountParameters.setLsimFrequencyMonths("on");
        submitAccountParameters.setLsimMonthTypeNthWeekdayOfMonth("on");
        submitAccountParameters.setLsimMonthRank("Second");
        submitAccountParameters.setLsimWeekDay("Thursday");

        createLSIMLoanAndCheckAmountAndInstallmentDate(searchParameters, submitAccountParameters, expectedDate);
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-123
    public void createLoanAccountWithNonMeetingDatesForDisburseAndRepay() throws Exception {
        //Given
        setTime(2011, 02, 24);
        DefineNewLoanProductPage.SubmitFormParameters defineNewLoanProductformParameters = FormParametersHelper.getMonthlyLoanProductParameters();
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct(defineNewLoanProductformParameters.getOfferingName());
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setDd("24");
        submitAccountParameters.setMm("02");
        submitAccountParameters.setYy("2011");
        EditLoanAccountStatusParameters editLoanAccountStatusParameters = new EditLoanAccountStatusParameters();
        editLoanAccountStatusParameters.setStatus(AccountStatus.LOAN_APPROVED.getStatusText());
        editLoanAccountStatusParameters.setNote("activate account");
        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        disburseLoanParameters.setDisbursalDateDD("24");
        disburseLoanParameters.setDisbursalDateMM("02");
        disburseLoanParameters.setDisbursalDateYYYY("2011");
        disburseLoanParameters.setPaymentType(PaymentParameters.CASH);
        //When
        loanProductTestHelper.defineNewLoanProduct(defineNewLoanProductformParameters);
        //Then
        String loanId = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters).getAccountId();
        loanTestHelper.changeLoanAccountStatus(loanId, editLoanAccountStatusParameters);
        setTime(2011, 03, 24);
        loanTestHelper.disburseLoan(loanId, disburseLoanParameters);
        loanTestHelper.repayLoan(loanId);
    }

    private void setTime(int year, int monthOfYear, int dayOfMonth) throws UnsupportedEncodingException {

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime systemTime = new DateTime(year, monthOfYear, dayOfMonth, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemTime);
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-121
    public void createWeeklyLoanAccountWithNonMeetingDatesForDisburseAndRepay() throws Exception {
        //Given
        setTime(2011, 02, 23);

        //When
        DefineNewLoanProductPage.SubmitFormParameters defineNewLoanProductformParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        defineNewLoanProductformParameters.setOfferingName("ProdTest123");

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233171716380 Client1233171716380");
        searchParameters.setLoanProduct(defineNewLoanProductformParameters.getOfferingName());

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters = createSearchParameters("23","02","2011");

        EditLoanAccountStatusParameters editLoanAccountStatusParameters = new EditLoanAccountStatusParameters();
        editLoanAccountStatusParameters.setStatus(AccountStatus.LOAN_APPROVED.getStatusText());
        editLoanAccountStatusParameters.setNote("activate account");

        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        disburseLoanParameters=createDisubreseLoanParameters("24","02","2011");

        loanProductTestHelper.defineNewLoanProduct(defineNewLoanProductformParameters);
        String loanId = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters).getAccountId();
        loanTestHelper.changeLoanAccountStatus(loanId, editLoanAccountStatusParameters);

        //Then
        loanTestHelper.disburseLoanWithWrongParams(loanId, disburseLoanParameters,"Date of transaction can not be a future date.");
        disburseLoanParameters.setDisbursalDateDD("23");
        loanTestHelper.disburseLoan(loanId, disburseLoanParameters);
        //loanTestHelper.disburseLoan(loanId, disburseLoanParameters);
        loanTestHelper.repayLoan(loanId);
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-124
    public void VerifyGracePeriodEffectOnLoanSchedule() throws Exception{
        //Given
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setGracePeriodType(DefineNewLoanProductPage.SubmitFormParameters.PRINCIPAL_ONLY_GRACE);
        formParameters.setGracePeriodDuration("3");
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233266063395 Client1233266063395");
        searchParameters.setLoanProduct(formParameters.getOfferingName());

        //When / Then
        loanProductTestHelper
            .navigateToDefineNewLoanPangAndFillMandatoryFields(formParameters)
            .verifyVariableInstalmentOptionsDefaults()
            .checkConfigureVariableInstalmentsCheckbox()
            .submitAndGotoNewLoanProductPreviewPage()
            .submit();

        //Then
        loanTestHelper.createLoanAccount(searchParameters, new CreateLoanAccountSubmitParameters())
            .navigateToRepaymentSchedulePage()
            .verifySchedulePrincipalWithGrace(Integer.parseInt(formParameters.getGracePeriodDuration()));
    }

    private CreateLoanAccountSubmitParameters createSearchParameters(String d, String m, String y){
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setDd(d);
        submitAccountParameters.setMm(m);
        submitAccountParameters.setYy(y);
        return submitAccountParameters;
    }

    private DisburseLoanParameters createDisubreseLoanParameters(String d, String m, String y){
        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        disburseLoanParameters.setDisbursalDateDD(d);
        disburseLoanParameters.setDisbursalDateMM(m);
        disburseLoanParameters.setDisbursalDateYYYY(y);
        disburseLoanParameters.setPaymentType(PaymentParameters.CASH);
        return disburseLoanParameters;
    }
    
    private String createLSIMLoanAndCheckAmountAndInstallmentDate(CreateLoanAccountSearchParameters searchParameters,
                                                                  CreateLoanAccountSubmitParameters submitAccountParameters, String expectedDate) {

        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        loanAccountPage.verifyLoanAmount(submitAccountParameters.getAmount());
        String loanId = loanAccountPage.getAccountId();
        ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountPage.navigateToViewRepaymentSchedule();
        viewRepaymentSchedulePage.verifyFirstInstallmentDate(4, 2, expectedDate);
        return loanId;
    }
}
