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

package org.mifos.test.acceptance.collectionsheet;

import java.io.IOException;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryConfirmationPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryPreviewDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CollectionSheetEntryTestHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.framework.util.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
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

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @Test(sequential = true, groups = {"smoke"})
    public void defaultAdminUserEntersAttendanceData() throws Exception {
        enterAndVerifyBasicAttendanceData();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void secondCollectionSheetEntryOverwritesFirstAttendanceData() throws Exception {
        CollectionSheetEntryConfirmationPage confirmationPage = enterAndVerifyBasicAttendanceData();
        verifyAttendanceData(this.getBasicAttendanceDataSet());
        SubmitFormParameters formParameters = getFormParametersForCenter2();
        enterAttendanceDataForSecondCenter(confirmationPage, formParameters, OVERWRITE_ATTENDANCE_VALUES, getOverwriteAttendanceDataSet());
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void enteringAttendanceDataForOneCenterDoesNotAffectOtherCenters() throws Exception {
        CollectionSheetEntryConfirmationPage confirmationPage = enterAndVerifyBasicAttendanceData();
        verifyAttendanceData(this.getBasicAttendanceDataSet());
        SubmitFormParameters formParameters = getFormParametersForCenter1();
        enterAttendanceDataForSecondCenter(confirmationPage, formParameters, SECOND_CENTER_ATTENDANCE_VALUES, getTwoCenterAttendanceDataSet());
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
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);
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
        CollectionSheetEntrySelectPage selectPage = new CollectionSheetEntryTestHelper(selenium).loginAndNavigateToCollectionSheetEntrySelectPage();
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

    private IDataSet getOverwriteAttendanceDataSet() throws DataSetException, IOException {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        addAttendanceRow(attendanceDataSet, 1, 8, ATTENDANCE_P);
        addAttendanceRow(attendanceDataSet, 2, 9, ATTENDANCE_P);
        addAttendanceRow(attendanceDataSet, 3, 10, ATTENDANCE_P);
        addAttendanceRow(attendanceDataSet, 4, 11, ATTENDANCE_L);
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

}

