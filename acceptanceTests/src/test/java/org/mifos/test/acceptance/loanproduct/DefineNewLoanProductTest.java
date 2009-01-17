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

package org.mifos.test.acceptance.loanproduct;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.AdminPage;
import org.mifos.test.acceptance.framework.DbUnitUtilities;
import org.mifos.test.acceptance.framework.DefineNewLoanProductConfirmationPage;
import org.mifos.test.acceptance.framework.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.DefineNewLoanProductPreviewPage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.DefineNewLoanProductPage.SubmitFormParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"workInProgress","loanProducts","acceptance"})
public class DefineNewLoanProductTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
//    @Autowired
//    private DbUnitUtilities dbUnitUtilities;
        
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
    public void createWeeklyLoanProduct()throws Exception {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setOfferingName("productWeekly2500");
        formParameters.setOfferingShortName("pw25");
        formParameters.setDescription("descriptionForWeekly1");
        formParameters.setCategory("Other");
        formParameters.setApplicableFor("Clients");
        formParameters.setMinLoanAmount("100");
        formParameters.setMaxLoanAmount("190000");
        formParameters.setDefaultLoanAmount("2500");
        formParameters.setInterestTypes("Flat");
        formParameters.setMaxInterestRate("30");
        formParameters.setMinInterestRate("10");
        formParameters.setDefaultInterestRate("19");
        formParameters.setMaxInstallments("52");
        formParameters.setDefInstallments("52");
        formParameters.setGracePeriodType("None");
        formParameters.setInterestGLCode("31102");
        formParameters.setPrincipalGLCode("1506");


 //      dbUnitUtilities.loadDataFromFile("acceptance_small_001_dbunit.xml", dataSource);
   
        AdminPage adminPage = loginAndNavigateToAdminPage("mifos", "testmifos");
        adminPage.verifyPage();
        DefineNewLoanProductPage newLoanPage = adminPage.navigateToDefineLoanProduct();
        newLoanPage.verifyPage();
        DefineNewLoanProductPreviewPage previewPage = newLoanPage.submitAndGotoNewLoanProductPreviewPage(formParameters);
        previewPage.verifyPage();
        DefineNewLoanProductConfirmationPage confirmationPage = previewPage.submit();
        confirmationPage.verifyPage();
        
        
    }
    
                
    private AdminPage loginAndNavigateToAdminPage(String userName, String password) {
        return appLauncher
         .launchMifos()
         .loginSuccessfulAs(userName, password)
         .navigateToAdminPage();
     }

 
    
}

