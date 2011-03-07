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

package org.mifos.test.acceptance.survey;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.AttachSurveyPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"acceptance", "ui", "survey", "no_db_unit"})
public class AttachSurveyLinkTest extends UiTestCaseBase {
    private NavigationHelper navigationHelper;


    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009, 7, 11, 14, 01, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void attachQuestionnaireTest() throws Exception {
        attachSurveyToCenter();
        attachSurveyToClient();
        attachSurveyToGroup();
        attachSurveyToClientLoan();
        attachSurveyToGroupLoan();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void attachSurveyToClientLoan() throws Exception {
        LoanAccountPage loanAccountDetailPage = navigationHelper.navigateToLoanAccountPage("000100000000012");
        AttachSurveyPage attachSurveyPage = loanAccountDetailPage.navigateToAttachSurveyPage();
        attachSurveyPage.verifyPage("selectQuestionnaire");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void attachSurveyToGroupLoan() throws Exception {
        // ...206 is a group loan account
        LoanAccountPage loanAccountDetailPage = navigationHelper.navigateToLoanAccountPage("000100000000011");
        AttachSurveyPage attachSurveyPage = loanAccountDetailPage.navigateToAttachSurveyPage();
        attachSurveyPage.verifyPage("selectQuestionnaire");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void attachSurveyToClient() throws Exception {
        ClientViewDetailsPage clientViewDetailsPage = navigationHelper.navigateToClientViewDetailsPage("WeeklyClient Monday");
        AttachSurveyPage attachSurveyPage = clientViewDetailsPage.navigateToAttachSurveyPage();
        attachSurveyPage.verifyPage("selectQuestionnaire");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void attachSurveyToGroup() throws Exception {
        GroupViewDetailsPage groupViewDetailsPage = navigationHelper.navigateToGroupViewDetailsPage("Default Group");
        AttachSurveyPage attachSurveyPage = groupViewDetailsPage.navigateToAttachSurveyPage();
        attachSurveyPage.verifyPage("selectQuestionnaire");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void attachSurveyToCenter() throws Exception {
        CenterViewDetailsPage centerViewDetailsPage = navigationHelper.navigateToCenterViewDetailsPage("Default Center");
        AttachSurveyPage attachSurveyPage = centerViewDetailsPage.navigateToAttachSurveyPage();
        attachSurveyPage.verifyPage("selectQuestionnaire");
    }
}
