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

package org.mifos.test.acceptance.framework.testhelpers;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.account.AccountStatus;
import org.mifos.test.acceptance.framework.account.EditAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.AccountAddNotesPage;
import org.mifos.test.acceptance.framework.loan.AccountChangeStatusPage;
import org.mifos.test.acceptance.framework.loan.AccountPreviewNotesPage;
import org.mifos.test.acceptance.framework.loan.EditAccountStatusConfirmationPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.questionnaire.CaptureQuestionResponse;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountConfirmationPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountEntryPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountPreviewPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.DepositWithdrawalSavingsParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.savings.SavingsCloseAccountPage;
import org.mifos.test.acceptance.framework.savings.SavingsDepositWithdrawalConfirmationPage;
import org.mifos.test.acceptance.framework.savings.SavingsDepositWithdrawalPage;
import org.mifos.test.acceptance.framework.savings.ViewDepositDueDetailsPage;
import org.mifos.test.acceptance.framework.util.UiTestUtils;

import com.thoughtworks.selenium.Selenium;

/**
 * Holds methods common to most savings account tests.
 *
 */
public class SavingsAccountHelper {

    private final Selenium selenium;
    private final NavigationHelper navigationHelper;

    public SavingsAccountHelper(Selenium selenium) {
        this.selenium = selenium;
        this.navigationHelper = new NavigationHelper(selenium);
    }

    /**
     * Creates a savings account.
     * @param searchParameters Parameters to find the client/group that will be the owner of the account.
     * @param submitAccountParameters The parameters for the savings account.
     */
    public SavingsAccountDetailPage createSavingsAccount(CreateSavingsAccountSearchParameters searchParameters,
            CreateSavingsAccountSubmitParameters submitAccountParameters) {
        CreateSavingsAccountSearchPage createSavingsAccountSearchPage = navigateToCreateSavingsAccountSearchPage();
        createSavingsAccountSearchPage.verifyPage();
        CreateSavingsAccountEntryPage createSavingsAccountEntryPage = createSavingsAccountSearchPage.searchAndNavigateToCreateSavingsAccountPage(searchParameters);
        createSavingsAccountEntryPage.verifyPage();
        CreateSavingsAccountConfirmationPage createSavingsAccountConfirmationPage = createSavingsAccountEntryPage.submitAndNavigateToSavingsAccountConfirmationPage(submitAccountParameters);
        createSavingsAccountConfirmationPage.verifyPage();
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingsAccountConfirmationPage.navigateToSavingsAccountDetailsPage();
        savingsAccountDetailPage.verifyPage();
        return savingsAccountDetailPage;
    }
    public SavingsAccountDetailPage createSavingsAccountWithoutPendingApprovalState(CreateSavingsAccountSearchParameters searchParameters,
            CreateSavingsAccountSubmitParameters submitAccountParameters){
        CreateSavingsAccountSearchPage createSavingsAccountSearchPage = navigateToCreateSavingsAccountSearchPage();
        createSavingsAccountSearchPage.verifyPage();
        CreateSavingsAccountEntryPage createSavingsAccountEntryPage = createSavingsAccountSearchPage.searchAndNavigateToCreateSavingsAccountPage(searchParameters);
        createSavingsAccountEntryPage.verifyPage();
        CreateSavingsAccountConfirmationPage createSavingsAccountConfirmationPage =
            createSavingsAccountEntryPage.submitAndNavigateToSavingsAccountConfirmationPageWithoutPendingApprovalState(submitAccountParameters);
        createSavingsAccountConfirmationPage.verifyPage();
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingsAccountConfirmationPage.navigateToSavingsAccountDetailsPage();
        savingsAccountDetailPage.verifyPage();
        return savingsAccountDetailPage;
    }

    public QuestionnairePage navigateToQuestionResponseDuringCreateSavings(CreateSavingsAccountSearchParameters searchParameters,
            CreateSavingsAccountSubmitParameters submitAccountParameters) {
        CreateSavingsAccountSearchPage createSavingsAccountSearchPage = navigateToCreateSavingsAccountSearchPage();
        createSavingsAccountSearchPage.verifyPage();
        CreateSavingsAccountEntryPage createSavingsAccountEntryPage = createSavingsAccountSearchPage.searchAndNavigateToCreateSavingsAccountPage(searchParameters);
        createSavingsAccountEntryPage.verifyPage();
        return createSavingsAccountEntryPage.submitAndNavigateToQuestionnairePage(submitAccountParameters);
    }

