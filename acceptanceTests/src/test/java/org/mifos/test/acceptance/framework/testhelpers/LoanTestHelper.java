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

package org.mifos.test.acceptance.framework.testhelpers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.client.ClientSearchResultsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.AccountActivityPage;
import org.mifos.test.acceptance.framework.loan.ApplyChargePage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentConfirmationPage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.ClosedAccountsPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountsEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountsSuccessPage;
import org.mifos.test.acceptance.framework.loan.CreateMultipleLoanAccountSelectParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanConfirmationPage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanPage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditAccountStatusConfirmationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.EditPreviewLoanAccountPage;
import org.mifos.test.acceptance.framework.loan.GLIMClient;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.PerformanceHistoryAtributes;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalEntryPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSchedulePreviewPage;
import org.mifos.test.acceptance.framework.loan.RepayLoanConfirmationPage;
import org.mifos.test.acceptance.framework.loan.RepayLoanPage;
import org.mifos.test.acceptance.framework.loan.RepayLoanParameters;
import org.mifos.test.acceptance.framework.loan.TransactionHistoryPage;
import org.mifos.test.acceptance.framework.loan.ViewInstallmentDetailsPage;
import org.mifos.test.acceptance.framework.loan.ViewLoanStatusHistoryPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.EditLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.ViewLoanProductsPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;

import com.thoughtworks.selenium.Selenium;

/**
 * Holds methods common to most loan tests.
 *
 */
public class LoanTestHelper {
    private final Selenium selenium;
    private final NavigationHelper navigationHelper;

    public LoanTestHelper(Selenium selenium) {
        this.selenium = selenium;
        this.navigationHelper = new NavigationHelper(selenium);
    }

    public LoanTestHelper(Selenium selenium, NavigationHelper helper) {
        this.selenium = selenium;
        this.navigationHelper = helper;
    }

