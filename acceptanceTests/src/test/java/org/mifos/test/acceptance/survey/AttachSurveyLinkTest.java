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

package org.mifos.test.acceptance.survey;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.AttachSurveyPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"acceptance", "ui", "survey"})
public class AttachSurveyLinkTest extends UiTestCaseBase {
    private NavigationHelper navigationHelper;


    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,11,14,01,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void attachSurveyToClientLoan() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_005_dbunit.xml.zip", dataSource, selenium);

        LoanAccountPage loanAccountDetailPage = navigationHelper.navigateToLoanAccountPage("000100000000004");
        loanAccountDetailPage.verifyPage();

        AttachSurveyPage attachSurveyPage = loanAccountDetailPage.navigateToAttachSurveyPage();
        attachSurveyPage.verifyPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void attachSurveyToGroupLoan() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_005_dbunit.xml.zip", dataSource, selenium);

        // ...206 is a group loan account
        LoanAccountPage loanAccountDetailPage = navigationHelper.navigateToLoanAccountPage("000100000000206");

        AttachSurveyPage attachSurveyPage = loanAccountDetailPage.navigateToAttachSurveyPage();
        attachSurveyPage.verifyPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void attachSurveyToClient() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_005_dbunit.xml.zip", dataSource, selenium);

        ClientViewDetailsPage clientViewDetailsPage = navigationHelper.navigateToClientViewDetailsPage("Stu1232993852651 Client1232993852651");

        AttachSurveyPage attachSurveyPage = clientViewDetailsPage.navigateToAttachSurveyPage();
        attachSurveyPage.verifyPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void attachSurveyToGroup() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_005_dbunit.xml.zip", dataSource, selenium);

        GroupViewDetailsPage groupViewDetailsPage = navigationHelper.navigateToGroupViewDetailsPage("MyGroup1233266255641");

        AttachSurveyPage attachSurveyPage = groupViewDetailsPage.navigateToAttachSurveyPage();
        attachSurveyPage.verifyPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void attachSurveyToCenter() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_005_dbunit.xml.zip", dataSource, selenium);

        CenterViewDetailsPage centerViewDetailsPage = navigationHelper.navigateToCenterViewDetailsPage("MyCenter1233266210775");

        AttachSurveyPage attachSurveyPage = centerViewDetailsPage.navigateToAttachSurveyPage();
        attachSurveyPage.verifyPage();
    }
}