    private CreateSavingsAccountSearchPage navigateToCreateSavingsAccountSearchPage() {
      LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
      loginPage.verifyPage();
      HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
      homePage.verifyPage();
      ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
      return clientsAndAccountsPage.navigateToCreateSavingsAccountUsingLeftMenu();
    }

    public SavingsAccountDetailPage addNoteToSavingsAccount(String testAccount, String testAccountNote) {
            NavigationHelper helper = new NavigationHelper(selenium);

            SavingsAccountDetailPage savingsAccountDetailPage = helper.navigateToSavingsAccountDetailPage(testAccount);
            savingsAccountDetailPage.verifyPage();

            AccountAddNotesPage addNotesPage = savingsAccountDetailPage.navigateToAddNotesPage();
            addNotesPage.verifyPage();
            AccountPreviewNotesPage previewPage = addNotesPage.submitAndNavigateToAccountAddNotesPreviewPage(testAccountNote);
            previewPage.verifyPage();
            savingsAccountDetailPage = previewPage.submitAndNavigateToSavingsAccountDetailPage();

            return savingsAccountDetailPage;
    }

    public SavingsAccountDetailPage makeDepositOrWithdrawalOnSavingsAccount(String savingsAccountID, DepositWithdrawalSavingsParameters params) {
            NavigationHelper helper = new NavigationHelper(selenium);
             SavingsAccountDetailPage savingsAccountDetailPage = helper.navigateToSavingsAccountDetailPage(savingsAccountID);

            savingsAccountDetailPage.verifyPage();

            SavingsDepositWithdrawalPage savingsDepositWithdrawalPage = savingsAccountDetailPage.navigateToDepositWithdrawalPage();
            savingsDepositWithdrawalPage.verifyPage();

            SavingsDepositWithdrawalConfirmationPage savingsDepositWithdrawalConfirmationPage = savingsDepositWithdrawalPage.submitAndNavigateToDepositWithdrawalConfirmationPage(params);
            savingsDepositWithdrawalConfirmationPage.verifyPage();

            savingsAccountDetailPage = savingsDepositWithdrawalConfirmationPage.submitAndNavigateToSavingsAccountDetailPage();

            return savingsAccountDetailPage;

    }

    public SavingsAccountDetailPage createSavingsAccountWithQG(CreateSavingsAccountSearchParameters searchParameters,
                                                               CreateSavingsAccountSubmitParameters submitAccountParameters) {
        CreateSavingsAccountSearchPage createSavingsAccountSearchPage = navigateToCreateSavingsAccountSearchPage();
        createSavingsAccountSearchPage.verifyPage();
        CreateSavingsAccountEntryPage createSavingsAccountEntryPage = createSavingsAccountSearchPage.searchAndNavigateToCreateSavingsAccountPage(searchParameters);
        createSavingsAccountEntryPage.verifyPage();
        CreateSavingsAccountConfirmationPage createSavingsAccountConfirmationPage = createSavingsAccountEntryPage.submitWithQGAndNavigateToSavingsAccountConfirmationPage(submitAccountParameters);
        createSavingsAccountConfirmationPage.verifyPage();
        SavingsAccountDetailPage savingsAccountDetailPage = createSavingsAccountConfirmationPage.navigateToSavingsAccountDetailsPage();
        savingsAccountDetailPage.verifyPage();
        return savingsAccountDetailPage;
    }

    public CreateSavingsAccountPreviewPage fillQuestionGroupsDurringCreationSavingsAccount(CreateSavingsAccountSearchParameters searchParameters,
            CreateSavingsAccountSubmitParameters submitAccountParameters, QuestionResponseParameters responseParams) {
        CreateSavingsAccountSearchPage createSavingsAccountSearchPage = navigateToCreateSavingsAccountSearchPage();
        createSavingsAccountSearchPage.verifyPage();
        CreateSavingsAccountEntryPage createSavingsAccountEntryPage = createSavingsAccountSearchPage.searchAndNavigateToCreateSavingsAccountPage(searchParameters);
        createSavingsAccountEntryPage.verifyPage();
        QuestionnairePage questionnairePage = createSavingsAccountEntryPage.submitAndNavigateToQuestionnairePage(submitAccountParameters);
        questionnairePage.populateAnswers(responseParams);
        return questionnairePage.navigateToNextPageSavingsAccountCreation();
    }

