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

package org.mifos.test.acceptance.savings;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.account.AccountStatus;
import org.mifos.test.acceptance.framework.account.EditAccountStatusParameters;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineAcceptedPaymentTypesPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.DepositWithdrawalSavingsParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.savings.SavingsDepositWithdrawalPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
public class CreateSavingsAccountTest extends UiTestCaseBase {

    private SavingsAccountHelper savingsAccountHelper;

    private QuestionGroupTestHelper questionGroupTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        savingsAccountHelper = new SavingsAccountHelper(selenium);
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        questionGroupTestHelper.markQuestionGroupAsActive("QGForCreateSavingsAccount");
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        questionGroupTestHelper.markQuestionGroupAsInactive("QGForCreateSavingsAccount");
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(singleThreaded = true, groups = {"savings", "acceptance", "ui", "no_db_unit"})
    public void verifyPaymentTypesForWithdrawalsAndDeposits() throws Exception {
        //When
        NavigationHelper navigationHelper = new NavigationHelper(selenium);
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineAcceptedPaymentTypesPage defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addSavingsWithdrawalsType(defineAcceptedPaymentTypesPage.CHEQUE);

        adminPage = navigationHelper.navigateToAdminPage();
        defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addSavingsWithdrawalsType(defineAcceptedPaymentTypesPage.VOUCHER);

        adminPage = navigationHelper.navigateToAdminPage();
        defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addSavingsDepositsPaymentType(defineAcceptedPaymentTypesPage.CHEQUE);

        adminPage = navigationHelper.navigateToAdminPage();
        defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addSavingsDepositsPaymentType(defineAcceptedPaymentTypesPage.VOUCHER);

        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setSavingsProduct("MonthlyClientSavingsAccount");

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("248.0");

        SavingsAccountDetailPage savingsAccountDetailPage = savingsAccountHelper.createSavingsAccountWithQG(searchParameters, submitAccountParameters);

        EditAccountStatusParameters editAccountStatusParameters = new EditAccountStatusParameters();
        editAccountStatusParameters.setAccountStatus(AccountStatus.SAVINGS_ACTIVE);
        editAccountStatusParameters.setNote("test");
        savingsAccountDetailPage = savingsAccountHelper.changeStatus(savingsAccountDetailPage.getAccountId(), editAccountStatusParameters);
        SavingsDepositWithdrawalPage savingsDepositWithdrawalPage = savingsAccountDetailPage.navigateToDepositWithdrawalPage();
        savingsDepositWithdrawalPage.selectPaymentType(DepositWithdrawalSavingsParameters.DEPOSIT);

        //Then
        savingsDepositWithdrawalPage.verifyModeOfPayments();
        //When
        savingsDepositWithdrawalPage.selectPaymentType(DepositWithdrawalSavingsParameters.WITHDRAWAL);
        //Then
        savingsDepositWithdrawalPage.verifyModeOfPayments();
        //When
        savingsAccountDetailPage = navigationHelper.navigateToSavingsAccountDetailPage("000100000000002");
        savingsDepositWithdrawalPage = savingsAccountDetailPage.navigateToDepositWithdrawalPage();
        savingsDepositWithdrawalPage.selectPaymentType(DepositWithdrawalSavingsParameters.DEPOSIT);
        //Then
        savingsDepositWithdrawalPage.verifyModeOfPayments();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(singleThreaded = true, groups = {"smoke", "savings", "acceptance", "ui", "no_db_unit"})
    public void newMandatoryClientSavingsAccountWithDateTypeCustomField() throws Exception {

        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setSavingsProduct("MonthlyClientSavingsAccount");

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("248");

        verifySavingsAccountCreationWithQG(searchParameters, submitAccountParameters);
    }

    private void verifySavingsAccountCreationWithQG(CreateSavingsAccountSearchParameters searchParameters,
                                                    CreateSavingsAccountSubmitParameters submitAccountParameters) {
        SavingsAccountDetailPage savingsAccountPage = savingsAccountHelper.createSavingsAccountWithQG(searchParameters, submitAccountParameters);
        savingsAccountPage.verifyPage();
        savingsAccountPage.verifySavingsAmount(submitAccountParameters.getAmount());
        savingsAccountPage.verifySavingsProduct(searchParameters.getSavingsProduct());
    }
}
