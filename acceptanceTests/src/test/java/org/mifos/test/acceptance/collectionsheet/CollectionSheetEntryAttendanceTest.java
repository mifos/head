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

import java.io.IOException;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
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
import org.mifos.test.framework.util.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"CollectionSheetEntryTest","acceptance","ui"})
public class CollectionSheetEntryAttendanceTest extends UiTestCaseBase {

    private static final String CUSTOMER_ATTENDANCE = "CUSTOMER_ATTENDANCE";
    private static final String ATTENDANCE_P = "1";
    private static final String ATTENDANCE_A = "2";
    private static final String ATTENDANCE_AA = "3";
    private static final String ATTENDANCE_L = "4";

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
    public void defaultAdminUserEntersAttendanceData() throws Exception {
        enterAndVerifyBasicAttendanceData();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void secondCollectionSheetEntryOverwritesFirstAttendanceData() throws Exception {
        CollectionSheetEntryConfirmationPage confirmationPage = enterAndVerifyBasicAttendanceData();
        verifyAttendanceData(this.getBasicAttendanceDataSet());
        SubmitFormParameters formParameters = getFormParametersForCenter2();
        HomePage homePage = confirmationPage.navigateToHomePage();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        CollectionSheetEntrySelectPage selectPage = clientsAndAccountsPage.navigateToEnterCollectionSheetDataUsingLeftMenu();
        CollectionSheetEntryEnterDataPage enterDataPage = selectPage.submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        enterOverwriteAttendanceData(enterDataPage);
        submitDataAndVerifySuccessPage(formParameters, enterDataPage);
        verifyAttendanceData(getOverwriteAttendanceDataSet());
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void enteringAttendanceDataForOneCenterDoesNotAffectOtherCenters() throws Exception {
        CollectionSheetEntryConfirmationPage confirmationPage = enterAndVerifyBasicAttendanceData();
        verifyAttendanceData(this.getBasicAttendanceDataSet());
        SubmitFormParameters formParameters = getFormParametersForCenter1();
        HomePage homePage = confirmationPage.navigateToHomePage();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        CollectionSheetEntrySelectPage selectPage = clientsAndAccountsPage.navigateToEnterCollectionSheetDataUsingLeftMenu();
        CollectionSheetEntryEnterDataPage enterDataPage = selectPage.submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        enterSecondCenterAttendanceData(enterDataPage);
        submitDataAndVerifySuccessPage(formParameters, enterDataPage);
        verifyAttendanceData(getTwoCenterAttendanceData());
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private CollectionSheetEntryConfirmationPage enterAndVerifyBasicAttendanceData() throws DatabaseUnitException, SQLException, IOException,
            Exception, DataSetException {
        dbUnitUtilities.loadDataFromFile("acceptance_small_001_dbunit.xml.zip", dataSource);
        SubmitFormParameters formParameters = getFormParametersForCenter2();
        CollectionSheetEntryEnterDataPage enterDataPage = navigateToCollectionSheetEntryPage(formParameters);
        enterBasicAttendanceData(enterDataPage);
        CollectionSheetEntryConfirmationPage confirmationPage = submitDataAndVerifySuccessPage(formParameters, enterDataPage);
        verifyAttendanceData(getBasicAttendanceDataSet());
        return confirmationPage;
    }

    private void enterBasicAttendanceData(CollectionSheetEntryEnterDataPage enterDataPage) {
        enterDataPage.enterAttendance(0,ATTENDANCE_L);
        enterDataPage.enterAttendance(1,ATTENDANCE_AA);
        enterDataPage.enterAttendance(2,ATTENDANCE_A);
        enterDataPage.enterAttendance(3,ATTENDANCE_P);
    }

    private void enterOverwriteAttendanceData(CollectionSheetEntryEnterDataPage enterDataPage) {
        enterDataPage.enterAttendance(0,ATTENDANCE_P);
        enterDataPage.enterAttendance(1,ATTENDANCE_P);
        enterDataPage.enterAttendance(2,ATTENDANCE_P);
        enterDataPage.enterAttendance(3,ATTENDANCE_L);
    }

    private void enterSecondCenterAttendanceData(CollectionSheetEntryEnterDataPage enterDataPage) {
        enterDataPage.enterAttendance(0,ATTENDANCE_AA);
        enterDataPage.enterAttendance(1,ATTENDANCE_A);
        enterDataPage.enterAttendance(2,ATTENDANCE_L);
    }

    private CollectionSheetEntryEnterDataPage navigateToCollectionSheetEntryPage(SubmitFormParameters formParameters) {
        CollectionSheetEntrySelectPage selectPage = loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();
        CollectionSheetEntryEnterDataPage enterDataPage = selectPage
                .submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        return enterDataPage;
    }

    private CollectionSheetEntryConfirmationPage submitDataAndVerifySuccessPage(SubmitFormParameters formParameters,
            CollectionSheetEntryEnterDataPage enterDataPage) {
        CollectionSheetEntryPreviewDataPage previewPage = 
            enterDataPage.submitAndGotoCollectionSheetEntryPreviewDataPage();
        previewPage.verifyPage(formParameters);
        CollectionSheetEntryConfirmationPage confirmationPage = 
            previewPage.submitAndGotoCollectionSheetEntryConfirmationPage();
        confirmationPage.verifyPage();
        return confirmationPage;
    }

    private SubmitFormParameters getFormParametersForCenter2() {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("Office2");
        formParameters.setLoanOfficer("John Okoth");
        formParameters.setCenter("Center2");
        formParameters.setPaymentMode("Cash");
        return formParameters;
    }

    private SubmitFormParameters getFormParametersForCenter1() {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("Office1");
        formParameters.setLoanOfficer("Bagonza Wilson");
        formParameters.setCenter("Center1");
        formParameters.setPaymentMode("Cash");
        return formParameters;
    }

    private CollectionSheetEntrySelectPage loginAndNavigateToCollectionSheetEntrySelectPage() {
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        return clientsAndAccountsPage.navigateToEnterCollectionSheetDataUsingLeftMenu();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private void verifyAttendanceData(IDataSet attendanceDataSet) throws Exception {
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTable(dataSource, CUSTOMER_ATTENDANCE);
        dbUnitUtilities.verifyTable(CUSTOMER_ATTENDANCE, databaseDataSet, attendanceDataSet);   
    }

    private IDataSet getBasicAttendanceDataSet() throws DataSetException, IOException {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=1","MEETING_DATE=[null]","CUSTOMER_ID=8","ATTENDANCE=4");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=2","MEETING_DATE=[null]","CUSTOMER_ID=9","ATTENDANCE=3");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=3","MEETING_DATE=[null]","CUSTOMER_ID=10","ATTENDANCE=2");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=4","MEETING_DATE=[null]","CUSTOMER_ID=11","ATTENDANCE=1");
        return attendanceDataSet.getDataSet();
    }

    private IDataSet getOverwriteAttendanceDataSet() throws DataSetException, IOException {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=1","MEETING_DATE=[null]","CUSTOMER_ID=8","ATTENDANCE=1");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=2","MEETING_DATE=[null]","CUSTOMER_ID=9","ATTENDANCE=1");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=3","MEETING_DATE=[null]","CUSTOMER_ID=10","ATTENDANCE=1");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=4","MEETING_DATE=[null]","CUSTOMER_ID=11","ATTENDANCE=4");
        return attendanceDataSet.getDataSet();
    }
    
    private IDataSet getTwoCenterAttendanceData() throws DataSetException, IOException {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=1","MEETING_DATE=[null]","CUSTOMER_ID=8","ATTENDANCE=4");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=2","MEETING_DATE=[null]","CUSTOMER_ID=9","ATTENDANCE=3");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=3","MEETING_DATE=[null]","CUSTOMER_ID=10","ATTENDANCE=2");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=4","MEETING_DATE=[null]","CUSTOMER_ID=11","ATTENDANCE=1");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=5","MEETING_DATE=[null]","CUSTOMER_ID=3","ATTENDANCE=3");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=6","MEETING_DATE=[null]","CUSTOMER_ID=4","ATTENDANCE=2");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=7","MEETING_DATE=[null]","CUSTOMER_ID=5","ATTENDANCE=4");
        return attendanceDataSet.getDataSet();
    }
    
}

