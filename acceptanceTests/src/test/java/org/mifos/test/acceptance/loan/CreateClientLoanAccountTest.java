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
 
package org.mifos.test.acceptance.loan;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.DbUnitUtilities;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.LoanAccountPage;
import org.mifos.test.acceptance.framework.LoginPage; 
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = { "CreateMultipleLoanAccountsWithFeesTest", "acceptance", "ui", "workInProgress" })
public class CreateClientLoanAccountTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

     @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
     public void newWeeklyClientLoanAccount() throws Exception {
     CreateLoanAccountSearchParameters searchParameters = new
     CreateLoanAccountSearchParameters();
     searchParameters.setSearchString("Client - Veronica Abisya");
     searchParameters.setLoanProduct("Flat Interest Loan Product With Fee");
            
     CreateLoanAccountSubmitParameters submitAccountParameters = new
     CreateLoanAccountSubmitParameters();
     submitAccountParameters.setAmount("1012.0");
     dbUnitUtilities.loadDataFromFile("acceptance_small_001_dbunit.xml.zip",
     dataSource);
            
     createLoanAndCheckAmount(searchParameters, submitAccountParameters);
     }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    public void newMonthlyClientLoanAccountWithMeetingOnSpecificDayOfMonth() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct("MonthlyClientFlatLoan1stOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1234.0");
        dbUnitUtilities.loadDataFromFile("acceptance_small_004_dbunit.xml.zip", dataSource);

        createLoanAndCheckAmount(searchParameters, submitAccountParameters);
    }

     @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
     public void
     newMonthlyClientLoanAccountWithMeetingOnSameWeekAndWeekdayOfMonth()
     throws Exception {
     CreateLoanAccountSearchParameters searchParameters = new
     CreateLoanAccountSearchParameters();
     searchParameters.setSearchString("Client - Mia Monthly3rdFriday");
     searchParameters.setLoanProduct("MonthlyClientFlatLoanThirdFridayOfMonth");
            
     CreateLoanAccountSubmitParameters submitAccountParameters = new
     CreateLoanAccountSubmitParameters();
     submitAccountParameters.setAmount("2765.0");
     dbUnitUtilities.loadDataFromFile("acceptance_small_004_dbunit.xml.zip",
     dataSource);
            
     createLoanAndCheckAmount(searchParameters, submitAccountParameters);
     }

    private void createLoanAndCheckAmount(CreateLoanAccountSearchParameters searchParameters,
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
        loanAccountPage.verifyLoanAmount(submitAccountParameters.getAmount());
    }

    private CreateLoanAccountSearchPage navigateToCreateLoanAccountSearchPage() {
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        return clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
    }

    public class CreateLoanAccountSearchParameters {
        private String searchString;
        private String loanProduct;

        public String getSearchString() {
            return this.searchString;
        }

        public void setSearchString(String searchString) {
            this.searchString = searchString;
        }

        public String getLoanProduct() {
            return this.loanProduct;
        }

        public void setLoanProduct(String loanProduct) {
            this.loanProduct = loanProduct;
        }
    }

    public class CreateLoanAccountSubmitParameters {
        private String amount;

        public String getAmount() {
            return this.amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }

}
