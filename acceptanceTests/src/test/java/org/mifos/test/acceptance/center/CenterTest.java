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

package org.mifos.test.acceptance.center;


import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.center.CreateCenterChooseOfficePage;
import org.mifos.test.acceptance.framework.center.CreateCenterConfirmationPage;
import org.mifos.test.acceptance.framework.center.CreateCenterEnterDataPage;
import org.mifos.test.acceptance.framework.center.CreateCenterPreviewDataPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"smoke","center","acceptance","ui"})
public class CenterTest extends UiTestCaseBase {
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

	private NavigationHelper navigationHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
	@BeforeMethod
	public void setUp() throws Exception {
		super.setUp();
		navigationHelper = new NavigationHelper(selenium);
		new InitializeApplicationRemoteTestingService().reinitializeApplication(selenium);
	}

	@AfterMethod
	public void logOut() {
		(new MifosPage(selenium)).logout();
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void createCenterTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        createCenter(getCenterParameters("Fantastico", "Joe1233171679953 Guy1233171679953"), "MyOffice1233171674227");
	}

	public CenterViewDetailsPage createCenter(CreateCenterEnterDataPage.SubmitFormParameters formParameters, String officeName) {
	    ClientsAndAccountsHomepage clientsAccountsPage = navigationHelper.navigateToClientsAndAccountsPage();

        CreateCenterChooseOfficePage chooseOfficePage = clientsAccountsPage.navigateToCreateNewCenterPage();
        CreateCenterEnterDataPage enterDataPage = chooseOfficePage.selectOffice(officeName);
	    CreateCenterPreviewDataPage centerPreviewDataPage = enterDataPage.submitAndGotoCreateCenterPreviewDataPage(formParameters);
        CreateCenterConfirmationPage confirmationPage = centerPreviewDataPage.submit();
        confirmationPage.verifyPage();
        CenterViewDetailsPage centerDetailsPage = confirmationPage.navigateToCenterViewDetailsPage();
        centerDetailsPage.verifyActiveCenter(formParameters);

        return centerDetailsPage;
	}

	public CreateCenterEnterDataPage.SubmitFormParameters getCenterParameters(String centerName, String loanOfficer) {
        CreateCenterEnterDataPage.SubmitFormParameters formParameters = new CreateCenterEnterDataPage.SubmitFormParameters();
        formParameters.setCenterName(centerName);
        formParameters.setLoanOfficer(loanOfficer);

        MeetingParameters meetingFormParameters = new MeetingParameters();
        meetingFormParameters.setWeekFrequency("1");
        meetingFormParameters.setWeekDay(MeetingParameters.WEDNESDAY);
        meetingFormParameters.setMeetingPlace("Bangalore");

        formParameters.setMeeting(meetingFormParameters);
        return formParameters;
	}

}