    public SavingsAccountDetailPage fillQuestionGroupsDuringClosingSavingsAccount(String savingsId, QuestionResponseParameters questionResponseParameters, String[] questionsExist) {
        NavigationHelper helper = new NavigationHelper(selenium);
        SavingsAccountDetailPage savingsAccountDetailPage = helper.navigateToSavingsAccountDetailPage(savingsId);
        savingsAccountDetailPage.verifyPage();
        SavingsCloseAccountPage savingsCloseAccountPage = savingsAccountDetailPage.navigateToCloseAccount();
        savingsCloseAccountPage.verifyPage();
        CaptureQuestionResponse captureQuestionResponse = savingsCloseAccountPage.submitAndNavigateToQuestionnairePage("Closing Savings with QG");
        captureQuestionResponse.verifyQuestionsExists(questionsExist);
        captureQuestionResponse.populateAnswers(questionResponseParameters);
        captureQuestionResponse.navigateToNextPageSavingsAccountClosing();
        savingsAccountDetailPage = savingsCloseAccountPage.clickCloseButton();
        savingsAccountDetailPage.verifyPage();
        return savingsAccountDetailPage;
    }

    public SavingsAccountDetailPage editAdditionalInformationDurringCreationSavingsAccount(QuestionResponseParameters responseParams) {
        CreateSavingsAccountPreviewPage createSavingsAccountPreviewPage = new CreateSavingsAccountPreviewPage(selenium);
        QuestionnairePage questionnairePage = createSavingsAccountPreviewPage.editAdditionalInformationQuestionnairePage();
        UiTestUtils.sleep(1500);
        questionnairePage.populateAnswers(responseParams);
        createSavingsAccountPreviewPage = questionnairePage.navigateToNextPageSavingsAccountCreation();
        createSavingsAccountPreviewPage.verifyPage();
        CreateSavingsAccountConfirmationPage createSavingsAccountConfirmationPage = createSavingsAccountPreviewPage.submitForApproval();

        SavingsAccountDetailPage savingsAccountDetailPage = createSavingsAccountConfirmationPage.navigateToSavingsAccountDetailsPage();
        savingsAccountDetailPage.verifyPage();
        return savingsAccountDetailPage;
    }

    public SavingsAccountDetailPage closeSavingsAccount(String savingsAccountID, String notes) {
        NavigationHelper helper = new NavigationHelper(selenium);
        SavingsAccountDetailPage savingsAccountDetailPage = helper.navigateToSavingsAccountDetailPage(savingsAccountID);
        savingsAccountDetailPage.verifyPage();
        SavingsCloseAccountPage savingsCloseAccountPage = savingsAccountDetailPage.navigateToCloseAccount();
        savingsCloseAccountPage.verifyPage();
        savingsAccountDetailPage = savingsCloseAccountPage.closeSavingsAccount(notes);
        savingsAccountDetailPage.verifyPage();
        return savingsAccountDetailPage;
    }

    public SavingsAccountDetailPage changeStatus(String savingsId, EditAccountStatusParameters editAccountStatusParameters){
        SavingsAccountDetailPage savingsAccountDetailPage = navigationHelper.navigateToSavingsAccountDetailPage(savingsId);

        AccountChangeStatusPage accountChangeStatusPage = savingsAccountDetailPage.navigateToEditAccountStatus();

        EditAccountStatusConfirmationPage editAccountStatusConfirmationPage = accountChangeStatusPage.setChangeStatusParametersAndSubmit(editAccountStatusParameters);

        savingsAccountDetailPage = editAccountStatusConfirmationPage.submitAndNavigateToSavingAccountPage();
        savingsAccountDetailPage.verifyStatus(editAccountStatusParameters.getAccountStatus().getStatusText());

        return savingsAccountDetailPage;
    }

    public SavingsAccountDetailPage activateSavingsAccount(String savingsId){
        EditAccountStatusParameters editAccountStatusParameters =new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("change status to active");
        return changeStatus(savingsId, editAccountStatusParameters);
    }

    public void verifyTotalAmountDue(String savingsId, Integer numberOfGroupMembers, Float amountPerMember){
        SavingsAccountDetailPage savingsAccountDetailPage = navigationHelper.navigateToSavingsAccountDetailPage(savingsId);
        ViewDepositDueDetailsPage viewDepositDueDetailsPage = savingsAccountDetailPage.navigateToViewDepositDueDetails();
        viewDepositDueDetailsPage.verifyTotalAmountDue(numberOfGroupMembers, amountPerMember);
    }
}
