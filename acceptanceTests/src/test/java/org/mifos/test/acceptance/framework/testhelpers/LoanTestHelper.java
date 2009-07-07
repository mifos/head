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
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanConfirmationPage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanPage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditAccountStatusConfirmationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;

import com.thoughtworks.selenium.Selenium;

/**
 * Holds methods common to most loan tests.
 *
 */
public class LoanTestHelper {

    private final Selenium selenium;

    public LoanTestHelper(Selenium selenium) {
        this.selenium = selenium;
    }
    /**
     * Creates a loan account.
     * @param searchParameters
     * @param submitAccountParameters
     */
    public LoanAccountPage createLoanAccount(CreateLoanAccountSearchParameters searchParameters,
            CreateLoanAccountSubmitParameters submitAccountParameters) {
        CreateLoanAccountSearchPage createLoanAccountSearchPage = navigateToCreateLoanAccountSearchPage();
        createLoanAccountSearchPage.verifyPage();
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage
        .searchAndNavigateToCreateLoanAccountPage(searchParameters);
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountEntryPage
        .submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters);
        createLoanAccountConfirmationPage.verifyPage();
        LoanAccountPage loanAccountPage = createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
        loanAccountPage.verifyPage();
        return loanAccountPage;
    }
    
    /**
     * Edits the loan account with id loanId and updates its settings with the ones in params.
     * @param loanId The account id.
     * @param params The status parameters.
     */
    public void changeLoanAccountStatus(String loanId, EditLoanAccountStatusParameters params) {
        LoanAccountPage loanAccountPage = navigateToLoanAccountPage(loanId);
        loanAccountPage.verifyPage();
        
        EditLoanAccountStatusPage editAccountStatusPage = loanAccountPage.navigateToEditAccountStatus();
        editAccountStatusPage.verifyPage();
        
        EditAccountStatusConfirmationPage confirmationPage = editAccountStatusPage.submitAndNavigateToEditStatusConfirmationPage(params);
        confirmationPage.verifyPage();
        
        loanAccountPage = confirmationPage.submitAndNavigateToLoanAccountPage();
        loanAccountPage.verifyPage();
    }
    
    /**
     * Disburses the loan account with id loanId.
     * @
     */
    public LoanAccountPage disburseLoan(String loanId, DisburseLoanParameters params) {
        LoanAccountPage loanAccountPage = navigateToLoanAccountPage(loanId);
        loanAccountPage.verifyPage();
        
        DisburseLoanPage disburseLoanPage = loanAccountPage.navigateToDisburseLoan();
        disburseLoanPage.verifyPage();
        
        DisburseLoanConfirmationPage disburseLoanConfirmationPage = disburseLoanPage.submitAndNavigateToDisburseLoanConfirmationPage(params);
        disburseLoanConfirmationPage.verifyPage();
        
        loanAccountPage = disburseLoanConfirmationPage.submitAndNavigateToLoanAccountPage();
        loanAccountPage.verifyPage();
        
        return loanAccountPage;
    }
    
    /**
     * Wraps navigateToLoanAccountPage so that a String can be used.
     */
    public LoanAccountPage navigateToLoanAccountPage(String searchParam) {
        CreateLoanAccountSearchParameters params = new CreateLoanAccountSearchParameters();
        params.setSearchString(searchParam);
        return navigateToLoanAccountPage(params);
    }

    
    public LoanAccountPage navigateToLoanAccountPage(CreateLoanAccountSearchParameters searchParams) {
        String searchString = searchParams.getSearchString();
        
        LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        SearchResultsPage searchResultsPage = homePage.search(searchString);
        
        LoanAccountPage loanAccountPage = searchResultsPage.navigateToLoanAccountDetailPage(searchString);
        loanAccountPage.verifyPage();
        return loanAccountPage;
    }

    private CreateLoanAccountSearchPage navigateToCreateLoanAccountSearchPage() {
        LoginPage loginPage = new AppLauncher(selenium).launchMifos().logout();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        return clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
    }
}
