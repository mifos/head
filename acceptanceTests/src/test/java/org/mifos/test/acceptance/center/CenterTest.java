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

package org.mifos.test.acceptance.center;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineAcceptedPaymentTypesPage;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.center.CreateCenterEnterDataPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentPage;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.CenterTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = { "center", "acceptance", "ui", "no_db_unit"})
public class CenterTest extends UiTestCaseBase {

    private CenterTestHelper centerTestHelper;

    private QuestionGroupTestHelper questionTestHelper;
    
    private ClientTestHelper clientTestHelper;
    
    private GroupTestHelper groupTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        centerTestHelper = new CenterTestHelper(selenium);
        questionTestHelper = new QuestionGroupTestHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
        groupTestHelper = new GroupTestHelper(selenium);
        new InitializeApplicationRemoteTestingService().reinitializeApplication(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-246
    @Test(enabled=true)
    public void verifyAcceptedPaymentTypesForCenter() throws Exception {
        // When
        CreateCenterEnterDataPage.SubmitFormParameters formParameters = new CreateCenterEnterDataPage.SubmitFormParameters();
        String testCenterName = "Center" + StringUtil.getRandomString(6);
        formParameters.setCenterName(testCenterName);
        formParameters.setLoanOfficer("loan officer");
        MeetingParameters meeting = new MeetingParameters();
        meeting.setMeetingPlace("centerTestMeetingPlace" + StringUtil.getRandomString(2));
        meeting.setWeekFrequency("1");
        meeting.setWeekDay(MeetingParameters.WeekDay.MONDAY);
        formParameters.setMeeting(meeting);
        centerTestHelper.createCenter(formParameters, "MyOfficeDHMFT");

        NavigationHelper navigationHelper = new NavigationHelper(selenium);
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineAcceptedPaymentTypesPage defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addLoanFeesPaymentType(DefineAcceptedPaymentTypesPage.CHEQUE);

        adminPage = navigationHelper.navigateToAdminPage();
        defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addLoanFeesPaymentType(DefineAcceptedPaymentTypesPage.VOUCHER);

        ApplyPaymentPage applyPaymentPage = navigationHelper.navigateToCenterViewDetailsPage(testCenterName)
                .navigateToViewCenterChargesDetailPage().navigateToApplyPayments();
        // Then
        applyPaymentPage.verifyModeOfPayments();

    }

    /**
     * Capturing responses during the Center creation http://mifosforge.jira.com/browse/MIFOSTEST-665
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void createCenterTest() throws Exception {
        String officeName = "MyOfficeDHMFT";
        String qG1Name = "CreateCenterQG";
        String qG2Name = "CreateCenterQG2";

        createQuestions();

        CreateQuestionGroupParameters qG1 = questionTestHelper.getCreateQuestionGroupParameters(qG1Name,
                asList("center question 1", "center question 2", "center question 3"), "Create Center", "Sec 1");
        questionTestHelper.createQuestionGroup(qG1);

        CreateQuestionGroupParameters qG2 = questionTestHelper.getCreateQuestionGroupParameters(qG2Name,
                asList("center question 4", "center question 5", "center question 6"), "Create Center", "Sec 2");
        questionTestHelper.createQuestionGroup(qG2);

        String testCenterName = "Center" + StringUtil.getRandomString(6);
        CreateCenterEnterDataPage.SubmitFormParameters centerParams = getCenterParameters(testCenterName,
                "loan officer");

        QuestionResponseParameters responseParams = getQuestionResponseParameters("answer1");
        QuestionResponseParameters responseParams2 = getQuestionResponseParameters("answer2");

        List<CreateQuestionParameters> questionsList = new ArrayList<CreateQuestionParameters>();
        questionsList.add(newFreeTextQuestionParameters("new center question 1"));
        questionsList.add(newFreeTextQuestionParameters("new center question 2"));
        String[] newActiveQuestions = { "new center question 1", "center question 2" };
        String[] deactivateArray = { "center question 3", "center question 4", };
        List<String> deactivateList = Arrays.asList(deactivateArray);

        CenterViewDetailsPage centerViewDetailsPage = centerTestHelper.createCenterWithQuestionGroupsEdited(
                centerParams, officeName, responseParams, responseParams2);
        centerViewDetailsPage.navigateToViewAdditionalInformation().navigateBack();
        questionTestHelper.addNewQuestionsToQuestionGroup(qG1Name, questionsList);
        questionTestHelper.markQuestionsAsInactive(deactivateList);
        questionTestHelper.markQuestionGroupAsInactive(qG2Name);
        QuestionResponsePage responsePage = centerTestHelper.navigateToQuestionResponsePageWhenCreatingCenter(
                centerParams, officeName);

        responsePage.verifyQuestionsDoesnotappear(deactivateArray);
        responsePage.verifyQuestionsExists(newActiveQuestions);
        centerViewDetailsPage = centerTestHelper.navigateToCenterViewDetailsPage(testCenterName);
        centerViewDetailsPage.verifyActiveCenter(centerParams);
        ViewQuestionResponseDetailPage responseDetailsPage = centerViewDetailsPage
                .navigateToViewAdditionalInformation();
        responseDetailsPage.verifyQuestionsDoesnotappear(deactivateArray);
        responseDetailsPage.verifyEditButtonDisabled("1");
        QuestionnairePage questionnairePage = responseDetailsPage.navigateToEditSection("0");
        questionnairePage.verifyField("details[0].sectionDetails[0].questions[0].value", "");
        questionnairePage.verifyField("details[0].sectionDetails[0].questions[1].value", "");
        questionTestHelper.markQuestionGroupAsInactive(qG1Name);
    }
    
    // http://mifosforge.jira.com/browse/MIFOSTEST-1176
    @Test(enabled=true)
    public void editingCenterMeetinSchedule(){
    	//Given
    	String centerName="DefineNewSavingsProductTestCenter";
    	String groupName="DefineNewSavingsProductTestGroup";
    	String clientName="DefineNewSavingsProduct TestClient";
        MeetingParameters parameters = new MeetingParameters();
        parameters.setWeekDay(MeetingParameters.WeekDay.THURSDAY);
    	//When
    	String meetingSchedule = centerTestHelper.editCenterMeetingSchedule(centerName, parameters).getMeetingSchedule();
    	//Then
    	clientTestHelper.verifyMeetingSchedule(clientName, meetingSchedule);
    	groupTestHelper.verifyMeetingSchedule(groupName, meetingSchedule);
    	parameters.setWeekDay(MeetingParameters.WeekDay.MONDAY);
    	centerTestHelper.editCenterMeetingSchedule(centerName, parameters);
    }

    private QuestionResponseParameters getQuestionResponseParameters(String answer) {
        QuestionResponseParameters responseParams = new QuestionResponseParameters();
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "yes");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].value", "good");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[2].value", answer);
        return responseParams;
    }

    private void createQuestions() {
        List<CreateQuestionParameters> questions = new ArrayList<CreateQuestionParameters>();
        CreateQuestionParameters q1 = new CreateQuestionParameters();
        q1.setType(CreateQuestionParameters.TYPE_SINGLE_SELECT);
        q1.setText("center question 1");
        q1.setChoicesFromStrings(Arrays.asList(new String[] { "yes", "no" }));
        questions.add(q1);
        CreateQuestionParameters q2 = new CreateQuestionParameters();
        q2.setType(CreateQuestionParameters.TYPE_SINGLE_SELECT);
        q2.setText("center question 2");
        q2.setChoicesFromStrings(Arrays.asList(new String[] { "good", "bad", "average" }));
        questions.add(q2);
        CreateQuestionParameters q3 = new CreateQuestionParameters();
        q3.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        q3.setText("center question 3");
        questions.add(q3);
        CreateQuestionParameters q4 = new CreateQuestionParameters();
        q4.setType(CreateQuestionParameters.TYPE_DATE);
        q4.setText("center question 4");
        questions.add(q4);
        CreateQuestionParameters q5 = new CreateQuestionParameters();
        q5.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        q5.setText("center question 5");
        questions.add(q5);
        CreateQuestionParameters q6 = new CreateQuestionParameters();
        q6.setType(CreateQuestionParameters.TYPE_NUMBER);
        q6.setText("center question 6");
        q6.setNumericMax(10);
        q6.setNumericMin(0);
        questions.add(q6);
        questionTestHelper.createQuestions(questions);
    }

    private CreateQuestionParameters newFreeTextQuestionParameters(String text) {
        CreateQuestionParameters questionParams = new CreateQuestionParameters();

        questionParams.setText(text);
        questionParams.setType(CreateQuestionParameters.TYPE_FREE_TEXT);

        return questionParams;
    }

    private CreateCenterEnterDataPage.SubmitFormParameters getCenterParameters(String centerName, String loanOfficer) {
        CreateCenterEnterDataPage.SubmitFormParameters formParameters = new CreateCenterEnterDataPage.SubmitFormParameters();
        formParameters.setCenterName(centerName);
        formParameters.setLoanOfficer(loanOfficer);

        MeetingParameters meetingFormParameters = new MeetingParameters();
        meetingFormParameters.setWeekFrequency("1");
        meetingFormParameters.setWeekDay(MeetingParameters.WeekDay.WEDNESDAY);
        meetingFormParameters.setMeetingPlace("Bangalore");

        formParameters.setMeeting(meetingFormParameters);
        return formParameters;
    }
}