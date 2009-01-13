/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
import org.mifos.test.acceptance.framework.CreateLoanAccountsSearchPage;
import org.mifos.test.acceptance.framework.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"CreateMultipleLoanAccountsWithFeesTest","acceptance","ui", "workInProgress"})
public class CreateMultipleLoanAccountsWithFeesTest extends UiTestCaseBase {

    private class CreateMultipleLoanAccountSelectParameters {
        public String getBranch() {
            return this.branch;
        }
        public void setBranch(String branch) {
            this.branch = branch;
        }
        public String getLoanOfficer() {
            return this.loanOfficer;
        }
        public void setLoanOfficer(String loanOfficer) {
            this.loanOfficer = loanOfficer;
        }
        public String getCenter() {
            return this.center;
        }
        public void setCenter(String center) {
            this.center = center;
        }
        public String getLoanProduct() {
            return this.loanProduct;
        }
        public void setLoanProduct(String loanProduct) {
            this.loanProduct = loanProduct;
        }
        
        private String branch;
        private String loanOfficer;
        private String center;
        private String loanProduct;
    }

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
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
    public void defaultAdminUserCreatesMultipleLoanAccountsWithFees() throws Exception {
        CreateMultipleLoanAccountSelectParameters formParameters = new CreateMultipleLoanAccountSelectParameters();
        formParameters.setBranch("Office1");
        formParameters.setLoanOfficer("Bagonza Wilson");
        formParameters.setCenter("Center1");
        formParameters.setLoanProduct("FlatInterestLoanProductWithFee");

        dbUnitUtilities.loadDataFromFile("acceptance_small_001_dbunit.xml", dataSource);
        CreateLoanAccountsSearchPage createLoanAccountsSearchPage =  // NOPMD by Van on 1/12/09 5:29 PM (work in progress)
            loginAndNavigateToCreateLoanAccountsSearchPage("mifos","testmifos");  // NOPMD
     
        // TODO: user formParameters to make selections from drop downs
    }
    
    private CreateLoanAccountsSearchPage loginAndNavigateToCreateLoanAccountsSearchPage(String userName, String password) {
        return appLauncher
         .launchMifos()
         .loginSuccessfulAs(userName, password)
         .navigateToClientsAndAccountsUsingHeaderTab()
         .navigateToCreateLoanAccountsSearchPageUsingLeftMenu();
    }
    
}

