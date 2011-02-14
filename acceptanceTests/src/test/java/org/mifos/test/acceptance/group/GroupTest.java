/*
 * Copyright Grameen Foundation USA
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

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineAcceptedPaymentTypesPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPreviewPage;
import org.mifos.test.acceptance.framework.group.CenterSearchTransferGroupPage;
import org.mifos.test.acceptance.framework.group.ConfirmCenterMembershipPage;
import org.mifos.test.acceptance.framework.group.CreateGroupConfirmationPage;
import org.mifos.test.acceptance.framework.group.CreateGroupEntryPage;
import org.mifos.test.acceptance.framework.group.CreateGroupEntryPage.CreateGroupSubmitParameters;
import org.mifos.test.acceptance.framework.group.CreateGroupSearchPage;
import org.mifos.test.acceptance.framework.group.EditCustomerStatusParameters;
import org.mifos.test.acceptance.framework.group.GroupStatus;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.questionnaire.Choice;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
public class GroupTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    private AppLauncher appLauncher;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    private Random random;
    private NavigationHelper navigationHelper;
    private static final String NUMBER = "Number";
    private static final String SMART_SELECT = "Smart Select";

    private GroupTestHelper groupTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod(groups = {"smoke","group","acceptance","ui"})
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        navigationHelper = new NavigationHelper(selenium);
        random = new Random();
        groupTestHelper = new GroupTestHelper(selenium);
    }

    @AfterMethod(groups = {"smoke","group","acceptance","ui"})
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-247
    @Test(sequential = true, groups = {"smoke","group","acceptance","ui"})
    public void verifyAcceptedPaymentTypesForGroup() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        //When
        GroupTestHelper groupTestHelper = new GroupTestHelper(selenium);
        CreateGroupSubmitParameters groupParams = new CreateGroupSubmitParameters();
        groupParams.setGroupName("TestGroup123456");
        groupTestHelper.createNewGroup("MyCenter1232993841778", groupParams);

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineAcceptedPaymentTypesPage defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addLoanFeesPaymentType(defineAcceptedPaymentTypesPage.CHEQUE);

        adminPage = navigationHelper.navigateToAdminPage();
        defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addLoanFeesPaymentType(defineAcceptedPaymentTypesPage.VOUCHER);

        ApplyPaymentPage applyPaymentPage = navigationHelper.navigateToGroupViewDetailsPage("TestGroup123456")
                                            .navigateToViewGroupChargesDetailPage().navigateToApplyPayments();
        //Then
        applyPaymentPage.verifyModeOfPayments();

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @Test(sequential = true, groups = {"group","acceptance","ui"})
    public void testHitGroupDashboard() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        SearchResultsPage searchResultsPage = homePage.search("mygroup");
        searchResultsPage.verifyPage();
        // click on any search result leading to a group dashboard
        searchResultsPage.navigateToGroupViewDetailsPage("link=MyGroup*");
    }

    @Test(sequential = true, groups = {"smoke","group","acceptance","ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    // http://mifosforge.jira.com/browse/MIFOSTEST-301
    public void createGroupInPendingApprovalStateTest() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
        //When
        CreateGroupEntryPage groupEntryPage = loginAndNavigateToNewGroupPage();
        CreateGroupSubmitParameters formParameters = getGenericGroupFormParameters();
        CreateGroupConfirmationPage confirmationPage = groupEntryPage.submitNewGroupForApproval(formParameters);
        confirmationPage.verifyPage();
        GroupViewDetailsPage groupDetailsPage = confirmationPage.navigateToGroupDetailsPage();
        //Then
        groupDetailsPage.verifyStatus("Application Pending*");
        //When
        CustomerChangeStatusPage customerChangeStatusPage = groupDetailsPage.navigateToEditGroupStatusPage();
        EditCustomerStatusParameters editCustomerStatusParameters = new EditCustomerStatusParameters();
        editCustomerStatusParameters.setGroupStatus(GroupStatus.ACTIVE);
        editCustomerStatusParameters.setNote("test");
        CustomerChangeStatusPreviewPage customerChangeStatusPreviewPage = customerChangeStatusPage.setChangeStatusParametersAndSubmit(editCustomerStatusParameters);
        GroupViewDetailsPage detailsPage = customerChangeStatusPreviewPage.navigateToGroupDetailsPage();
        //Then
        detailsPage.verifyStatus("Active*");
    }

    @Test(sequential = true, groups = {"group","acceptance","ui"})
    // http://mifosforge.jira.com/browse/MIFOSTEST-300
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createGroupInPartialApplicationStateTest() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
        //When
        CreateGroupEntryPage groupEntryPage = loginAndNavigateToNewGroupPage();
        CreateGroupSubmitParameters formParameters = getGenericGroupFormParameters();
        CreateGroupConfirmationPage confirmationPage = groupEntryPage.submitNewGroupForPartialApplication(formParameters);
        confirmationPage.verifyPage();
        GroupViewDetailsPage groupDetailsPage = confirmationPage.navigateToGroupDetailsPage();
        //Then
        groupDetailsPage.verifyStatus("Partial Application*");
        //When
        CustomerChangeStatusPage customerChangeStatusPage = groupDetailsPage.navigateToEditGroupStatusPage();
        EditCustomerStatusParameters editCustomerStatusParameters = new EditCustomerStatusParameters();
        editCustomerStatusParameters.setGroupStatus(GroupStatus.PENDING_APPROVAL);
        editCustomerStatusParameters.setNote("test");
        CustomerChangeStatusPreviewPage customerChangeStatusPreviewPage = customerChangeStatusPage.setChangeStatusParametersAndSubmit(editCustomerStatusParameters);
        GroupViewDetailsPage detailsPage = customerChangeStatusPreviewPage.navigateToGroupDetailsPage();
        //Then
        detailsPage.verifyStatus("Application Pending Approval*");
    }

    @Test(sequential = true, groups = {"group","acceptance","ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void changeCenterMembership() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
        CreateGroupEntryPage groupEntryPage = loginAndNavigateToNewGroupPage();
        CreateGroupSubmitParameters formParameters = getGenericGroupFormParameters();
        CreateGroupConfirmationPage confirmationPage = groupEntryPage.submitNewGroupForApproval(formParameters);
        confirmationPage.verifyPage();
        GroupViewDetailsPage groupDetailsPage = confirmationPage.navigateToGroupDetailsPage();
        CenterSearchTransferGroupPage centerSearchTransfer = groupDetailsPage.editCenterMembership();
        centerSearchTransfer.verifyPage();
        ConfirmCenterMembershipPage confirmMembership = centerSearchTransfer.search("Center3");
        confirmMembership.verifyPage();
        groupDetailsPage = confirmMembership.submitMembershipChange();
        groupDetailsPage.verifyLoanOfficer(" Loan officer: Jenna Barth");
    }

    @Test(sequential = true, groups = {"smoke","group","acceptance","ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createGroupInPendingApprovalStateTestWithSurveys() throws Exception {
        String questionGroupTitle = "QG1" + random.nextInt(100);
        String question1 = "Nu_" + random.nextInt(100);
        String question2 = "SS_" + random.nextInt(100);
        List<Choice> choices = asList(new Choice("Choice1", asList("Tag1", "Tag2")), new Choice("Choice2", asList("Tag3", "Tag4")));
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
        createQuestionGroupForCreateGroup(questionGroupTitle, question1, question2, choices);
        CreateGroupEntryPage groupEntryPage = loginAndNavigateToNewGroupPage();
        CreateGroupSubmitParameters formParameters = getGenericGroupFormParameters();
        QuestionResponsePage questionResponsePage = groupEntryPage.submitNewGroupAndNavigateToQuestionResponsePage(formParameters);
        questionResponsePage.verifyPage();
        questionResponsePage.verifyNumericBoundsValidation("name=questionGroups[0].sectionDetails[0].questions[0].value", "1000", 10, 100, question1);
        questionResponsePage.populateTextAnswer("name=questionGroups[0].sectionDetails[0].questions[0].value", "30");
        questionResponsePage.populateSmartSelect("txtListSearch", getChoiceTags());
        GroupViewDetailsPage groupViewDetailsPage = questionResponsePage.navigateToCreateGroupDetailsPage("Application Pending*");
        ViewQuestionResponseDetailPage responsePage = groupViewDetailsPage.navigateToViewAdditionalInformationPage();
        responsePage.verifyPage();
        responsePage.verifyQuestionPresent(question1, "30");
        responsePage.verifyQuestionPresent(question2, "Choice", "Choice2");
        responsePage.navigateToDetailsPage();
    }

    /**
     * Verify when Pending Approval (Groups) is set to default(true);
     * the system transitions the account to this state when creating new groups
     * http://mifosforge.jira.com/browse/MIFOSTEST-210
     * @throws Exception
     */
    @Test(groups = {"smoke","group","acceptance","ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void verifyPendingApprovalSetToDefault() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        CreateGroupSubmitParameters groupParams = getGenericGroupFormParameters();
        String centerName = "MyCenter1233171688286";

        GroupViewDetailsPage groupViewDetailsPage = groupTestHelper.createNewGroup(centerName, groupParams);

        groupViewDetailsPage.verifyStatus(GroupViewDetailsPage.STATUS_PENDING_APPROVAL);
    }

    /**
     * Create group and change center membership for group
     * http://mifosforge.jira.com/browse/MIFOSTEST-655
     * @throws Exception
     */
    @Test(groups = {"smoke","group","acceptance","ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyChangeCenterMembership() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        String centerName = "MyCenter1233171688286";
        String newCenterName = "MyCenter1232993841778";
        String groupName = "Group655";
        CreateGroupSubmitParameters groupParams = getGenericGroupFormParameters();
        groupParams.setGroupName(groupName);
        EditCustomerStatusParameters groupStatusParams = new EditCustomerStatusParameters();
        groupStatusParams.setNote("note");

        GroupViewDetailsPage groupViewDetailsPage = groupTestHelper.createNewGroupPartialApplication(centerName, groupParams);
        groupViewDetailsPage.verifyStatus(GroupViewDetailsPage.STATUS_PARTIAL_APPLICATION);
        groupStatusParams.setGroupStatus(GroupStatus.PENDING_APPROVAL);
        groupTestHelper.changeGroupStatus(groupName, groupStatusParams);
        groupStatusParams.setGroupStatus(GroupStatus.ACTIVE);
        groupViewDetailsPage = groupTestHelper.changeGroupStatus(groupName, groupStatusParams);
        groupViewDetailsPage.verifyStatus(GroupViewDetailsPage.STATUS_ACTIVE);
        groupViewDetailsPage = groupTestHelper.changeGroupCenterMembership(groupName, newCenterName);

        groupViewDetailsPage.navigateToGroupsCenter(newCenterName);
    }

    private Map<String, String> getChoiceTags() {
        Map<String,String> tags = new HashMap<String, String>();
        tags.put("Tag1", "Choice1");
        tags.put("Tag3", "Choice2");
        return tags;
    }

    private void createQuestionGroupForCreateGroup(String questionGroupTitle, String question1, String question2, List<Choice> choices) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        createQuestionPage.addQuestion(getCreateQuestionParams(question1, NUMBER, 10, 100, null));
        createQuestionPage.addQuestion(getCreateQuestionParams(question2, SMART_SELECT, null, null, choices));
        adminPage = createQuestionPage.submitQuestions();

        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage();
        CreateQuestionGroupParameters parameters = getCreateQuestionGroupParameters(questionGroupTitle, asList(question1, question2));
        for(String section : parameters.getExistingQuestions().keySet()){
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(asList(question1));
        createQuestionGroupPage.submit(parameters);
    }

    private CreateQuestionParameters getCreateQuestionParams(String title, String type, Integer numericMin, Integer numericMax, List<Choice> choices) {
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        parameters.setText(title);
        parameters.setType(type);
        parameters.setChoices(choices);
        parameters.setNumericMin(numericMin);
        parameters.setNumericMax(numericMax);
        return parameters;
    }

    private CreateQuestionGroupParameters getCreateQuestionGroupParameters(String questionGroupTitle, List<String> questions) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(questionGroupTitle);
        parameters.setAppliesTo("Create Group");
        parameters.setAnswerEditable(true);
        for (String question : questions) {
            parameters.addExistingQuestion("Default Section", question);
        }
        return parameters;
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
