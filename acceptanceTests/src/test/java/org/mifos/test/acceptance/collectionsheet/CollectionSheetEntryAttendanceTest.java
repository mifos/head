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

package org.mifos.test.acceptance.collectionsheet;

import java.io.IOException;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryConfirmationPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryPreviewDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CollectionSheetEntryTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.framework.util.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"collectionsheet","acceptance","ui"})
public class CollectionSheetEntryAttendanceTest extends UiTestCaseBase {

    private static final int ATTENDANCE_P = CollectionSheetEntryEnterDataPage.ATTENDANCE_P;
    private static final int ATTENDANCE_A = CollectionSheetEntryEnterDataPage.ATTENDANCE_A;
    private static final int ATTENDANCE_AA = CollectionSheetEntryEnterDataPage.ATTENDANCE_AA;
    private static final int ATTENDANCE_L = CollectionSheetEntryEnterDataPage.ATTENDANCE_L;

    private static final int[] BASIC_ATTENDANCE_VALUES = new int[] { ATTENDANCE_L, ATTENDANCE_AA, ATTENDANCE_A, ATTENDANCE_P };
    private static final int[] OVERWRITE_ATTENDANCE_VALUES = new int[] { ATTENDANCE_P, ATTENDANCE_P, ATTENDANCE_P, ATTENDANCE_L };
    private static final int[] SECOND_CENTER_ATTENDANCE_VALUES = new int[] { ATTENDANCE_AA, ATTENDANCE_A, ATTENDANCE_L };

    private static final String CUSTOMER_ATTENDANCE = "CUSTOMER_ATTENDANCE";

