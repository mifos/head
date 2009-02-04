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

package org.mifos.test.acceptance.collectionsheet;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.CollectionSheetEntryConfirmationPage;
import org.mifos.test.acceptance.framework.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.CollectionSheetEntryPreviewDataPage;
import org.mifos.test.acceptance.framework.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.DbUnitUtilities;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.LoginPage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"CollectionSheetEntryTest","acceptance","ui"})
public class CollectionSheetEntryAttendanceTest extends UiTestCaseBase {

    private static final String ATTENDANCE_P = "1";
    private static final String ATTENDANCE_L = "2";
    private static final String ATTENDANCE_AA = "3";
    private static final String ATTENDANCE_A = "4";

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
    public void defaultAdminUserSelectsValidCollectionSheetEntryParameters() throws Exception {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("Office2");
        formParameters.setLoanOfficer("John Okoth");
        formParameters.setCenter("Center2");
        formParameters.setPaymentMode("Cash");
        
        dbUnitUtilities.loadDataFromFile("acceptance_small_001_dbunit.xml.zip", dataSource);
        
        CollectionSheetEntrySelectPage selectPage = 
            loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();
        CollectionSheetEntryEnterDataPage enterDataPage = 
            selectPage.submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        enterDataPage.verifyPage(formParameters);
        enterDataPage.enterAttendance(0,ATTENDANCE_L);
        enterDataPage.enterAttendance(1,ATTENDANCE_AA);
        enterDataPage.enterAttendance(2,ATTENDANCE_A);
        enterDataPage.enterAttendance(3,ATTENDANCE_P);
        CollectionSheetEntryPreviewDataPage previewPage = 
            enterDataPage.submitAndGotoCollectionSheetEntryPreviewDataPage();
        previewPage.verifyPage(formParameters);
        CollectionSheetEntryConfirmationPage confirmationPage = 
            previewPage.submitAndGotoCollectionSheetEntryConfirmationPage();
        confirmationPage.verifyPage();
        
    }

    private CollectionSheetEntrySelectPage loginAndNavigateToCollectionSheetEntrySelectPage() {
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        return clientsAndAccountsPage.navigateToEnterCollectionSheetDataUsingLeftMenu();
    }

    
}

