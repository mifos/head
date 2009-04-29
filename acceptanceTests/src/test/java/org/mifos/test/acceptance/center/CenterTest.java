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
 
package org.mifos.test.acceptance.center;

import org.mifos.test.acceptance.framework.AdminPage;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.CreateCenterChooseOfficePage;
import org.mifos.test.acceptance.framework.CreateCenterConfirmationPage;
import org.mifos.test.acceptance.framework.CreateCenterEnterDataPage;
import org.mifos.test.acceptance.framework.CreateCenterPreviewDataPage;
import org.mifos.test.acceptance.framework.CreateMeetingPage;
import org.mifos.test.acceptance.framework.CreateUserEnterDataPage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.UserViewDetailsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"createCenterStory","acceptance","ui"})
public class CenterTest extends UiTestCaseBase {

	private AppLauncher appLauncher; 
	
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
	@BeforeMethod
	public void setUp() throws Exception {
		super.setUp();
		appLauncher = new AppLauncher(selenium);
		new InitializeApplicationRemoteTestingService().reinitializeApplication(selenium);
	}

	@AfterMethod
	public void logOut() {
		(new MifosPage(selenium)).logout();
	}
	
	public void createCenterTest() {   
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();
        AdminPage adminPage = homePage.navigateToAdminPage();
        adminPage.verifyPage();
	    
        String officeName = "Bangalore Branch " + StringUtil.getRandomString(8);
        
        AdminPage adminPage2 = adminPage.createOffice(adminPage, officeName);

        CreateUserEnterDataPage.SubmitFormParameters userFormParameters = adminPage2.getAdminUserParameters();
                
        UserViewDetailsPage userDetailsPage = adminPage2.createUser(adminPage2, userFormParameters, officeName);
        
	    ClientsAndAccountsHomepage clientsAccountsPage = userDetailsPage.navigateToClientsAndAccountsHomepage();
	    
	    String centerName = "Bangalore_Center" + StringUtil.getRandomString(4);
	    String loanOfficer = userFormParameters.getFirstName() + " " + userFormParameters.getLastName();
        
        createCenter(clientsAccountsPage, getCenterParameters(centerName, loanOfficer), officeName);

	}
	
	public CenterViewDetailsPage createCenter(ClientsAndAccountsHomepage clientsAccountsPage, CreateCenterEnterDataPage.SubmitFormParameters formParameters, String officeName) {
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
        
        CreateMeetingPage.SubmitFormParameters meetingFormParameters = new CreateMeetingPage.SubmitFormParameters();
        meetingFormParameters.setWeekFrequency("1");
        meetingFormParameters.setWeekDay("Wednesday");
        meetingFormParameters.setMeetingPlace("Bangalore");
        
        formParameters.setMeeting(meetingFormParameters);
        return formParameters;
	}
	
}
