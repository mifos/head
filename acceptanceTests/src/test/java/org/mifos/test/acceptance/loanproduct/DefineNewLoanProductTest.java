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

package org.mifos.test.acceptance.loanproduct;


import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loan.QuestionGroupHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;


@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"smoke","loanproduct","acceptance"})
public class DefineNewLoanProductTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    private Random random;
    private QuestionGroupHelper questionGroupHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        questionGroupHelper = new QuestionGroupHelper(new NavigationHelper(selenium));
        random = new Random();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createWeeklyLoanProduct()throws Exception {
        SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        adminPage.defineLoanProduct(formParameters);

    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createWeeklyLoanProductWithQuestionGroups()throws Exception {
        String questionGroupTitle = "QG1" + random.nextInt(100);
        String question1 = "DT_" + random.nextInt(100);
        String question2 = "SS_" + random.nextInt(100);
        questionGroupHelper.createQuestionGroup(questionGroupTitle, question1, question2, "Create Loan");

        SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParametersWithQuestionGroups(questionGroupTitle);
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        adminPage.defineLoanProduct(formParameters);

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createMonthlyLoanProduct()throws Exception {
        SubmitFormParameters formParameters = FormParametersHelper.getMonthlyLoanProductParameters();
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

