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
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
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

import java.sql.SQLException;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class CreateLSIMClientLoanAccountTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    private LoanProductTestHelper loanProductTestHelper;
    private String expectedDate;

    private DateTime systemTime;

    //@Autowired
    //private DriverManagerDataSource dataSource;
    //@Autowired
    //private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    private DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        applicationDatabaseOperation.updateLSIM(1);
        loanTestHelper = new LoanTestHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        systemTime = new DateTime(2010,1,22,10,55,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemTime);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() throws SQLException {
        applicationDatabaseOperation.updateLSIM(0);
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-127
    public void newWeeklyLSIMClientLoanAccount() throws Exception {
        //Given
        systemTime = new DateTime(2010,1,15,10,55,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemTime);
        //When
        CustomPropertiesHelper customPropertiesHelper = new CustomPropertiesHelper(selenium);
        customPropertiesHelper.setBackDatedTransactionsAllowed("true");

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233171716380 Client1233171716380");
        searchParameters.setLoanProduct("WeeklyFlatLoanWithOneTimeFees");
        expectedDate = "29-Jan-2010";
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("9012.0");
        submitAccountParameters.setLsimFrequencyWeeks("on");
        submitAccountParameters.setLsimWeekFrequency("1");
        submitAccountParameters.setLsimWeekDay("Friday");
        //Then
        String loanId = createLSIMLoanAndCheckAmountAndInstallmentDate(searchParameters, submitAccountParameters, expectedDate);
        //When
        systemTime = new DateTime(2010,1,18,10,55,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemTime);
        //Then
        loanTestHelper.activateLoanAccount(loanId);
        //When
        systemTime = new DateTime(2010,1,29,10,55,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemTime);

        DisburseLoanParameters disburseParameters = new DisburseLoanParameters();
        disburseParameters.setDisbursalDateDD("22");
        disburseParameters.setDisbursalDateMM("01");
        disburseParameters.setDisbursalDateYYYY("2010");
        disburseParameters.setPaymentType(PaymentParameters.CASH);
        //Then
        loanTestHelper.disburseLoan(loanId, disburseParameters);
        //When
        PaymentParameters paymentParameters = new PaymentParameters();
        paymentParameters.setAmount("200.0");
        paymentParameters.setTransactionDateDD("23");
        paymentParameters.setTransactionDateMM("01");
        paymentParameters.setTransactionDateYYYY("2010");
        paymentParameters.setPaymentType(PaymentParameters.CASH);
        //Then
        loanTestHelper.applyPayment(loanId, paymentParameters);
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
    public void newMonthlyClientLoanAccountWithMeetingOnSameWeekAndWeekday() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mia Monthly3rdFriday");
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
    @Test(enabled=false) // TODO js - make it no_db_unit
    public void createLoanAccountWithNonMeetingDatesForDisburseAndRepay() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        systemTime = new DateTime(2011,02,24,12,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemTime);
        //initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_011_dbunit.xml", dataSource, selenium);
        DefineNewLoanProductPage.SubmitFormParameters defineNewLoanProductformParameters = FormParametersHelper.getMonthlyLoanProductParameters();
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct(defineNewLoanProductformParameters.getOfferingName());
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters = createSearchParameters("24","02","2011");
        EditLoanAccountStatusParameters editLoanAccountStatusParameters = new EditLoanAccountStatusParameters();
        editLoanAccountStatusParameters.setStatus(AccountStatus.LOAN_APPROVED.getStatusText());
        editLoanAccountStatusParameters.setNote("activate account");
        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        disburseLoanParameters=createDisubreseLoanParameters("23","02","2011");
        //When
        loanProductTestHelper.defineNewLoanProduct(defineNewLoanProductformParameters);
        //Then
        String loanId = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters).getAccountId();
        loanTestHelper.changeLoanAccountStatus(loanId, editLoanAccountStatusParameters);
        loanTestHelper.disburseLoan(loanId, disburseLoanParameters);
        loanTestHelper.repayLoan(loanId);
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-121
    @Test(enabled=false) // TODO js - make it no_db_unit
    public void createWeeklyLoanAccountWithNonMeetingDatesForDisburseAndRepay() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        systemTime = new DateTime(2011,02,23,12,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemTime);
        //initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);

        //When
        DefineNewLoanProductPage.SubmitFormParameters defineNewLoanProductformParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        defineNewLoanProductformParameters.setOfferingName("ProdTest123");

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1232993852651 Client1232993852651");
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
        ViewRepaymentSchedulePage viewRepaymentSchedulePage =loanAccountPage.navigateToViewRepaymentSchedule();
        viewRepaymentSchedulePage.verifyFirstInstallmentDate(4, 2, expectedDate);
        return loanId;
    }
}
