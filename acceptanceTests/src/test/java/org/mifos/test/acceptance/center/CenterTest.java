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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.center.CreateCenterEnterDataPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.testhelpers.CenterTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"center","acceptance","ui"})
public class CenterTest extends UiTestCaseBase {
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private CenterTestHelper centerTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        centerTestHelper = new CenterTestHelper(selenium);
        new InitializeApplicationRemoteTestingService().reinitializeApplication(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * Capturing responses during the Center creation
     * http://mifosforge.jira.com/browse/MIFOSTEST-665
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createCenterTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);

        CreateCenterEnterDataPage.SubmitFormParameters centerParams = getCenterParameters("Fantastico", "Joe1233171679953 Guy1233171679953");
        String officeName = "MyOffice1233171674227";
        String qG_1 = "CreateCenterQG";
        String qG_2 = "CreateCenterQG2";
        QuestionResponseParameters responseParams = getQuestionResponseParameters("answer1");
        QuestionResponseParameters responseParams2 = getQuestionResponseParameters("answer2");
        List<CreateQuestionParameters> questionsList = new ArrayList<CreateQuestionParameters>();
        questionsList.add(newFreeTextQuestionParameters("new question 1"));
        questionsList.add(newFreeTextQuestionParameters("new question 2"));
        questionsList.add(newFreeTextQuestionParameters("new question 3"));
        String[] newActiveQuestions = {"new question 1", "new question 2"};
        String[] deactivateArray = {"new question 3", "SingleSelect", "SmartSelect", "Text", "TextQuestion"};
        String[] deactivatedGroupArray = {"question 7", "question 6"};
        List<String> deactivateList = Arrays.asList(deactivateArray);

        CenterViewDetailsPage centerViewDetailsPage = centerTestHelper.createCenterWithQuestionGroupsEdited(centerParams, officeName, responseParams, responseParams2);
        centerViewDetailsPage.navigateToViewAdditionalInformation().navigateBack();
        QuestionGroupTestHelper questionTestHelper = new QuestionGroupTestHelper(selenium);
        questionTestHelper.addNewQuestionsToQuestionGroup(qG_1, questionsList);
        questionTestHelper.markQuestionsAsInactive(deactivateList);
        questionTestHelper.markQuestionGroupAsInactive(qG_2);
        QuestionResponsePage responsePage = centerTestHelper.navigateToQuestionResponsePageWhenCreatingCenter(centerParams, officeName);


        responsePage.verifyQuestionsDoesnotappear(deactivateArray);
        responsePage.verifyQuestionsDoesnotappear(deactivatedGroupArray);
        responsePage.verifyQuestionsExists(newActiveQuestions);
        centerViewDetailsPage = centerTestHelper.navigateToCenterViewDetailsPage("Fantastico");
        centerViewDetailsPage.verifyActiveCenter(centerParams);
        ViewQuestionResponseDetailPage responseDetailsPage = centerViewDetailsPage.navigateToViewAdditionalInformation();
        responseDetailsPage.verifyQuestionsDoesnotappear(deactivateArray);
        responseDetailsPage.verifyEditButtonDisabled("1");
        QuestionnairePage questionnairePage = responseDetailsPage.navigateToEditSection("0");
        questionnairePage.verifyField("details[0].sectionDetails[0].questions[0].value", "");
        questionnairePage.verifyField("details[0].sectionDetails[0].questions[1].value", "");
    }

    private QuestionResponseParameters getQuestionResponseParameters(String answer) {
        QuestionResponseParameters responseParams = new QuestionResponseParameters();
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[0].value", "good");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].valuesAsArray", "february:feb");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[2].value", answer);
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[0].questions[3].value", answer);

        responseParams.addTextAnswer("questionGroups[0].sectionDetails[1].questions[0].value", "24/01/2011");
        responseParams.addSingleSelectAnswer("questionGroups[0].sectionDetails[1].questions[1].valuesAsArray", "green");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[1].questions[2].value", "10");
        responseParams.addTextAnswer("questionGroups[0].sectionDetails[1].questions[3].value", "10");

        responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[0].questions[0].valuesAsArray", "red");
        responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[0].questions[1].valuesAsArray", "Everything");
        responseParams.addTextAnswer("questionGroups[1].sectionDetails[0].questions[2].value", answer);
        responseParams.addTextAnswer("questionGroups[1].sectionDetails[0].questions[3].value", answer);

        responseParams.addTextAnswer("questionGroups[1].sectionDetails[1].questions[0].value", "24/01/2011");
        responseParams.addTextAnswer("questionGroups[1].sectionDetails[1].questions[1].value", "10");
        responseParams.addTextAnswer("questionGroups[1].sectionDetails[1].questions[2].value", "10");
        responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[3].value", "yes");
        responseParams.addSingleSelectAnswer("questionGroups[1].sectionDetails[1].questions[4].valuesAsArray", "answer2:2");

        return responseParams;
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
        meetingFormParameters.setWeekDay(MeetingParameters.WEDNESDAY);
        meetingFormParameters.setMeetingPlace("Bangalore");

        formParameters.setMeeting(meetingFormParameters);
        return formParameters;
    }

}
