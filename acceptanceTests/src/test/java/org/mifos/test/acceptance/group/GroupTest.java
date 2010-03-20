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

package org.mifos.test.acceptance.group;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.group.CenterSearchTransferGroupPage;
import org.mifos.test.acceptance.framework.group.ConfirmCenterMembershipPage;
import org.mifos.test.acceptance.framework.group.CreateGroupConfirmationPage;
import org.mifos.test.acceptance.framework.group.CreateGroupEntryPage;
import org.mifos.test.acceptance.framework.group.CreateGroupSearchPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.group.CreateGroupEntryPage.CreateGroupSubmitParameters;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
public class GroupTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    private AppLauncher appLauncher;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod(groups = {"smoke","group","acceptance","ui"})
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod(groups = {"smoke","group","acceptance","ui"})
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @Test(sequential = true, groups = {"group","acceptance","ui"})
    public void testHitGroupDashboard() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        SearchResultsPage searchResultsPage = homePage.search("mygroup");
        searchResultsPage.verifyPage();
        // click on any search result leading to a group dashboard
        GroupViewDetailsPage groupViewDetailsPage = searchResultsPage.navigateToGroupViewDetailsPage("link=MyGroup*");
        groupViewDetailsPage.verifyPage();
    }

    @Test(sequential = true, groups = {"smoke","group","acceptance","ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createGroupInPendingApprovalStateTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);
        CreateGroupEntryPage groupEntryPage = loginAndNavigateToNewGroupPage();
        CreateGroupSubmitParameters formParameters = getGenericGroupFormParameters();
        CreateGroupConfirmationPage confirmationPage = groupEntryPage.submitNewGroupForApproval(formParameters);
        confirmationPage.verifyPage();
        GroupViewDetailsPage groupDetailsPage = confirmationPage.navigateToGroupDetailsPage();
        groupDetailsPage.verifyPage();
        groupDetailsPage.verifyStatus("Application Pending*");
    }

    @Test(sequential = true, groups = {"group","acceptance","ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createGroupInPartialApplicationStateTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);
        CreateGroupEntryPage groupEntryPage = loginAndNavigateToNewGroupPage();
        CreateGroupSubmitParameters formParameters = getGenericGroupFormParameters();
        CreateGroupConfirmationPage confirmationPage = groupEntryPage.submitNewGroupForPartialApplication(formParameters);
        confirmationPage.verifyPage();
        GroupViewDetailsPage groupDetailsPage = confirmationPage.navigateToGroupDetailsPage();
        groupDetailsPage.verifyPage();
        groupDetailsPage.verifyStatus("Partial Application*");
    }

    @Test(sequential = true, groups = {"group","acceptance","ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void changeCenterMembership() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);
        CreateGroupEntryPage groupEntryPage = loginAndNavigateToNewGroupPage();
        CreateGroupSubmitParameters formParameters = getGenericGroupFormParameters();
        CreateGroupConfirmationPage confirmationPage = groupEntryPage.submitNewGroupForApproval(formParameters);
        confirmationPage.verifyPage();
        GroupViewDetailsPage groupDetailsPage = confirmationPage.navigateToGroupDetailsPage();
        groupDetailsPage.verifyPage();
        CenterSearchTransferGroupPage centerSearchTransfer = groupDetailsPage.editCenterMembership();
        centerSearchTransfer.verifyPage();
        ConfirmCenterMembershipPage confirmMembership = centerSearchTransfer.search("Center3");
        confirmMembership.verifyPage();
        groupDetailsPage = confirmMembership.submitMembershipChange();
        groupDetailsPage.verifyPage();
        groupDetailsPage.verifyLoanOfficer(" Loan officer: Jenna Barth");
    }


    private CreateGroupEntryPage loginAndNavigateToNewGroupPage() {
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        String centerName = "Center1";
        CreateGroupSearchPage groupSearchPage = homePage.navigateToCreateNewGroupSearchPage();
        groupSearchPage.verifyPage();
        return  groupSearchPage.searchAndNavigateToCreateGroupPage(centerName);
    }

    private CreateGroupSubmitParameters getGenericGroupFormParameters() {
        CreateGroupSubmitParameters formParameters = new CreateGroupSubmitParameters();
        formParameters.setGroupName("groupTest" + StringUtil.getRandomString(6));
        formParameters.setRecruitedBy("Bagonza Wilson");
        formParameters.setTrainedDateDay("25");
        formParameters.setTrainedDateMonth("03");
        formParameters.setTrainedDateYear("1999");
        formParameters.setExternalId("external12345");
        formParameters.setAddressOne("address one: 4321 Pine Street");
        formParameters.setAddressTwo("address two: P.O. Box 99");
        formParameters.setAddressThree("address three: suite 322");
        formParameters.setCity("Circuit City");
        formParameters.setState("Garden State");
        formParameters.setCountry("Elbonia");
        formParameters.setPostalCode("33AB3");
        formParameters.setTelephone("88855533322");
        return formParameters;
    }


}
