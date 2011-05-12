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

package org.mifos.test.acceptance.collectionsheet;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CollectionSheetEntryTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"collectionsheet","acceptance","ui"})
public class CollectionSheetEntryAttendanceTest extends UiTestCaseBase {

    private static final int ATTENDANCE_P = CollectionSheetEntryEnterDataPage.ATTENDANCE_P;
    private static final int ATTENDANCE_A = CollectionSheetEntryEnterDataPage.ATTENDANCE_A;
    private static final int ATTENDANCE_AA = CollectionSheetEntryEnterDataPage.ATTENDANCE_AA;
    private static final int ATTENDANCE_L = CollectionSheetEntryEnterDataPage.ATTENDANCE_L;

    private CollectionSheetEntryTestHelper collectionSheetEntryTestHelper;
    private NavigationHelper navigationHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium, applicationDatabaseOperation);
        DateTime targetTime = new DateTime(2009,7,23,1,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTimeWithMifosLastLoginUpdate(targetTime);

        collectionSheetEntryTestHelper = new CollectionSheetEntryTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * Verify Entering attendance for one center doesn't affect other centers.
     * http://mifosforge.jira.com/browse/MIFOSTEST-5
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyEnteringAttendanceForOneCenterDoesntAffectOtherCenters() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium, applicationDatabaseOperation);
        int[] overwriteAttendanceValues = new int[] { ATTENDANCE_L, ATTENDANCE_AA, ATTENDANCE_A, ATTENDANCE_P };
        int[] basicAttendanceValues = new int[] { ATTENDANCE_P, ATTENDANCE_P, ATTENDANCE_P, ATTENDANCE_L };
        int[] secondCenterAttendance = new int[] { ATTENDANCE_AA, ATTENDANCE_A, ATTENDANCE_L };
        SubmitFormParameters collectionSheetParams = getFormParametersForCenter2();
        SubmitFormParameters collectionSheetParams2 = getFormParametersForCenter1();
        int[] defaultAttendanceValues = {ATTENDANCE_P, ATTENDANCE_P, ATTENDANCE_P, ATTENDANCE_P};
        int[] defaultAttendanceValues2 = {ATTENDANCE_P, ATTENDANCE_P, ATTENDANCE_P};
        String[] clients = {"Teja Kakarla", "Aarif Mawani", "Mutu Juma", "Anna Martin"};
        String[] clients2 = {"Veronica Abisya", "Dauda Mayowa", "Polly Gikonyo"};
        String[] clientsNotAffected = {"Tesa Mendez", "Megana Martin", "Reyna Tabilin", "Mary Asanti"};
        int[] meetingsAttended11 = {1, 1, 1, 1};
        int[] meetingsMissed11 = {0, 0, 0, 0};
        int[] meetingsAttended12 = {1, 0, 0, 1};
        int[] meetingsMissed12 = {0, 1, 1, 0};
        int[] meetingsAttended22 = {0, 0, 1};
        int[] meetingsMissed22 = {1, 1, 0};

        collectionSheetEntryTestHelper.submitCollectionSheetWithChangedAttendance(collectionSheetParams, defaultAttendanceValues, basicAttendanceValues);
        for(int i = 0; i < clients.length; i++) {
            verifyMeetingAttendances(clients[i], meetingsAttended11[i], meetingsMissed11[i]);
        }
        collectionSheetEntryTestHelper.submitCollectionSheetWithChangedAttendance(collectionSheetParams, basicAttendanceValues, overwriteAttendanceValues);
        collectionSheetEntryTestHelper.submitCollectionSheetWithChangedAttendance(collectionSheetParams2, defaultAttendanceValues2, secondCenterAttendance);
        for(int i = 0; i < clients.length; i++) {
            verifyMeetingAttendances(clients[i], meetingsAttended12[i], meetingsMissed12[i]);
        }
        for(int i = 0; i < clients2.length; i++) {
            verifyMeetingAttendances(clients2[i], meetingsAttended22[i], meetingsMissed22[i]);
        }
        for (String element : clientsNotAffected) {
            verifyMeetingAttendances(element, 0, 0);
        }
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

    private void verifyMeetingAttendances(String client, int attended, int missed) {
        ClientViewDetailsPage clientViewDetailsPage = navigationHelper.navigateToClientViewDetailsPage(client);
        clientViewDetailsPage.verifyMeetingsAttended(attended);
        clientViewDetailsPage.verifyMeetingsMissed(missed);
    }
}

