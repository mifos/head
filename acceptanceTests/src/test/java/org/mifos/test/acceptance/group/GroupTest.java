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

package org.mifos.test.acceptance.group;

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
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.questionnaire.Choice;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.Arrays.asList;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
public class GroupTest extends UiTestCaseBase {

    private AppLauncher appLauncher;
    private Random random;
    private NavigationHelper navigationHelper;
    private static final String NUMBER = "Number";
    private static final String SMART_SELECT = "Smart Select";
    private QuestionGroupTestHelper questionGroupTestHelper;
    private GroupTestHelper groupTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        navigationHelper = new NavigationHelper(selenium);
        random = new Random();
        groupTestHelper = new GroupTestHelper(selenium);
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-247
    @Test(singleThreaded = true, groups = {"group", "acceptance", "ui", "no_db_unit"})
    public void verifyAcceptedPaymentTypesForGroup() throws Exception {
        //When
        GroupTestHelper groupTestHelper = new GroupTestHelper(selenium);
        CreateGroupSubmitParameters groupParams = new CreateGroupSubmitParameters();
        groupParams.setGroupName("TestGroup123456");
        groupTestHelper.createNewGroup("Default Center", groupParams);

        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineAcceptedPaymentTypesPage defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addLoanFeesPaymentType(DefineAcceptedPaymentTypesPage.CHEQUE);

        adminPage = navigationHelper.navigateToAdminPage();
        defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addLoanFeesPaymentType(DefineAcceptedPaymentTypesPage.VOUCHER);

        ApplyPaymentPage applyPaymentPage = navigationHelper.navigateToGroupViewDetailsPage("TestGroup123456")
                .navigateToViewGroupChargesDetailPage().navigateToApplyPayments();
        //Then
        applyPaymentPage.verifyModeOfPayments();

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @Test(singleThreaded = true, groups = {"group", "acceptance", "ui", "no_db_unit"})
    public void testHitGroupDashboard() throws Exception {
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        SearchResultsPage searchResultsPage = homePage.search("Default Group");
        searchResultsPage.verifyPage();
        // click on any search result leading to a group dashboard
        searchResultsPage.navigateToGroupViewDetailsPage("link=Default Group*");
    }

    @Test(singleThreaded = true, groups = {"group", "acceptance", "ui", "no_db_unit"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    // http://mifosforge.jira.com/browse/MIFOSTEST-301
    public void createGroupInPendingApprovalStateTest() throws Exception {
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

    @Test(singleThreaded = true, groups = {"group", "acceptance", "ui", "no_db_unit"})
    // http://mifosforge.jira.com/browse/MIFOSTEST-300
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createGroupInPartialApplicationStateTest() throws Exception {
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

    @Test(singleThreaded = true, groups = {"group", "acceptance", "ui", "no_db_unit"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void changeCenterMembership() throws Exception {
        CreateGroupEntryPage groupEntryPage = loginAndNavigateToNewGroupPage();
        CreateGroupSubmitParameters formParameters = getGenericGroupFormParameters();
        CreateGroupConfirmationPage confirmationPage = groupEntryPage.submitNewGroupForApproval(formParameters);
        confirmationPage.verifyPage();
        GroupViewDetailsPage groupDetailsPage = confirmationPage.navigateToGroupDetailsPage();
        CenterSearchTransferGroupPage centerSearchTransfer = groupDetailsPage.editCenterMembership();
        centerSearchTransfer.verifyPage();
        ConfirmCenterMembershipPage confirmMembership = centerSearchTransfer.search("WeeklyMeetingCenter");
        confirmMembership.verifyPage();
        groupDetailsPage = confirmMembership.submitMembershipChange();
        groupDetailsPage.verifyLoanOfficer(" Loan officer: loan officer");
    }

    @Test(singleThreaded = true, groups = {"smoke", "group", "acceptance", "ui", "no_db_unit"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createGroupInPendingApprovalStateTestWithSurveys() throws Exception {
        QuestionGroupTestHelper questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        String questionGroupTitle = "QG1" + random.nextInt(100);
        String question1 = "Nu_" + random.nextInt(100);
        String question2 = "SS_" + random.nextInt(100);
        List<Choice> choices = asList(new Choice("Choice1", asList("Tag1", "Tag2")), new Choice("Choice2", asList("Tag3", "Tag4")));
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
        questionGroupTestHelper.markQuestionGroupAsInactive(questionGroupTitle);
    }

    /**
     * Verify when Pending Approval (Groups) is set to default(true);
     * the system transitions the account to this state when creating new groups
     * http://mifosforge.jira.com/browse/MIFOSTEST-210
     *
     * @throws Exception
     */
    @Test(groups = {"group", "acceptance", "ui", "no_db_unit"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void verifyPendingApprovalSetToDefault() throws Exception {
        CreateGroupSubmitParameters groupParams = getGenericGroupFormParameters();
        String centerName = "WeeklyMeetingCenter";

        GroupViewDetailsPage groupViewDetailsPage = groupTestHelper.createNewGroup(centerName, groupParams);

        groupViewDetailsPage.verifyStatus(GroupViewDetailsPage.STATUS_PENDING_APPROVAL);
    }

    /**
     * Create group and change center membership for group
     * http://mifosforge.jira.com/browse/MIFOSTEST-655
     *
     * @throws Exception
     */
    @Test(groups = {"group", "acceptance", "ui", "no_db_unit"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyChangeCenterMembership() throws Exception {
        String centerName = "Default Center";
        String newCenterName = "WeeklyMeetingCenter";
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

    @Test(singleThreaded = true, groups = {"group", "acceptance", "ui"}, enabled = false)
    // http://mifosforge.jira.com/browse/MIFOSTEST-682
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createGroupWithQuestionGroup() throws Exception {
        //Given

    	CreateQuestionGroupParameters questionGroupParams = new CreateQuestionGroupParameters();
    	questionGroupParams.setTitle("CreateGroupQG");
    	questionGroupParams.setAppliesTo("Create Group");
    	questionGroupParams.setAnswerEditable(true);
    	questionGroupParams.addExistingQuestion("Sec 1", "Date");
    	questionGroupParams.addExistingQuestion("Sec 1", "ToBeDisabled");
    	questionGroupParams.addExistingQuestion("Sec 2", "FreeText");
    	questionGroupParams.addExistingQuestion("Sec 2", "SingleSelect");
    	questionGroupTestHelper.createQuestionGroup(questionGroupParams);

    	CreateQuestionGroupParameters questionGroupParams2 = new CreateQuestionGroupParameters();
    	questionGroupParams2.setTitle("CreateGroupQG2");
    	questionGroupParams2.setAppliesTo("Create Group");
    	questionGroupParams2.setAnswerEditable(true);
    	questionGroupParams2.addExistingQuestion("Sec 1", "DateQuestion");
    	questionGroupParams2.addExistingQuestion("Sec 1", "Number");
    	questionGroupParams2.addExistingQuestion("Sec 2", "MultiSelect");
    	questionGroupParams2.addExistingQuestion("Sec 2", "Text");
    	questionGroupTestHelper.createQuestionGroup(questionGroupParams2);
        CreateGroupSubmitParameters groupParams = new CreateGroupSubmitParameters();
        groupParams.setGroupName("GroupTest");
        String centerName = "Default Center";
        String qG_1 = "CreateGroupQG";
        String qG_2 = "CreateGroupQG2";
        QuestionResponseParameters responseParams = getQuestionResponseParametersForGroupCreation("answer1");
        QuestionResponseParameters responseParams2 = getQuestionResponseParametersForGroupCreation("answer2");
        List<CreateQuestionParameters> questionsList = new ArrayList<CreateQuestionParameters>();
        questionsList.add(newFreeTextQuestionParameters("new question 1"));
        questionsList.add(newFreeTextQuestionParameters("new question 2"));
        questionsList.add(newFreeTextQuestionParameters("new question 3"));
        String[] newActiveQuestions = {"new question 1", "new question 2"};
        String[] deactivateArray = {"new question 3", "SingleSelect", "ToBeDisabled"};
        String[] deactivatedGroupArray = {"MultiSelect", "DateQuestion"};
        List<String> deactivateList = Arrays.asList(deactivateArray);
        //When / Then
        GroupViewDetailsPage groupViewDetailsPage = groupTestHelper.createGroupWithQuestionGroupsEdited(
                groupParams, centerName, responseParams, responseParams2);
        groupViewDetailsPage.navigateToViewAdditionalInformationPage().navigateBack();
        QuestionGroupTestHelper questionTestHelper = new QuestionGroupTestHelper(selenium);
        questionTestHelper.addNewQuestionsToQuestionGroup(qG_1, questionsList);
        questionTestHelper.markQuestionsAsInactive(deactivateList);
        questionTestHelper.markQuestionGroupAsInactive(qG_2);
        QuestionResponsePage responsePage = groupTestHelper.navigateToQuestionResponsePageWhenCreatingGroup(
                groupParams, centerName);

        responsePage.verifyQuestionsDoesnotappear(deactivateArray);
        responsePage.verifyQuestionsDoesnotappear(deactivatedGroupArray);
        responsePage.verifyQuestionsExists(newActiveQuestions);
        groupViewDetailsPage = navigationHelper.navigateToGroupViewDetailsPage(groupParams.getGroupName());
        ViewQuestionResponseDetailPage responseDetailsPage = groupViewDetailsPage
                .navigateToViewAdditionalInformationPage();
        responseDetailsPage.verifyQuestionsDoesnotappear(deactivateArray);
        QuestionnairePage questionnairePage = responseDetailsPage.navigateToEditSection("0");
        questionnairePage.verifyField("details[0].sectionDetails[0].questions[0].value", "");
        questionnairePage.verifyField("details[0].sectionDetails[0].questions[1].value", "");
        questionGroupTestHelper.markQuestionGroupAsInactive(qG_1);
    }

    private QuestionResponseParameters getQuestionResponseParametersForGroupCreation(String answer) {
        QuestionResponseParameters responseParams = new QuestionResponseParameters();
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "24/01/2011");
        //responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].valuesAsArray", "first");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[1].value", "text");

        //responseParams.addTextAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "24/01/2011");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "text2");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[1].value", "red");
        //responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[2].valuesAsArray", "february:feb");

        responseParams.addTextAnswer("questionGroups[1].sectionDetails[0].questions[0].value", "24/01/2011");
        //responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[0].questions[1].valuesAsArray", "first");
        responseParams.addTextAnswer("questionGroups[1].sectionDetails[0].questions[1].value", "10");

        responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[0].valuesAsArray", "one");
        responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[0].valuesAsArray", "four");
        responseParams.addTextAnswer("questionGroups[1].sectionDetails[1].questions[1].value", "text3");
        //responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[1].value", "good");
        //responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[0].valuesAsArray", "february:feb");
        //responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[1].value", "good");
        //responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[2].valuesAsArray", "answer2:2");

        return responseParams;
    }

    private CreateQuestionParameters newFreeTextQuestionParameters(String text) {
        CreateQuestionParameters questionParams = new CreateQuestionParameters();

        questionParams.setText(text);
        questionParams.setType(CreateQuestionParameters.TYPE_FREE_TEXT);

        return questionParams;
    }

    private Map<String, String> getChoiceTags() {
        Map<String, String> tags = new HashMap<String, String>();
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
        for (String section : parameters.getExistingQuestions().keySet()) {
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
        String centerName = "Default Center";
        CreateGroupSearchPage groupSearchPage = homePage.navigateToCreateNewGroupSearchPage();
        groupSearchPage.verifyPage();
        return groupSearchPage.searchAndNavigateToCreateGroupPage(centerName);
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
