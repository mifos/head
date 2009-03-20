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
 
package org.mifos.test.acceptance.loanproduct;


import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.AdminPage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"loanProducts","acceptance"})
public class DefineNewLoanProductTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

 
        
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
        formParameters.setOfferingName("productWeekly" + StringUtil.getRandomString(4));
        formParameters.setOfferingShortName("pw" + StringUtil.getRandomString(2));
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
        formParameters.setFreqOfInstallments("Weeks"); //This parameter expects Weeks or Months
        formParameters.setMaxInstallments("52");
        formParameters.setDefInstallments("52");
        formParameters.setGracePeriodType("None");
        formParameters.setInterestGLCode("31102");
        formParameters.setPrincipalGLCode("1506");

   
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        adminPage.defineLoanProduct(formParameters);

    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createMonthlyLoanProduct()throws Exception {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setOfferingName("productMonthly" + StringUtil.getRandomString(4));
        formParameters.setOfferingShortName("pm" + StringUtil.getRandomString(2));
        formParameters.setDescription("descriptionForMonthly1");
        formParameters.setCategory("Other");
        formParameters.setApplicableFor("Clients");
        formParameters.setMinLoanAmount("1007");
        formParameters.setMaxLoanAmount("190000");
        formParameters.setDefaultLoanAmount("60000");
        formParameters.setInterestTypes("Flat");
        formParameters.setMaxInterestRate("30");
        formParameters.setMinInterestRate("10");
        formParameters.setDefaultInterestRate("12");
        formParameters.setFreqOfInstallments("Months"); //This parameter expects Weeks or Months
        formParameters.setMaxInstallments("72");
        formParameters.setDefInstallments("60");
        formParameters.setGracePeriodType("None");
        formParameters.setInterestGLCode("31102");
        formParameters.setPrincipalGLCode("1506");

        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        adminPage.defineLoanProduct(formParameters);

    }
                    
    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher
         .launchMifos()
         .loginSuccessfullyUsingDefaultCredentials()
         .navigateToAdminPage();
     }

}

