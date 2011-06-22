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

package org.mifos.test.acceptance.properties;

import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.center.CreateCenterEnterDataPage;
import org.mifos.test.acceptance.framework.center.CreateMeetingPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.group.CreateGroupEntryPage.CreateGroupSubmitParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.CenterTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups={"acceptance","ui", "properties", "no_db_unit"})
public class UpdateCustomPropertiesTest extends UiTestCaseBase {
    NavigationHelper navigationHelper;
    CustomPropertiesHelper propertiesHelper;
    SavingsAccountHelper savingsAccountHelper;
    CenterTestHelper centerTestHelper;
    ClientTestHelper clientTestHelper;

    String errorInterestExceedsLimit = "The max interest is invalid because it is not in between";
    String errorInterestDigitsAfterDecimal ="The max interest is invalid because the number of digits after the decimal separator";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        navigationHelper = new NavigationHelper(selenium);
        propertiesHelper = new CustomPropertiesHelper(selenium);
        savingsAccountHelper = new SavingsAccountHelper(selenium);
        centerTestHelper = new CenterTestHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
        super.setUp();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-228
    @Test(enabled=true)
    public void verifyPropertyBackDatedTransactionsAllowedFalse() throws Exception{
        //Given
        propertiesHelper.setBackDatedTransactionsAllowed("false");
        //When
        navigationHelper.navigateToLoanAccountPage("000100000000020").navigateToDisburseLoan().verifyDisbursalDateIsDisabled();
        //Then
        propertiesHelper.setBackDatedTransactionsAllowed("true");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-235
    @Test(enabled=true)
    public void verifyPropertyClientRulesClientCanExistOutsideGroupFalse() throws Exception{
        //Given
        propertiesHelper.setClientCanExistOutsideGroup("false");
        //When
        navigationHelper.navigateToClientsAndAccountsPage().navigateToCreateNewClientPage();
        //Then
        Assert.assertFalse(selenium.isElementPresent("group_search.link.membershipNotRequired"));
        propertiesHelper.setClientCanExistOutsideGroup("true");
    }

    /*
     * FIXME - keithw - test passes when run individually but not as part of ci build. it appears that question groups data is not
     *                  cleaned up right as instead of stepping to review installments, the questionnaire page is presented.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-234
    @Test(enabled=true)
    public void verifyPropertyGroupCanApplyLoansTrue() throws Exception{
        //Given
        propertiesHelper.setGroupCanApplyLoans("true");
        LoanTestHelper helper = new LoanTestHelper(selenium);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("UpdateCustomPropertiesTestGroup");
        searchParameters.setLoanProduct("GroupEmergencyLoan");
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("2000.0");
        //When Then
        helper.createLoanAccount(searchParameters, submitAccountParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-233
    @Test(enabled=true)
    public void verifyPropertyGroupCanApplyLoansFalse() throws Exception{
        //Given
        propertiesHelper.setGroupCanApplyLoans("false");
        //When
        navigationHelper.navigateToGroupViewDetailsPage("Default Group");
        //Then
        Assert.assertFalse(selenium.isElementPresent("viewgroupdetails.link.newLoanAccount"));
        propertiesHelper.setGroupCanApplyLoans("true");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-232
    @Test(enabled=true)
    public void verifyPropertyClientRulesCenterHierarchyExistsFalse() throws Exception{
        //Given
        propertiesHelper.setCenterHierarchyExists("false");

        //When
        ClientsAndAccountsHomepage clientsAndAccountsHomePage =  navigationHelper.navigateToClientsAndAccountsPage();

        //Then
        clientsAndAccountsHomePage.verifyMenuWithCenterHierarchyOff();

        //Given
        propertiesHelper.setCenterHierarchyExists("true");

        //When
        clientsAndAccountsHomePage = navigationHelper.navigateToClientsAndAccountsPage();

        //Then
        clientsAndAccountsHomePage.verifyMenuWithCenterHierarchyOn();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-231
    @Test(enabled=true)
    public void verifyPropertyClientRulesCenterHierarchyExistsTrue() throws Exception{
        //Given
        propertiesHelper.setCenterHierarchyExists("true");

        //When
        CreateCenterEnterDataPage.SubmitFormParameters formParameters = new CreateCenterEnterDataPage.SubmitFormParameters();
        formParameters.setCenterName("testCenterName12123");
        formParameters.setLoanOfficer("loan officer");
        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("Bangalore");
        meeting.setWeekDay(MeetingParameters.WeekDay.WEDNESDAY);
        meeting.setWeekFrequency("1");
        formParameters.setMeeting(meeting);
        CenterViewDetailsPage centerViewDetailsPage = centerTestHelper.createCenter(formParameters, "MyOfficeDHMFT");

        //Then
        centerViewDetailsPage.verifyActiveCenter(formParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-216
    @Test(enabled=true)
    public void verifyPropertySavingsPendingApprovalStateEnabled() throws Exception{
        //Given
        propertiesHelper.setSavingsPendingApprovalStateEnabled("false");
        //When
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString("UpdateCustomProperties TestClient");
        searchParameters.setSavingsProduct("MandatorySavingsAccount");
        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("248.0");
        SavingsAccountDetailPage savingsAccountPage = savingsAccountHelper.createSavingsAccountWithoutPendingApprovalState(searchParameters, submitAccountParameters);
        savingsAccountPage.verifyPage();
        //Then
        savingsAccountPage.verifySavingsAmount(submitAccountParameters.getAmount());
        savingsAccountPage.verifySavingsProduct(searchParameters.getSavingsProduct());
        savingsAccountPage.verifyStatus("Active");

        propertiesHelper.setSavingsPendingApprovalStateEnabled("true");
    }

    /*
     * FIXME - keithw - test passes when run individually but not as part of ci build. it appears that question groups data is not
     *                  cleaned up right as instead of stepping to review installments, the questionnaire page is presented.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-215
    @Test(enabled=true)
    public void verifyPropertyPendingApprovalStateEnabledForSavingsAndLoanAccounts() throws Exception{
        //Given
        propertiesHelper.setSavingsPendingApprovalStateEnabled("true");
        propertiesHelper.setLoanPendingApprovalStateEnabled("true");
        propertiesHelper.setGroupPendingApprovalStateEnabled("true");
        //When
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString("UpdateCustomProperties TestClient");
        searchParameters.setSavingsProduct("MonthlyClientSavingsAccount");

        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("248.0");

        SavingsAccountDetailPage savingsAccountPage = savingsAccountHelper.createSavingsAccount(searchParameters, submitAccountParameters);
        savingsAccountPage.verifyPage();
        //Then
        savingsAccountPage.verifySavingsAmount(submitAccountParameters.getAmount());
        savingsAccountPage.verifySavingsProduct(searchParameters.getSavingsProduct());
        savingsAccountPage.verifyStatus("Application Pending Approval");
        //when
        CreateLoanAccountSearchParameters searchParameters2 = new CreateLoanAccountSearchParameters();
        searchParameters2.setSearchString("UpdateCustomProperties TestClient");
        searchParameters2.setLoanProduct("ClientEmergencyLoan");
        CreateLoanAccountSubmitParameters submitAccountParameters2 = new CreateLoanAccountSubmitParameters();
        submitAccountParameters2.setAmount("2765.0");
        submitAccountParameters2.setGracePeriodTypeNone(true);
        LoanTestHelper loanTestHelper = new LoanTestHelper(selenium);
        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters2, submitAccountParameters2);
        loanAccountPage.verifyStatus("Application Pending Approval");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-211
    @Test(enabled=true)
    public void verifyPropertyGroupPendingApprovalStateEnabled() throws Exception{
        //Given
        propertiesHelper.setGroupPendingApprovalStateEnabled("false");
        //When
        GroupTestHelper groupTestHelper = new GroupTestHelper(selenium);
        CreateGroupSubmitParameters groupParams = new CreateGroupSubmitParameters();
        groupParams.setGroupName("testGroup123123123123");
        GroupViewDetailsPage groupViewDetailsPage = groupTestHelper.createNewGroupWithoutPendingForApproval("Default Center" , groupParams);
        //Then
        groupViewDetailsPage.verifyStatus("Active");
        propertiesHelper.setGroupPendingApprovalStateEnabled("true");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-86
    @Test(enabled=true)
    public void changeLocale() {
        // Given
        propertiesHelper.setLocale("FR", "FR");

        // When
        HomePage homePage = navigationHelper.navigateToHomePage();

        // Then
        Assert.assertEquals(homePage.getWelcome(), "Bienvenue,  mifos");

        // cleanup
        propertiesHelper.setLocale("EN", "GB");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-200
    @Test(enabled=true)
    public void changeDigitsAfterDecimal() throws Exception {

        propertiesHelper.setDigitsAfterDecimal(3);

        LoanAccountPage loanAccountPage  = navigationHelper.navigateToLoanAccountPage("000100000000015");
        loanAccountPage.verifyExactLoanAmount("1000.000");

        propertiesHelper.setDigitsAfterDecimal(2);

        loanAccountPage  = navigationHelper.navigateToLoanAccountPage("000100000000015");
        loanAccountPage.verifyExactLoanAmount("1000.00");

        propertiesHelper.setDigitsAfterDecimal(0);

        loanAccountPage  = navigationHelper.navigateToLoanAccountPage("000100000000015");
        loanAccountPage.verifyExactLoanAmount("1000");

        propertiesHelper.setDigitsAfterDecimal(1);

        loanAccountPage  = navigationHelper.navigateToLoanAccountPage("000100000000015");
        loanAccountPage.verifyExactLoanAmount("1000.0");

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void changeMinInterestRateToTwelve() throws Exception {
        propertiesHelper.setMinInterest(12);
        SubmitFormParameters  submitFormParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        submitFormParameters.setMaxInterestRate("8");//invalid value
        submitFormParameters.setMinInterestRate("1");
        submitFormParameters.setDefaultInterestRate("3");
        verifyInvalidInterestInLoanProduct(submitFormParameters,true);
        propertiesHelper.setMinInterest(0);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void changeMaxInterestRateToFive() throws Exception {
        propertiesHelper.setMaxInterest(5);
        SubmitFormParameters  submitFormParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        submitFormParameters.setMaxInterestRate("12");//invalid value
        submitFormParameters.setMinInterestRate("1");
        submitFormParameters.setDefaultInterestRate("3");
        verifyInvalidInterestInLoanProduct(submitFormParameters,true);
        propertiesHelper.setMaxInterest(999);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void changeDigitsAfterDecimalForInterestToThree() throws Exception {
        propertiesHelper.setDigitsAfterDecimalForInterest(3);
        SubmitFormParameters  submitFormParameters =FormParametersHelper.getWeeklyLoanProductParameters();
        submitFormParameters.setMaxInterestRate("6.33333");//invalid value
        submitFormParameters.setMinInterestRate("1");
        submitFormParameters.setDefaultInterestRate("3");
        verifyInvalidInterestInLoanProduct(submitFormParameters,false);
        propertiesHelper.setDigitsAfterDecimalForInterest(5);
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-204
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void removeThursdayFromWorkingDays() throws Exception {

        String workingDays ="Monday,Tuesday,Wednesday,Thursday,Friday,Saturday";
        propertiesHelper.setWorkingDays(workingDays);
        //When
        CreateCenterEnterDataPage createCenterEnterDataPage = navigationHelper.navigateToCreateCenterEnterDataPage("MyOfficeDHMFT");
        CreateMeetingPage createMeetingPage = createCenterEnterDataPage.navigateToCreateMeetingPage();
        createMeetingPage.verifyWorkingDays(workingDays);

        CreateClientEnterMfiDataPage createClientEnterMfiDataPage = navigationHelper.navigateToCreateClientEnterMfiDataPage("MyOfficeDHMFT");
        createMeetingPage = createClientEnterMfiDataPage.navigateToCreateMeetingPage();
        createMeetingPage.verifyWorkingDays(workingDays);

        workingDays ="Monday,Tuesday,Wednesday,Friday,Saturday";
        propertiesHelper.setWorkingDays(workingDays);

        //Then
        createCenterEnterDataPage = navigationHelper.navigateToCreateCenterEnterDataPage("MyOfficeDHMFT");
        createMeetingPage = createCenterEnterDataPage.navigateToCreateMeetingPage();
        createMeetingPage.verifyWorkingDays(workingDays);

        createClientEnterMfiDataPage = navigationHelper.navigateToCreateClientEnterMfiDataPage("MyOfficeDHMFT");
        createMeetingPage = createClientEnterMfiDataPage.navigateToCreateMeetingPage();
        createMeetingPage.verifyWorkingDays(workingDays);

        String groupName = "testGroup";

        CreateCenterEnterDataPage.SubmitFormParameters formParameters = new CreateCenterEnterDataPage.SubmitFormParameters();
        formParameters = setCenterParameters();
        centerTestHelper.createCenter(formParameters, "MyOfficeDHMFT");

        CreateGroupSubmitParameters groupParams = new CreateGroupSubmitParameters();
        groupParams.setGroupName(groupName);
        GroupTestHelper groupTestHelper = new GroupTestHelper(selenium);
        groupTestHelper.createNewGroupPartialApplication("Default Center", groupParams);

        clientTestHelper.createClientAndVerify("loan officer", "MyOfficeDHMFT");

        // restore original configuration
        propertiesHelper.setWorkingDays("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday");
    }

    private CreateCenterEnterDataPage.SubmitFormParameters setCenterParameters() {
        CreateCenterEnterDataPage.SubmitFormParameters formParameters = new CreateCenterEnterDataPage.SubmitFormParameters();
        formParameters.setCenterName("CenterNameTest123456");
        formParameters.setLoanOfficer("loan officer");
        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("testMeetingPlace");
        meeting.setWeekFrequency("1");
        meeting.setWeekDay(MeetingParameters.WeekDay.MONDAY);
        formParameters.setMeeting(meeting);
        return  formParameters;
    }

    private void verifyInvalidInterestInLoanProduct(SubmitFormParameters formParameters,boolean checkInterestExceedsLimit)
    {
        DefineNewLoanProductPage newLoanPage = navigationHelper.navigateToDefineNewLoanProductPage();
        newLoanPage.fillLoanParameters(formParameters);
        DefineNewLoanProductPage previewPage = newLoanPage.submitWithErrors();
        if(checkInterestExceedsLimit)
        {
            previewPage.verifyErrorInForm(errorInterestExceedsLimit);
        }
        else
        {
            previewPage.verifyErrorInForm(errorInterestDigitsAfterDecimal);
        }
    }

}
