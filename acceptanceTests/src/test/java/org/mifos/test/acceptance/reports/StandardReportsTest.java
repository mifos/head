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

package org.mifos.test.acceptance.reports;

import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.ViewReportCategoriesPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.reports.CollectionSheetReportParametersPage;
import org.mifos.test.acceptance.framework.reports.ReportsPage;
import org.mifos.test.acceptance.framework.testhelpers.ReportTestHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"reports", "acceptance", "ui", "no_db_unit"}, enabled = false) // TODO - ldomzalski - funcionality doesn't work
public class StandardReportsTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    private ReportTestHelper reportTestHelper;
    private static final String NEWCAT = "newCat";
    private static final String NEWCAT1 = "newCat1";
    private static final String NEWCAT2 = "newCat2";
    private static final String[] DEFAULT_CATEGORIES = new String[]{"Client Detail",
            "Performance", "Center", "Loan Product Detail", "Status", "Analysis", "Miscellaneous"};

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        reportTestHelper = new ReportTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void generateCollectionSheetEntryReport() throws Exception {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("MyOfficeDHMFT");
        formParameters.setLoanOfficer("ALL");
        formParameters.setCenter("Default Center");
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

    /**
     * Report categories can be added, edited and deleted
     * http://mifosforge.jira.com/browse/MIFOSTEST-181
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void reportCategoriesTest() throws Exception {

        //When
        ViewReportCategoriesPage viewReportCategoriesPage = reportTestHelper.navigateToViewReportCategories();

        //Then
        viewReportCategoriesPage.verifyReportCategoriesExist(DEFAULT_CATEGORIES);

        //When
        viewReportCategoriesPage = reportTestHelper.addNewCategory(viewReportCategoriesPage, NEWCAT);

        //Then
        viewReportCategoriesPage.verifyReportCategoriesExist(NEWCAT);

        //When
        viewReportCategoriesPage = reportTestHelper.addNewCategory(viewReportCategoriesPage, NEWCAT1);

        //Then
        viewReportCategoriesPage.verifyReportCategoriesExist(NEWCAT1);

        //When
        viewReportCategoriesPage = reportTestHelper.editCategory(viewReportCategoriesPage, NEWCAT2, "8");

        //Then
        viewReportCategoriesPage.verifyReportCategoriesExist(NEWCAT2);
        viewReportCategoriesPage.verifyReportCategoriesNotExist(NEWCAT, 9);

        //When
        viewReportCategoriesPage = reportTestHelper.deleteCategory(viewReportCategoriesPage, "9");

        //Then
        viewReportCategoriesPage.verifyReportCategoriesNotExist(NEWCAT1, 8);

    }
}
