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
 
package org.mifos.test.acceptance.reports;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.reports.CollectionSheetReportParametersPage;
import org.mifos.test.acceptance.framework.reports.ReportsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"reports","acceptance","ui"})
public class StandardReportsTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    private AppLauncher appLauncher;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

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
    
    @Test(enabled=false)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void generateCollectionSheetEntryReport() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("MyOffice1233265929385");
        formParameters.setLoanOfficer("Joe1233265931256 Guy1233265931256");
        formParameters.setCenter("MyCenter1233265933427");
        formParameters.setTransactionDay("23");
        formParameters.setTransactionMonth("04");
        formParameters.setTransactionYear("2009");
        
        
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        ReportsPage reportsPage = homePage.navigateToReportsPage();
        CollectionSheetReportParametersPage collSheetReportParametersPage = reportsPage.selectCollectionSheetEntryReport();
        collSheetReportParametersPage.generateCollectionSheetEntryReport(formParameters);
        // TODO: No validation for now.  This will simply demonstrate the problem if 
        // PDF generation is messed up (as it was when the itext library was removed)
        // An attempt was made to validate by using the BIRT url to generate the PDF
        // but following that url includes parameter dropdown screens before whatever
        // call actually generates the PDF
       
    }

}
