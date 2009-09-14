/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
import org.mifos.test.acceptance.framework.loan.AccountAddNotesPage;
import org.mifos.test.acceptance.framework.loan.AccountPreviewNotesPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountConfirmationPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountEntryPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;

import com.thoughtworks.selenium.Selenium;

/**
 * Holds methods common to most savings account tests.
 *
 */
public class SavingsAccountHelper {

    private final Selenium selenium;

    public SavingsAccountHelper(Selenium selenium) {
        this.selenium = selenium;
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

    private CreateSavingsAccountSearchPage navigateToCreateSavingsAccountSearchPage() {
      LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
      loginPage.verifyPage();
      HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
      homePage.verifyPage();
      ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
      clientsAndAccountsPage.verifyPage();
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
    
}