    private CollectionSheetEntryTestHelper collectionSheetEntryTestHelper;
    private NavigationHelper navigationHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,23,1,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        collectionSheetEntryTestHelper = new CollectionSheetEntryTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void enteringAttendanceDataForOneCenterDoesNotAffectOtherCenters() throws Exception {
        CollectionSheetEntryConfirmationPage confirmationPage = enterAndVerifyBasicAttendanceData();
        verifyAttendanceData(this.getBasicAttendanceDataSet());
        SubmitFormParameters formParameters = getFormParametersForCenter1();
        enterAttendanceDataForSecondCenter(confirmationPage, formParameters, SECOND_CENTER_ATTENDANCE_VALUES, getTwoCenterAttendanceDataSet());
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyEnteringAttendanceForOneCenterDoesntAffectOtherCenters() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
        SubmitFormParameters collectionSheetParams = getFormParametersForCenter2();
        int[] defaultAttendanceValues = {ATTENDANCE_P, ATTENDANCE_P, ATTENDANCE_P, ATTENDANCE_P};
        String[] clients = {"Teja Kakarla", "Aarif Mawani", "Mutu Juma", "Anna Martin"};
        String[] clientsNotAffected = {"Veronica Abisya", "Dauda Mayowa", "Polly Gikonyo"};
        int[] meetingsAttended1 = {1, 1, 1, 1};
        int[] meetingsMissed1 = {0, 0, 0, 0};
        int[] meetingsAttended2 = {1, 0, 0, 1};
        int[] meetingsMissed2 = {0, 1, 1, 0};

        collectionSheetEntryTestHelper.submitCollectionSheetWithChangedAttendance(collectionSheetParams, defaultAttendanceValues, OVERWRITE_ATTENDANCE_VALUES);
        for(int i = 0; i < clients.length; i++) {
            verifyMeetingAttendances(clients[i], meetingsAttended1[i], meetingsMissed1[i]);
        }
        for (String element : clientsNotAffected) {
            verifyMeetingAttendances(element, 0, 0);
        }
        collectionSheetEntryTestHelper.submitCollectionSheetWithChangedAttendance(collectionSheetParams, OVERWRITE_ATTENDANCE_VALUES, BASIC_ATTENDANCE_VALUES);
        for(int i = 0; i < clients.length; i++) {
            verifyMeetingAttendances(clients[i], meetingsAttended2[i], meetingsMissed2[i]);
        }
        for (String element : clientsNotAffected) {
            verifyMeetingAttendances(element, 0, 0);
        }
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private void enterAttendanceDataForSecondCenter(CollectionSheetEntryConfirmationPage confirmationPage, SubmitFormParameters formParameters, int[] attendanceValues, IDataSet dataSetToVerify)
    throws Exception, DataSetException, IOException {
        HomePage homePage = confirmationPage.navigateToHomePage();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        CollectionSheetEntrySelectPage selectPage = clientsAndAccountsPage.navigateToEnterCollectionSheetDataUsingLeftMenu();
        CollectionSheetEntryEnterDataPage enterDataPage = selectPage.submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        enterAttendanceData(enterDataPage, attendanceValues);
        submitDataAndVerifySuccessPage(formParameters, enterDataPage);
        verifyAttendanceData(dataSetToVerify);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public CollectionSheetEntryConfirmationPage enterAndVerifyBasicAttendanceData() throws DatabaseUnitException, SQLException, IOException,
            Exception, DataSetException {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
        SubmitFormParameters formParameters = getFormParametersForCenter2();
        CollectionSheetEntryEnterDataPage enterDataPage = navigateToCollectionSheetEntryPage(formParameters);
        enterAttendanceData(enterDataPage, BASIC_ATTENDANCE_VALUES);
        CollectionSheetEntryConfirmationPage confirmationPage = submitDataAndVerifySuccessPage(formParameters, enterDataPage);
        verifyAttendanceData(getBasicAttendanceDataSet());
        return confirmationPage;
    }

    private void enterAttendanceData(CollectionSheetEntryEnterDataPage enterDataPage, int[] attendanceValues) {
        int id = 0;
        for (int attendance : attendanceValues) {
            enterDataPage.enterAttendance(id, attendance);
            id++;
        }
    }

    private CollectionSheetEntryEnterDataPage navigateToCollectionSheetEntryPage(SubmitFormParameters formParameters) {
        CollectionSheetEntrySelectPage selectPage = collectionSheetEntryTestHelper.loginAndNavigateToCollectionSheetEntrySelectPage();
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

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private void verifyAttendanceData(IDataSet attendanceDataSet) throws Exception {
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTable(dataSource, CUSTOMER_ATTENDANCE);
        dbUnitUtilities.verifyTable(CUSTOMER_ATTENDANCE, databaseDataSet, attendanceDataSet);
    }

    private IDataSet getBasicAttendanceDataSet() throws DataSetException, IOException {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        addBasicAttendanceRows(attendanceDataSet);
        return attendanceDataSet.getDataSet();
    }

    private IDataSet getTwoCenterAttendanceDataSet() throws DataSetException, IOException {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        addBasicAttendanceRows(attendanceDataSet);
        addAttendanceRow(attendanceDataSet, 5, 3, ATTENDANCE_AA);
        addAttendanceRow(attendanceDataSet, 6, 4, ATTENDANCE_A);
        addAttendanceRow(attendanceDataSet, 7, 5, ATTENDANCE_L);
        return attendanceDataSet.getDataSet();
    }

    private void addBasicAttendanceRows(SimpleDataSet attendanceDataSet) {
        addAttendanceRow(attendanceDataSet, 1, 8, ATTENDANCE_L);
        addAttendanceRow(attendanceDataSet, 2, 9, ATTENDANCE_AA);
        addAttendanceRow(attendanceDataSet, 3, 10, ATTENDANCE_A);
        addAttendanceRow(attendanceDataSet, 4, 11, ATTENDANCE_P);
    }

    private void addAttendanceRow(SimpleDataSet dataSet, int id, int customerId, int attendance) {
        dataSet.row(CUSTOMER_ATTENDANCE, "ID=" + id,"MEETING_DATE=[null]","CUSTOMER_ID=" + customerId,"ATTENDANCE=" + attendance);
    }

    private void verifyMeetingAttendances(String client, int attended, int missed) {
        ClientViewDetailsPage clientViewDetailsPage = navigationHelper.navigateToClientViewDetailsPage(client);
        clientViewDetailsPage.verifyMeetingsAttended(attended);
        clientViewDetailsPage.verifyMeetingsMissed(missed);
    }
}