    /**
     * Creates a loan account.
     * @param searchParameters Parameters to find the client/group that will be the owner of the account.
     * @param submitAccountParameters The parameters for the loan account.
     * @return LoanAccountPage
     */
    public LoanAccountPage createLoanAccount(CreateLoanAccountSearchParameters searchParameters,
                                             CreateLoanAccountSubmitParameters submitAccountParameters) {
        return navigateToLoanAccountEntryPage(searchParameters)
                .submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters)
                .navigateToLoanAccountDetailsPage(submitAccountParameters);
    }

    /**
     * Creates loan account with default paramteres.
     * @param searchParameters
     * @return
     */
    public LoanAccountPage createDefaultLoanAccount(CreateLoanAccountSearchParameters searchParameters) {
        return navigationHelper.navigateToClientsAndAccountsPage()
            .navigateToCreateLoanAccountUsingLeftMenu()
            .searchAndNavigateToCreateLoanAccountPage(searchParameters)
            .continuePreviewSubmitAndNavigateToDetailsPage();
    }

    /**
     * Creates a loan account.
     * @param loanSaveType : True - submit for approval, False - save for later
     * @return LoanAccountPage
     */
    public LoanAccountPage createLoanAccountForMultipleClientsInGroup(boolean loanSaveType){

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("MyGroup1233266297718");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

        CreateLoanAccountEntryPage loanAccountEntryPage = this.navigateToCreateLoanAccountEntryPage(searchParameters);

        List<GLIMClient> clients= new ArrayList<GLIMClient>();
        clients.add(new GLIMClient(0,"Stu1233266299995 Client1233266299995 \n Client Id: 0006-000000051", "9999.9", "0700-Marriage"));
        clients.add(new GLIMClient(1, "Stu1233266309851 Client1233266309851 \n Client Id: 0006-000000052", "99999.9", "1008-Hospital"));
        clients.add(new GLIMClient(2, "Stu1233266319760 Client1233266319760 \n Client Id: 0006-000000053", "99999.9", "1010-Education"));

        for (GLIMClient glimClient : clients) {
            loanAccountEntryPage.selectGLIMClients(glimClient.getClientNumber(), glimClient.getClientName(), glimClient.getLoanAmount(), glimClient.getLoanPurpose());
        }
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage;
        if(loanSaveType) {
            createLoanAccountConfirmationPage = loanAccountEntryPage.submitAndNavigateToGLIMLoanAccountConfirmationPage();
        } else {
            createLoanAccountConfirmationPage = loanAccountEntryPage.submitAndNavigateToGLIMLoanAccountConfirmationPageSaveForLaterButton();
        }
        LoanAccountPage loanAccountPage =  createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();

        if(loanSaveType) {
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

    /**
     * Creates a loan account.
     * @param searchParameters Parameters to find the client/group that will be the owner of the account.
     * @param submitAccountParameters The parameters for the loan account.
     * @param questionResponseParameters The parameters for the create loan question responses.
     * @return LoanAccountPage
     */
    public LoanAccountPage createLoanAccount(CreateLoanAccountSearchParameters searchParameters,
                                             CreateLoanAccountSubmitParameters submitAccountParameters, QuestionResponseParameters questionResponseParameters) {
        return navigateToLoanAccountEntryPage(searchParameters)
                .submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters, questionResponseParameters)
                .navigateToLoanAccountDetailsPage();
    }

    private CreateLoanAccountEntryPage navigateToLoanAccountEntryPage(CreateLoanAccountSearchParameters searchParameters) {
        CreateLoanAccountSearchPage createLoanAccountSearchPage = navigateToCreateLoanAccountSearchPage();
        createLoanAccountSearchPage.verifyPage();
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage
        .searchAndNavigateToCreateLoanAccountPage(searchParameters);
        createLoanAccountEntryPage.verifyPage();
        return createLoanAccountEntryPage;
    }

    /**
     * TODO: Create a helper for creating a GLIM loan account.
     */


    /**
     * Edits the loan account with id loanId and updates its settings with the ones in params.
     * @param loanId The account id.
     * @param accountSubmitParameters The create loan parameters.
     * @param editAccountParameters The edit loan parameters.
     * @return edit preview loan account page
     */
    public LoanAccountPage changeLoanAccountInformation(String loanId, CreateLoanAccountSubmitParameters accountSubmitParameters, EditLoanAccountInformationParameters editAccountParameters) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        EditLoanAccountInformationPage editLoanAccountInformationPage = loanAccountPage.navigateToEditAccountInformation();
        EditPreviewLoanAccountPage editPreviewLoanAccountPage = editLoanAccountInformationPage.editAccountParams(accountSubmitParameters, editAccountParameters).submitAndNavigateToAccountInformationPreviewPage();
        loanAccountPage = editPreviewLoanAccountPage.submitAndNavigateToLoanAccountPage();
        loanAccountPage.verifyLoanDetails(accountSubmitParameters, editAccountParameters);
        return loanAccountPage;
    }

    public EditLoanAccountInformationPage changeLoanAccountInformationWithErrors(String loanId, CreateLoanAccountSubmitParameters accountSubmitParameters, EditLoanAccountInformationParameters editAccountParameters) {
        return navigationHelper.navigateToLoanAccountPage(loanId).
                navigateToEditAccountInformation().
                editAccountParams(accountSubmitParameters, editAccountParameters).
                submitWithErrors();
    }

    /**
     * Edits the loan account with id loanId and updates its settings with the ones in params.
     * @param loanId The account id.
     * @param params The status parameters.
     */
    public void changeLoanAccountStatus(String loanId, EditLoanAccountStatusParameters params) {
        changeLoanAccountStatusProvidingQuestionGroupResponses(loanId, params, null);
    }

    public void changeLoanAccountStatusProvidingQuestionGroupResponses(String loanId, EditLoanAccountStatusParameters params, QuestionResponseParameters responseParameters) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);

        EditLoanAccountStatusPage editAccountStatusPage = loanAccountPage.navigateToEditAccountStatus();

        EditAccountStatusConfirmationPage editAccountStatusConfirmationPage = editAccountStatusPage.submitAndNavigateToNextPage(params);

        if (responseParameters != null) {
            populateQuestionGroupResponses(responseParameters);
        }

        loanAccountPage = editAccountStatusConfirmationPage.submitAndNavigateToLoanAccountPage();
        loanAccountPage.verifyStatus(params.getStatus(), params.getCancelReason());
    }

    public void verifyLastEntryInStatusHistory(String loanId, String oldStatus, String newStatus) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        ViewLoanStatusHistoryPage viewLoanStatusHistoryPage = loanAccountPage.navigateToViewLoanStatusHistoryPage();
        viewLoanStatusHistoryPage.verifyLastEntryInStatusHistory(oldStatus, newStatus);
    }

    private void populateQuestionGroupResponses(QuestionResponseParameters responseParameters) {
        QuestionResponsePage responsePage = new QuestionResponsePage(selenium);
        responsePage.populateAnswers(responseParameters);
        responsePage.navigateToNextPage();
    }


    /**
     * Disburses the loan with id <tt>loanId</tt>.
     * @param loanId The system/global id of the loan that'll be disbursed.
     * @param disburseParameters The disbursal parameters.
     * @return The loan account page of the loan account with id loanId.
     */
    public LoanAccountPage disburseLoan(String loanId, DisburseLoanParameters disburseParameters) {
        DisburseLoanPage disburseLoanPage = prepareToDisburseLoan(loanId);

        DisburseLoanConfirmationPage disburseLoanConfirmationPage = disburseLoanPage.submitAndNavigateToDisburseLoanConfirmationPage(disburseParameters);

        LoanAccountPage loanAccountPage = disburseLoanConfirmationPage.submitAndNavigateToLoanAccountPage();
        loanAccountPage.verifyStatus(LoanAccountPage.ACTIVE);
        return loanAccountPage;
    }

    public void editLoanProduct(String loanProduct, boolean interestWaiver) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewLoanProductsPage viewLoanProducts = adminPage.navigateToViewLoanProducts();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails(loanProduct);
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setInterestWaiver(interestWaiver);
        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.submitInterestWaiverChanges(formParameters);
        editLoanProductPreviewPage.submit();
    }

    public void editLoanProductIncludeInLoanCounter(String loanProduct, boolean includeInLoanCounter) {
        EditLoanProductPage editLoanProductPage = navigationHelper.navigateToAdminPage().
                navigateToViewLoanProducts().
                viewLoanProductDetails(loanProduct).
                editLoanProduct();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setIncludeInLoanCounter(includeInLoanCounter);
        editLoanProductPage.submitIncludeInLoanCounter(formParameters).submit();
    }

    public void editLoanProduct(String loanProduct, String... questionGroup) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewLoanProductsPage viewLoanProducts = adminPage.navigateToViewLoanProducts();
        LoanProductDetailsPage loanProductDetailsPage = viewLoanProducts.viewLoanProductDetails(loanProduct);
        EditLoanProductPage editLoanProductPage = loanProductDetailsPage.editLoanProduct();
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setQuestionGroups(Arrays.asList(questionGroup));
        EditLoanProductPreviewPage editLoanProductPreviewPage = editLoanProductPage.submitQuestionGroupChanges(formParameters);
        editLoanProductPreviewPage.submit();
    }

    public DisburseLoanPage prepareToDisburseLoan(String loanId) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        return loanAccountPage.navigateToDisburseLoan();
    }

    /**
     * Applies a charge to the loan account with id <tt>loanId</tt>.
     * @param loanId The account id.
     * @param params The charge parameters (amount and type).
     * @return The loan account page for the loan account.
     */
    public LoanAccountPage applyCharge(String loanId, ChargeParameters params) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        ApplyChargePage applyChargePage = loanAccountPage.navigateToApplyCharge();
        loanAccountPage = applyChargePage.submitAndNavigateToApplyChargeConfirmationPage(params);

        return loanAccountPage;
    }

    /**
     * Repay loan account with id <tt>loanId</tt>.
     * @param loanId The account id.
     * @return The loan account page for the loan account.
     */
    public LoanAccountPage repayLoan(String loanId) {
        RepayLoanParameters params = new RepayLoanParameters();
        params.setModeOfRepayment(RepayLoanParameters.CASH);

        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);

        RepayLoanPage repayLoanPage = loanAccountPage.navigateToRepayLoan();
        RepayLoanConfirmationPage repayLoanConfirmationPage = repayLoanPage.submitAndNavigateToRepayLoanConfirmationPage(params);
        loanAccountPage = repayLoanConfirmationPage.submitAndNavigateToLoanAccountDetailsPage();
        loanAccountPage.verifyStatus(LoanAccountPage.CLOSED);

        return loanAccountPage;
    }


    /**
     * Applies a payment to the loan account with id <tt>loanId</tt>.
     * @param loanId The account id.
     * @param paymentParams The payment parameters.
     * @return The loan account page for the loan account.
     */
    public LoanAccountPage applyPayment(String loanId, PaymentParameters paymentParams) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);

        ApplyPaymentPage applyPaymentPage = loanAccountPage.navigateToApplyPayment();
        ApplyPaymentConfirmationPage applyPaymentConfirmationPage = applyPaymentPage.submitAndNavigateToApplyPaymentConfirmationPage(paymentParams);
        loanAccountPage = applyPaymentConfirmationPage.submitAndNavigateToLoanAccountDetailsPage();

        AccountActivityPage accountActivityPage = loanAccountPage.navigateToAccountActivityPage();
        accountActivityPage.verifyLastTotalPaid(paymentParams.getAmount());
        accountActivityPage.navigateBack();

        return loanAccountPage;
    }

    /**
     * Waive the fee associated with the loan account with id <tt>loanId</tt>.
     * @param loanId The loan account id.
     */
    public void waiveFee(String loanId) {
        navigationHelper.navigateToLoanAccountPage(loanId).
                navigateToViewNextInstallmentDetails().
                waiveFee();
    }

    /**
     * Waive the penalty associated with the loan account with id <tt>loanId</tt>.
     * @param loanId The loan account id.
     */
    public void waivePenalty(String loanId) {
        navigationHelper.navigateToLoanAccountPage(loanId).
                navigateToViewNextInstallmentDetails().
                waivePenalty();
    }

    /**
     * Redoes the loan disbursal.
     * @param clientName The name of the client.
     * @param loanProduct The name of the loan product.
     * @param paramsPastDate The parameters for the loan disbursal (past date).
     * @param paramsCurrentDate The parameters for the loan disbursal (current date).
     * @param amountPaid The amount typed in second pay row. Used to pay whole loan.
     * @return LoanAccountPage
     */
    public LoanAccountPage redoLoanDisbursal(String clientName, String loanProduct, RedoLoanDisbursalParameters paramsPastDate, RedoLoanDisbursalParameters paramsCurrentDate, int amountPaid, boolean testForm) {
        RedoLoanDisbursalEntryPage dataEntryPage = navigationHelper
            .navigateToAdminPage()
            .navigateToRedoLoanDisbursal()
            .searchAndNavigateToRedoLoanDisbursalPage(clientName)
            .navigateToRedoLoanDisbursalChooseLoanProductPage(clientName)
            .submitAndNavigateToRedoLoanDisbursalEntryPage(loanProduct);

        if(paramsCurrentDate != null) { // tests current or future date if need to.
            dataEntryPage = dataEntryPage.submitInvalidDataAndReloadPageWithInputError(paramsCurrentDate);
            dataEntryPage.verifyFutureDateInputError();
        }
        if(testForm) {  // tests clear form
            RedoLoanDisbursalParameters clearedParameters = RedoLoanDisbursalParameters.createObjectWithClearedParameters();
            dataEntryPage = dataEntryPage.submitInvalidDataAndReloadPageWithInputError(clearedParameters);
            dataEntryPage.verifyAllFormErrors();
        }

        RedoLoanDisbursalSchedulePreviewPage schedulePreviewPage = dataEntryPage.submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(paramsPastDate);
        if(amountPaid != 0) { // used to pay grater amount than default (ex. for closing loan)
            schedulePreviewPage.typeAmountPaid(amountPaid, 2);
        }

        return schedulePreviewPage
            .submitAndNavigateToRedoLoanDisbursalPreviewPage()
            .submitAndNavigateToLoanAccountConfirmationPage()
            .navigateToLoanAccountDetailsPage();
    }

    public LoanAccountPage redoLoanDisbursalWithGLIMandLSIM(String clientName, String loanProduct, RedoLoanDisbursalParameters paramsPastDate) {
        return navigationHelper
            .navigateToAdminPage()
            .navigateToRedoLoanDisbursal()
            .searchAndNavigateToRedoLoanDisbursalPage(clientName)
            .navigateToRedoLoanDisbursalChooseLoanProductPage(clientName)
            .submitAndNavigateToRedoLoanDisbursalEntryPage(loanProduct)
            .submitWithGLIMandLSIPAndNavigateToPreviewPage(paramsPastDate)
            .submitAndNavigateToRedoLoanDisbursalPreviewPage()
            .submitAndNavigateToLoanAccountConfirmationPage()
            .navigateToLoanAccountDetailsPage();
    }

    /**
     *
     * Reverses the loan disbursal account
     * @param accountID the id of loan account that should be reversed
     */
    public MifosPage reverseLoanDisbursal(String accountID, String clientID, boolean isGroup, String resultClickLink) {
        ClientSearchResultsPage clientSearchResultsPage = navigationHelper
            .navigateToAdminPage()
            .navigateToUndoLoanDisbursal()
            .searchAndNavigateToUndoLoanDisbursalPage(accountID)
            .submitAndNavigateToUndoLoanDisbursalConfirmationPage("test reverse loan disembursal note")
            .submitAndNavigateToAdminPage()
            .navigateToClientsAndAccountsUsingHeaderTab()
            .searchForClient(clientID);

        MifosPage searchResultsPage;
        if(isGroup) {
            searchResultsPage = clientSearchResultsPage.navigateToGroupSearchResult(resultClickLink);
        }
        else {
            searchResultsPage = clientSearchResultsPage.navigateToSearchResult(resultClickLink);
        }
        return searchResultsPage;
    }

    /**
     * Small verification of reversed loan.
     * @param closedAccountPage
     * @param loanID
     */
    public void verifyHistoryAndSummaryReversedLoan(ClosedAccountsPage closedAccountPage, String loanID) {
        verifyHistoryAndSummaryReversedLoan(closedAccountPage, loanID, null, null, null);
    }

    /**
     * Extended verification of reversed loan.
     * @param closedAccountPage
     * @param loanID
     * @param totalOriginalLoan
     * @param totalAmountPaid
     * @param totalLoanBalance
     */
    public void verifyHistoryAndSummaryReversedLoan(ClosedAccountsPage closedAccountPage, String loanID, String totalOriginalLoan, String totalAmountPaid, String totalLoanBalance) {
        LoanAccountPage loanAccountPage = closedAccountPage.verifyAndNavigateToOneClosedLoan(loanID);
        loanAccountPage.verifyStatus(EditLoanAccountStatusParameters.CANCEL, EditLoanAccountStatusParameters.CANCEL_REASON_LOAN_REVERSAL);
        if(totalOriginalLoan != null) {
            loanAccountPage.verifyTotalOriginalLoan(totalOriginalLoan);
            loanAccountPage.verifyTotalAmountPaid(totalAmountPaid);
            loanAccountPage.verifyLoanTotalBalance(totalLoanBalance);
            loanAccountPage.verifyClosedLoanPerformanceHistory();
            TransactionHistoryPage transactionHistoryPage = loanAccountPage.navigateToTransactionHistory();
            transactionHistoryPage.verifyTransactionHistory(0, 0, 4);
        }
    }

    public LoanAccountPage navigateToLoanAccountPage(CreateLoanAccountSearchParameters searchParams) {
        String searchString = searchParams.getSearchString();

        LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        SearchResultsPage searchResultsPage = homePage.search(searchString);

        return searchResultsPage.navigateToLoanAccountDetailPage(searchString);
    }

    private CreateLoanAccountSearchPage navigateToCreateLoanAccountSearchPage() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        return clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
    }

    public CreateLoanAccountEntryPage navigateToCreateLoanAccountEntryPage(CreateLoanAccountSearchParameters searchParameters) {
        return navigateToLoanAccountEntryPage(searchParameters);
    }

    public CreateLoanAccountEntryPage navigateToCreateLoanAccountEntryPageWithoutLogout(CreateLoanAccountSearchParameters searchParameters) {
        ClientsAndAccountsHomepage clientsAndAccountsPage = new HomePage(selenium).navigateToClientsAndAccountsUsingHeaderTab();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
        createLoanAccountSearchPage.verifyPage();
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage
        .searchAndNavigateToCreateLoanAccountPage(searchParameters);
        createLoanAccountEntryPage.verifyPage();
        return createLoanAccountEntryPage;
    }

    public DisburseLoanPage prepareToDisburseLoanWithoutLogout(HomePage homePage, String loanId) {
        SearchResultsPage searchResultsPage = homePage.search(loanId);
        searchResultsPage.verifyPage();
        LoanAccountPage loanAccountPage = searchResultsPage.navigateToLoanAccountDetailPage(loanId);
        return loanAccountPage.navigateToDisburseLoan();

    }

    public CreateLoanAccountEntryPage navigateToCreateLoanAccountEntryPageWithoutLogout(String clientName, String loanProductName) {
        return navigateToCreateLoanAccountEntryPageWithoutLogout(setLoanSearchParameters(clientName,loanProductName));
    }

    public CreateLoanAccountSearchParameters setLoanSearchParameters(String clientName, String loanProductName) {
        CreateLoanAccountSearchParameters accountSearchParameters = new CreateLoanAccountSearchParameters();
        accountSearchParameters.setSearchString(clientName);
        accountSearchParameters.setLoanProduct(loanProductName);
        return accountSearchParameters;
    }


    public AbstractPage setApplicationTime(DateTime systemDateTime) throws UnsupportedEncodingException {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemDateTime);
        return new AbstractPage(selenium);
    }

    public PaymentParameters setPaymentParams(String amount, ReadableInstant paymentDate) {
        String dd = DateTimeFormat.forPattern("dd").print(paymentDate);
        String mm = DateTimeFormat.forPattern("MM").print(paymentDate);
        String yyyy = DateTimeFormat.forPattern("yyyy").print(paymentDate);

        PaymentParameters paymentParameters = new PaymentParameters();
        paymentParameters.setAmount(amount);
        paymentParameters.setTransactionDateDD(dd);
        paymentParameters.setTransactionDateMM(mm);
        paymentParameters.setTransactionDateYYYY(yyyy);
        paymentParameters.setPaymentType(PaymentParameters.CASH);
        return paymentParameters;
    }

    public DisburseLoanParameters setDisbursalParams(ReadableInstant validDisbursalDate) {
        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        String dd = DateTimeFormat.forPattern("dd").print(validDisbursalDate);
        String mm = DateTimeFormat.forPattern("MM").print(validDisbursalDate);
        String yyyy = DateTimeFormat.forPattern("yyyy").print(validDisbursalDate);

        disburseLoanParameters.setDisbursalDateDD(dd);
        disburseLoanParameters.setDisbursalDateMM(mm);
        disburseLoanParameters.setDisbursalDateYYYY(yyyy);
        disburseLoanParameters.setPaymentType(DisburseLoanParameters.CASH);
        return disburseLoanParameters;
    }

    public EditLoanAccountStatusParameters setApprovedStatusParameters() {
        EditLoanAccountStatusParameters loanAccountStatusParameters = new EditLoanAccountStatusParameters();
        loanAccountStatusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        loanAccountStatusParameters.setNote("test notes");
        return loanAccountStatusParameters;
    }

    public LoanAccountPage approveLoan(){
        return new LoanAccountPage(selenium).
                navigateToEditAccountStatus().
                submitAndNavigateToNextPage(setApprovedStatusParameters()).
                submitAndNavigateToLoanAccountPage();
    }

    public void verifyTransactionHistory(String loanId, Double paymentAmount){
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        TransactionHistoryPage transactionHistoryPage = loanAccountPage.navigateToTransactionHistory();

        transactionHistoryPage.verifyTransactionHistory(paymentAmount, 1, 217);
    }

    public LoanAccountPage createTwoLoanAccountsWithMixedRestricedPoducts(CreateLoanAccountSearchParameters searchParams1, CreateLoanAccountSearchParameters searchParams2, DisburseLoanParameters disburseParams) {
        LoanAccountPage loanAccountPage = navigationHelper
            .navigateToAdminPage()
            .navigateToDefineProductMix()
            .createOneMixAndNavigateToClientsAndAccounts(searchParams1.getLoanProduct(), searchParams2.getLoanProduct())
            .navigateToCreateLoanAccountUsingLeftMenu()
            .searchAndNavigateToCreateLoanAccountPage(searchParams1)
            .continuePreviewSubmitAndNavigateToDetailsPage();
        loanAccountPage.changeAccountStatusToAccepted();
        loanAccountPage.navigateToDisburseLoan()
            .submitAndNavigateToDisburseLoanConfirmationPage(disburseParams)
            .submitAndNavigateToLoanAccountPage();

        // create second account and try to disburse
        return loanAccountPage.navigateToClientsAndAccountsUsingHeaderTab()
            .navigateToCreateLoanAccountUsingLeftMenu()
            .searchAndNavigateToCreateLoanAccountPage(searchParams2)
            .continuePreviewSubmitAndNavigateToDetailsPage()
            .changeAccountStatusToAccepted()
            .tryNavigatingToDisburseLoanWithError();
    }

    /**
     * Creates multiple accounts and navigates to creation success page.
     * Must be on Clients And Accounts page.
     * @param multipleAccParameters
     * @param clients
     * @param loanPurpose
     * @return
     */
    public CreateLoanAccountsSuccessPage createMultipleLoanAccounts(CreateMultipleLoanAccountSelectParameters multipleAccParameters, String[] clients, String loanPurpose) {
        ClientsAndAccountsHomepage clientsAndAccountsHomepage = new ClientsAndAccountsHomepage(selenium);
        CreateLoanAccountsEntryPage createLoanAccountsEntryPage = clientsAndAccountsHomepage
            .navigateToCreateMultipleLoanAccountsUsingLeftMenu()
            .searchAndNavigateToCreateMultipleLoanAccountsEntryPage(multipleAccParameters);

        for(int i = 0; i < clients.length; i++) {
            createLoanAccountsEntryPage.selectClients(i, clients[i]);
            createLoanAccountsEntryPage.updateLoanPurposeForClient(i, loanPurpose);
        }

        return createLoanAccountsEntryPage.submitAndNavigateToCreateMultipleLoanAccountsSuccessPage();
    }

    public CreateLoanAccountsSuccessPage createMultipleLoanAccountsWithMixedRestricedPoducts(CreateMultipleLoanAccountSelectParameters multipleAccParameters1, CreateMultipleLoanAccountSelectParameters multipleAccParameters2, DisburseLoanParameters disburseParams, String[] clients) {
        navigationHelper.navigateToAdminPage()
            .navigateToDefineProductMix()
            .createOneMixAndNavigateToClientsAndAccounts(multipleAccParameters1.getLoanProduct(), multipleAccParameters2.getLoanProduct());

        CreateLoanAccountsSuccessPage createLoanAccountsSuccessPage = createMultipleLoanAccounts(multipleAccParameters1, clients, "0000-Animal Husbandry");
        List<String> accountNumbers = createLoanAccountsSuccessPage.verifyAndGetLoanAccountNumbers(clients.length);
        LoanAccountPage loanAccountPage = createLoanAccountsSuccessPage.selectLoansAndNavigateToLoanAccountPage(0);
        for(int i = 0; i < accountNumbers.size(); i++) {
            if(i > 0) {
                loanAccountPage = loanAccountPage.navigateToClientsAndAccountsUsingHeaderTab()
                    .searchForClient(accountNumbers.get(i))
                    .navigateToLoanAccountSearchResult("Account # "+accountNumbers.get(i));
            }
            loanAccountPage.changeAccountStatusToAccepted();
            loanAccountPage.navigateToDisburseLoan()
                .submitAndNavigateToDisburseLoanConfirmationPage(disburseParams)
                .submitAndNavigateToLoanAccountPage();
        }
        loanAccountPage.navigateToClientsAndAccountsUsingHeaderTab();
        return createMultipleLoanAccounts(multipleAccParameters2, clients, "0000-Animal Husbandry");
    }

    public void verifyPerformenceHistory(String clientName, PerformanceHistoryAtributes performanceHistoryAtributes){
        ClientViewDetailsPage clientViewDetailsPage = navigationHelper.navigateToClientViewDetailsPage(clientName);
        clientViewDetailsPage.verifyPerformanceHistory(performanceHistoryAtributes);
    }

    /**
     * Apply one charge to account.
     * Need to be on LoanAccountPage.
     * @param chargeParameters
     * @return
     */
    public LoanAccountPage applyOneChargeOnLoanAccount(ChargeParameters chargeParameters) {
        LoanAccountPage loanAccountPage = new LoanAccountPage(selenium);
        return loanAccountPage.navigateToApplyCharge()
                .submitAndNavigateToApplyChargeConfirmationPage(chargeParameters);
    }

    public LoanAccountPage createProductAndThenAccount(DefineNewLoanProductPage.SubmitFormParameters productParams, CreateLoanAccountSearchParameters searchParams, DisburseLoanParameters disburseParams) {
        DefineNewLoanProductPage defineNewLoanProductPage = navigationHelper
            .navigateToAdminPage()
            .navigateToDefineLoanProduct();
        defineNewLoanProductPage.fillLoanParameters(productParams);

        return defineNewLoanProductPage
            .submitAndGotoNewLoanProductPreviewPage()
            .submit()
            .navigateToClientsAndAccountsUsingHeaderTab()
            .navigateToCreateLoanAccountUsingLeftMenu()
            .searchAndNavigateToCreateLoanAccountPage(searchParams)
            .continuePreviewSubmitAndNavigateToDetailsPage()
            .changeAccountStatusToAccepted()
            .navigateToDisburseLoan()
            .submitAndNavigateToDisburseLoanConfirmationPage(disburseParams)
            .submitAndNavigateToLoanAccountPage();
    }

    /**
     * Method for creating default loan account.
     * Doesn't matter on which page you currently are.
     * Must be logged in.
     */
    public LoanAccountPage createAndActivateDefaultLoanAccount(CreateLoanAccountSearchParameters searchParams) {
        MifosPage mifosPage = new MifosPage(selenium);
        return mifosPage.navigateToClientsAndAccountsPageUsingHeaderTab()
            .navigateToCreateLoanAccountUsingLeftMenu()
            .searchAndNavigateToCreateLoanAccountPage(searchParams)
            .continuePreviewSubmitAndNavigateToDetailsPage()
            .changeAccountStatusToAccepted();
    }

    public LoanAccountPage createActivateAndDisburseDefaultLoanAccount(CreateLoanAccountSearchParameters searchParams, DisburseLoanParameters disburseParams) {
        return createAndActivateDefaultLoanAccount(searchParams)
                .navigateToDisburseLoan()
                .submitAndNavigateToDisburseLoanConfirmationPage(disburseParams)
                .submitAndNavigateToLoanAccountPage();
    }

    public LoanAccountPage createWithVerificationAndActivationLoanAccount(CreateLoanAccountSearchParameters searchParams, String[] amounts, String[] interestRate, String[] installments) {
        CreateLoanAccountEntryPage createLoanAccountEntryPage = navigationHelper
                .navigateToClientsAndAccountsPage()
                .navigateToCreateLoanAccountUsingLeftMenu()
                .searchAndNavigateToCreateLoanAccountPage(searchParams);
        if(amounts != null) {
            createLoanAccountEntryPage.verifyAllowedAmounts(amounts[0], amounts[1], amounts[2]);
        }
        if(interestRate != null) {
            createLoanAccountEntryPage.verifyAllowedInterestRate(interestRate[0], interestRate[1], interestRate[2]);
        }
        if(installments != null) {
            createLoanAccountEntryPage.verifyAllowedInstallments(installments[0], installments[1], installments[2]);
        }
        return createLoanAccountEntryPage
            .continuePreviewSubmitAndNavigateToDetailsPage()
            .changeAccountStatusToAccepted();
    }

    public LoanProductDetailsPage defineNewLoanProduct(DefineNewLoanProductPage.SubmitFormParameters productParams) {
        DefineNewLoanProductPage defineNewLoanProductPage = navigationHelper
            .navigateToAdminPage()
            .navigateToDefineLoanProduct();
        defineNewLoanProductPage.fillLoanParameters(productParams);

        return defineNewLoanProductPage
            .submitAndGotoNewLoanProductPreviewPage()
            .submit()
            .navigateToViewLoanDetailsPage();
    }

    public AdminPage loginAndNavigateToAdminPage() {
        return navigationHelper.navigateToAdminPage();
    }

    public RepayLoanParameters setRepaymentParameters() {
        RepayLoanParameters repayLoanParameters = new RepayLoanParameters();
        repayLoanParameters.setModeOfRepayment(RepayLoanParameters.CASH);
        repayLoanParameters.setReceiptDateDD("");
        return repayLoanParameters;
    }

    public LoanAccountPage repayLoan(DateTime repaymentDate) throws UnsupportedEncodingException {
        setApplicationTime(repaymentDate).navigateBack();
        RepayLoanParameters params = setRepaymentParameters();
        return new LoanAccountPage(selenium).navigateToRepayLoan().
                submitAndNavigateToRepayLoanConfirmationPage(params).
                submitAndNavigateToLoanAccountDetailsPage();
    }

    public LoanAccountPage makePayment(DateTime paymentDate, String paymentAmount) throws UnsupportedEncodingException {
        PaymentParameters paymentParameters =setPaymentParams(paymentAmount, paymentDate);
        setApplicationTime(paymentDate).navigateBack();
        LoanAccountPage loanAccountPage = new LoanAccountPage(selenium).navigateToApplyPayment().
                submitAndNavigateToApplyPaymentConfirmationPage(paymentParameters).
                submitAndNavigateToLoanAccountDetailsPage();
        AccountActivityPage accountActivityPage = loanAccountPage.navigateToAccountActivityPage();
        accountActivityPage.verifyLastTotalPaid(paymentAmount);
        accountActivityPage.navigateBack();
        return loanAccountPage;
    }

    public void disburseLoan(DateTime disbursalDate) throws UnsupportedEncodingException {
        setApplicationTime(disbursalDate).navigateBack();
        DisburseLoanParameters disburseLoanParameters = setDisbursalParams(disbursalDate);
        LoanAccountPage loanAccountPage = new LoanAccountPage(selenium).navigateToDisburseLoan().
                submitAndNavigateToDisburseLoanConfirmationPage(disburseLoanParameters).
                submitAndNavigateToLoanAccountPage();
        loanAccountPage.verifyStatus(LoanAccountPage.ACTIVE);
    }

    public void createLoanAccount(String clientName, String loanProductName) {
        navigateToLoanAccountEntryPage(setLoanSearchParameters(clientName,loanProductName)).
                clickContinueAndNavigateToLoanAccountConfirmationPage().
                navigateToLoanAccountDetailsPage();
    }

    public LoanAccountPage applyMultipleAdjustments(String loanAcountID, int howMany) {
        LoanAccountPage loanAccountPage = navigationHelper
            .navigateToClientsAndAccountsPage()
            .searchForClient(loanAcountID)
            .navigateToLoanAccountSearchResult("Account # "+loanAcountID);
        for(int i = 0; i < howMany; i++) {
            loanAccountPage
                .navigateToApplyAdjustment()
                .fillAdjustmentFieldsAndSubmit("note note note");
        }
        return loanAccountPage;
    }

    public ViewInstallmentDetailsPage reviewInstallments(CreateLoanAccountSearchParameters searchParameters, CreateLoanAccountSubmitParameters submitAccountParameters) {
        return navigateToLoanAccountEntryPage(searchParameters).createLoanAccountAndReviewInstallments(submitAccountParameters);
    }

    public ViewInstallmentDetailsPage reviewInstallmentsForHolidaySameDayRule(CreateLoanAccountSearchParameters searchParameters, CreateLoanAccountSubmitParameters submitAccountParameters) {
        return navigateToLoanAccountEntryPage(searchParameters).createLoanAccountAndReviewInstallmentsForSameDayRule(submitAccountParameters);
    }

    public ViewInstallmentDetailsPage reviewInstallmentsForHolidayNextWorkingDayRule(CreateLoanAccountSearchParameters searchParameters, CreateLoanAccountSubmitParameters submitAccountParameters) {
        return navigateToLoanAccountEntryPage(searchParameters).createLoanAccountAndReviewInstallmentsForNextWorkingRule(submitAccountParameters);
    }
}
