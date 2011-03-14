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
package org.mifos.test.acceptance.questionnaire;

import java.util.Collections;
import java.util.Set;
import org.mifos.test.acceptance.framework.client.QuestionGroup;
import org.mifos.test.acceptance.framework.customer.CustomerChangeStatusPage;
import org.mifos.test.acceptance.framework.questionnaire.EditQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionDetailPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionsPage;
import org.mifos.test.acceptance.framework.questionnaire.Choice;
import org.mifos.test.acceptance.framework.client.ClientViewChangeLogPage;
import java.util.Arrays;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.questionnaire.AttachQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.framework.questionnaire.EditQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionGroupDetailPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionGroupResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionnairePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewAllQuestionGroupsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mifos.test.acceptance.framework.client.ClientCloseReason;
import org.mifos.test.acceptance.framework.client.ClientStatus;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.EditCustomerStatusParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.QuestionResponseParameters;
import org.mifos.test.acceptance.framework.office.OfficeParameters;
import org.mifos.test.acceptance.framework.office.OfficeViewDetailsPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.OfficeHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@SuppressWarnings("PMD.CyclomaticComplexity")
@Test(sequential = true, groups = {"client", "acceptance", "ui"})
public class QuestionGroupTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private OfficeHelper officeHelper;
    private QuestionGroupTestHelper questionGroupTestHelper;
    private ClientTestHelper clientTestHelper;
    private LoanTestHelper loanTestHelper;
    private static final String START_DATA_SET = "acceptance_small_003_dbunit.xml";
    private String qgTitle1, qgTitle2, qgTitle3;
    private String qTitle1, qTitle2, qTitle3, qTitle4, qTitle5;
    private static final String TITLE_MISSING = "Please specify Question Group title.";
    private static final String APPLIES_TO_MISSING = "Please choose a valid 'Applies To' value.";
    private static final String SECTION_MISSING = "Please add at least one section.";
    private static final String QUESTION_MISSING = "Section should have at least one question.";
    public static final String APPLIES_TO_CREATE_CLIENT = "Create Client";
    public static final String SECTION_DEFAULT = "Default";
    private static final String SECTION_MISC = "Misc";
    private static final String VIEW_CLIENT_QUESTION_GROUP = "ViewClientQG";
    private static final String CREATE_CLIENT_QUESTION_GROUP = "CreateClientQG2";
    private static final int CREATE_OFFICE_QUESTION_GROUP_ID = 27;
    private static final String CLIENT = "Stu1232993852651 Client1232993852651";
    private Map<Integer, QuestionGroup> questionGroupInstancesOfClient;
    private static final List<String> charactersList = new ArrayList<String>(Arrays.asList("عربية","有","òèßñ"));
    private static final String noNumber = "qwerty";
    private static final List<String> EMPTY_LIST = new ArrayList<String>();
    private static final List<String> QUESTIONS_LIST = new ArrayList<String>(Arrays.asList("CreateClientQG",
        "CreateClientQG2", "ViewClientQG", "ViewClientQG2", "Survey 1", "CloseClientQG", "CloseClientQG2",
        "CreateGroupQG", "CreateGroupQG2", "ViewGroupQG", "ViewGroupQG2", "CreateCenterQG",
        "CreateCenterQG2", "ViewCenterQG", "ViewCenterQG2", "CreateSavingsQG",
        "CreateSavingsQG2","ViewSavingsQG", "ViewSavingsQG2", "CreateLoanQG",
        "CreateLoanQG2", "ViewLoanQG", "ViewLoanQG2", "DisburseLoanQG", "DisburseLoanQG2"));
    private static final Map<String,String> QUESTIONS = new HashMap<String, String>();


    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();
        officeHelper = new OfficeHelper(selenium);
        appLauncher = new AppLauncher(selenium);
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        questionGroupInstancesOfClient = new HashMap<Integer, QuestionGroup>();
        qgTitle1 = "QuestionGroup1 " + System.currentTimeMillis();
        qgTitle2 = "QuestionGroup2 " + System.currentTimeMillis();
        qgTitle3 = "QuestionGroup3 " + System.currentTimeMillis();
        qTitle1 = "Question1 " + System.currentTimeMillis();
        qTitle2 = "Question2 " + System.currentTimeMillis();
        qTitle3 = "Question3 " + System.currentTimeMillis();
        qTitle4 = "Question4 " + System.currentTimeMillis();
        qTitle5 = "Question5 " + System.currentTimeMillis();
        QUESTIONS.put("TextQuestionTest", CreateQuestionParameters.TYPE_FREE_TEXT);
        QUESTIONS.put("DateQuestionTest", CreateQuestionParameters.TYPE_DATE);
        QUESTIONS.put("NumberQuestionTest", CreateQuestionParameters.TYPE_NUMBER);
        QUESTIONS.put("MultiSelectQuestionTest", CreateQuestionParameters.TYPE_MULTI_SELECT);
        QUESTIONS.put("SingleSelectQuestionTest", CreateQuestionParameters.TYPE_SINGLE_SELECT);
        QUESTIONS.put("SmartSelectQuestionTest", CreateQuestionParameters.TYPE_SMART_SELECT);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createQuestionGroup() throws Exception{
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);
        AdminPage adminPage = createQuestions(qTitle1, qTitle2, qTitle3);
        CreateQuestionGroupPage createQuestionGroupPage = getCreateQuestionGroupPage(adminPage);
        testMissingMandatoryInputs(createQuestionGroupPage);
        testCreateQuestionGroup(createQuestionGroupPage, qgTitle1, APPLIES_TO_CREATE_CLIENT, true, SECTION_DEFAULT, asList(qTitle1, qTitle2), asList(qTitle3), qTitle4);
        testShouldAllowDuplicateTitlesForQuestionGroup();
        testCancelCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)));
        testViewQuestionGroups();
    }

    /**
     * Verify that user is able to edit the defined Question Groups and
     * change the order of questions and sections
     * http://mifosforge.jira.com/browse/MIFOSTEST-666
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void editQuestionGroups() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);

        //When
        ViewAllQuestionGroupsPage viewAllQuestionGroupsPage = questionGroupTestHelper.navigateToViewQuestionGroups(QUESTIONS_LIST);
        EditQuestionGroupPage editQuestionGroupPage = viewAllQuestionGroupsPage.navigateToQuestionGroupDetailPage(VIEW_CLIENT_QUESTION_GROUP).navigateToEditPage();
        editQuestionGroupPage.moveSectionUp("Sec 2");
        editQuestionGroupPage.moveQuestionUp("Sec 1", 15);
        editQuestionGroupPage.moveQuestionDown("Sec 1", 16);
        QuestionGroupDetailPage questionGroupDetailPage = editQuestionGroupPage.editQuestionGroup(false, "ViewClientQuestionGroupName", "Create Client", Arrays.asList("2","5","6"));

        //Then
        questionGroupDetailPage.verifyOrderQuestions(asList("MultiSelect","Date","Number","Text","SmartSelect"), 6);
        questionGroupDetailPage.verifyOrderSections(asList("Sec 2","Sec 1"));
        questionGroupDetailPage.verifyMandatoryQuestions(EMPTY_LIST, "Sec 1");
        questionGroupDetailPage.verifyMandatoryQuestions(EMPTY_LIST, "Sec 2");
        questionGroupDetailPage.verifyTitle("ViewClientQuestionGroupName");

        //When
        viewAllQuestionGroupsPage = questionGroupDetailPage.navigateToViewQuestionGroupsPage();

        //Then
        viewAllQuestionGroupsPage.verifyInactiveQuestions(0,0);

    }

    /**
     * Verifying that Change Log for Question Groups has an appropriate format
     * http://mifosforge.jira.com/browse/MIFOSTEST-667
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void testChangeLog() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);
        ClientViewDetailsPage clientViewDetailsPage = clientTestHelper.navigateToClientViewDetailsPage(CLIENT);

        //When
        Map<String,Integer> questions = new HashMap<String, Integer>();
        questions.put("question 4:yes",0);
        clientViewDetailsPage = clientTestHelper.editQuestionGroupResponses(
                clientViewDetailsPage, "0",
                questions);

        questions = new HashMap<String, Integer>();
        questions.put("Number:123",1);
        questions.put("NumberQuestion2:9",1);
        clientViewDetailsPage = clientTestHelper.editQuestionGroupResponses(
                clientViewDetailsPage, "0",
                questions);

        //Then
        ClientViewChangeLogPage clientViewChangeLogPage = clientViewDetailsPage.navigateToClientViewChangeLog();
        clientViewChangeLogPage.verifyChangeLog(asList("CreateClientQG/Sec 2/Number","CreateClientQG/Sec 2/NumberQuestion2"),
                asList("-","-"), asList("123","9"), asList("mifos","mifos"),3);
    }

    /**
     * Capturing responses during the Office creation
     * http://mifosforge.jira.com/browse/MIFOSTEST-671
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createOfficeWithQuestionGroupTest() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);
        EditQuestionGroupPage editQuestionGroupPage = questionGroupTestHelper.naviagateToEditQuestionGroup(CREATE_OFFICE_QUESTION_GROUP_ID);
        editQuestionGroupPage.editQuestionGroup("Create Office");

        //When
        OfficeParameters officeParameters = new OfficeParameters();
        officeParameters.setOfficeName("TestOffice");
        officeParameters.setOfficeType(OfficeParameters.REGIONAL_OFFICE);
        officeParameters.setParentOffice("Head Office(Mifos HO )");
        officeParameters.setShortName("TO");
        QuestionResponsePage questionResponsePage = officeHelper.navigateToQuestionResponsePage(officeParameters);

        QuestionResponseParameters responseParameters = new QuestionResponseParameters();
        responseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[0].valuesAsArray", "Nothing");
        responseParameters.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].valuesAsArray", "Yes");
        responseParameters.addTextAnswer("questionGroups[0].sectionDetails[0].questions[2].value", "123");

        QuestionResponseParameters responseParameters2 = new QuestionResponseParameters();
        responseParameters2.addSingleSelectAnswer("questionGroups[0].sectionDetails[0].questions[1].valuesAsArray", "Maybe");
        responseParameters2.addTextAnswer("questionGroups[0].sectionDetails[0].questions[2].value", "1234");

        OfficeViewDetailsPage officeViewDetailsPage = questionGroupTestHelper.createOfficeWithQuestionGroup(questionResponsePage, responseParameters, responseParameters2);
        ViewQuestionResponseDetailPage viewQuestionResponseDetailPage = officeViewDetailsPage.navigateToViewAdditionalInformation();
        viewQuestionResponseDetailPage.verifyQuestionPresent("question 7", "Nothing");
        viewQuestionResponseDetailPage.verifyQuestionPresent("question 5", "Yes", "Maybe");
        viewQuestionResponseDetailPage.verifyQuestionPresent("question 2", "1234");
        officeViewDetailsPage = viewQuestionResponseDetailPage.navigateToDetailsPage();

        List<String> questionToAdd= new ArrayList<String>();
        questionToAdd.add("question 4");
        questionToAdd.add("question 3");

        List<String> questionToDesactivate = new ArrayList<String>();
        questionToDesactivate.add("question 5");
        questionToDesactivate.add("question 7");
        questionToDesactivate.add("MultiSelect");
        questionToDesactivate.add("NumberQuestion");
        questionToDesactivate.add("question 1");

        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        for (String question : questionToAdd) {
            createQuestionGroupParameters.addExistingQuestion("Sec 1", question);
        }
        questionGroupTestHelper.addQuestionsToQuestionGroup(CREATE_OFFICE_QUESTION_GROUP_ID, createQuestionGroupParameters.getExistingQuestions());

        for (String question : questionToDesactivate) {
            questionGroupTestHelper.markQuestionAsInactive(question);
        }

        editQuestionGroupPage = questionGroupTestHelper.naviagateToEditQuestionGroup(VIEW_CLIENT_QUESTION_GROUP);
        editQuestionGroupPage.editQuestionGroup("Create Office");

        editQuestionGroupPage = questionGroupTestHelper.naviagateToEditQuestionGroup(CREATE_CLIENT_QUESTION_GROUP);
        editQuestionGroupPage.editQuestionGroup("Create Office");

        questionGroupTestHelper.markQuestionGroupAsInactive(VIEW_CLIENT_QUESTION_GROUP);

        officeParameters = new OfficeParameters();
        officeParameters.setOfficeName("TestOffice2");
        officeParameters.setOfficeType(OfficeParameters.REGIONAL_OFFICE);
        officeParameters.setParentOffice("Head Office(Mifos HO )");
        officeParameters.setShortName("TO2");
        questionResponsePage = officeHelper.navigateToQuestionResponsePage(officeParameters);

        //Then
        questionResponsePage.verifyQuestionsDoesnotappear(questionToDesactivate.toArray(new String[questionToDesactivate.size()]));
        questionResponsePage.verifyQuestionsExists(questionToAdd.toArray(new String[questionToAdd.size()]));
        questionResponsePage.verifySectionDoesnotappear("Sec 2");
        officeHelper.verifyQuestionPresent("TestOffice", "question 3", "");
        officeHelper.verifyQuestionPresent("TestOffice", "question 4", "");
        officeHelper.verifyQuestionPresent("TestOffice", "Date", "");
        officeHelper.verifyQuestionPresent("TestOffice", "Number", "");
        officeHelper.verifyQuestionPresent("TestOffice", "SingleSelect", "");
    }

    /**
     * Creating and editing Questions
     * http://mifosforge.jira.com/browse/MIFOSTEST-700
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createAndEditQuestionsTest() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);

        //When
        testValidationAddQuestion();
        CreateQuestionPage createQuestionPage = questionGroupTestHelper.navigateToCreateQuestionPage();
        CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
        createQuestionParameters.setText("TextQuestionTest");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        createQuestionPage.addQuestion(createQuestionParameters);

        createQuestionParameters.setText("DateQuestionTest");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_DATE);
        createQuestionPage.addQuestion(createQuestionParameters);

        createQuestionParameters.setText("NumberQuestionTest");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_NUMBER);
        createQuestionParameters.setNumericMin(1);
        createQuestionParameters.setNumericMax(10);
        createQuestionPage.addQuestion(createQuestionParameters);

        createQuestionParameters.setText("MultiSelectQuestionTest");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_MULTI_SELECT);
        List<Choice> choices = new ArrayList<Choice>();
        Choice c = new Choice("choice 1", EMPTY_LIST);
        choices.add(c);
        c = new Choice("choice 2", EMPTY_LIST);
        choices.add(c);
        c = new Choice("choice 3", EMPTY_LIST);
        choices.add(c);
        createQuestionParameters.setChoices(choices);
        createQuestionPage.addQuestion(createQuestionParameters);
        createQuestionParameters.setText("SingleSelectQuestionTest");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_SINGLE_SELECT);
        createQuestionParameters.setChoices(choices);
        createQuestionPage.addQuestion(createQuestionParameters);
        createQuestionParameters.setText("SmartSelectQuestionTest");
        createQuestionParameters.setChoices(choices);
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_SMART_SELECT);
        createQuestionPage.addQuestion(createQuestionParameters);
        AdminPage adminPage = createQuestionPage.submitQuestions();
        ViewAllQuestionsPage viewAllQuestionsPage = adminPage.navigateToViewAllQuestions();

        //Then
        viewAllQuestionsPage.verifyQuestions(QUESTIONS.keySet());

        //When
        c = new Choice("answerChoice1", EMPTY_LIST);
        choices.add(c);
        c = new Choice("answerChoice3", EMPTY_LIST);
        choices.add(c);
        for(String question : QUESTIONS.keySet()) {
            QuestionDetailPage questionDetailPage = viewAllQuestionsPage.navigateToQuestionDetail(question);
            EditQuestionPage editQuestionPage = questionDetailPage.navigateToEditQuestionPage();
            createQuestionParameters.setText("");
            editQuestionPage.tryUpdate(createQuestionParameters);
            //Then
            editQuestionPage.verifyTextPresent("Please specify the question", "No text <Please specify the question> present on the page");
            questionDetailPage = editQuestionPage.cancelEdit();
            //When
            editQuestionPage = questionDetailPage.navigateToEditQuestionPage();
            for(String characters: charactersList) {
                editQuestionPage.setQuestionName(characters);
                editQuestionPage.verifyQuestionName(characters);
            }
            if("NumberQuestionTest".equals(question)) {
                editQuestionPage.setNumberQuestion(noNumber, noNumber);
                editQuestionPage.verifyNumberQuestion("", "");
                editQuestionPage.setNumberQuestion("","");
            }
            else if("MultiSelectQuestionTest".equals(question) || "SingleSelectQuestionTest".equals(question)) {
                editQuestionPage.addAnswerChoices(asList("answerChoice1","answerChoice2","answerChoice3"));
                editQuestionPage.removeAnswerChoice("4");
            }
            else if("SmartSelectQuestionTest".equals(question)) {
                editQuestionPage.addSmartAnswerChoices(asList("answerChoice1","answerChoice2","answerChoice3"));
                editQuestionPage.removeAnswerChoice("4");
            }
            editQuestionPage.setQuestionName(question + "Edit");
            questionDetailPage = editQuestionPage.deactivate();
            //Then
            questionDetailPage.verifyQuestionTitle(question + "Edit");
            if("MultiSelectQuestionTest".equals(question) || "SingleSelectQuestionTest".equals(question) || "SmartSelectQuestionTest".equals(question)) {
                questionDetailPage.assertForChoices(QUESTIONS.get(question),choices);
            }
            viewAllQuestionsPage = questionDetailPage.navigateToViewAllQuestionsPage();
        }
    }

    /**
     * Attaching a Question Group to Multiple flows
     * http://mifosforge.jira.com/browse/MIFOSTEST-701
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void attachingQuestionGroupToMultipleFlowsTest() throws Exception{
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);
        questionGroupTestHelper.markQuestionGroupAsInactive("CloseClientQG");
        questionGroupTestHelper.markQuestionGroupAsInactive("CloseClientQG2");
        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();
        params.setCancelReason(EditLoanAccountStatusParameters.CANCEL_REASON_OTHER);
        params.setNote("TEST");
        params.setStatus(EditLoanAccountStatusParameters.CANCEL);
        loanTestHelper.changeLoanAccountStatus("000100000000004", params);
        loanTestHelper.changeLoanAccountStatus("000100000000005", params);
        //When
        testValidationAddQuestionGroup();
        Map<String, List<String>> sectionQuestions = new HashMap<String, List<String>>();
        List<String> questions = new ArrayList<String>();
        questions.add("Text");
        questions.add("question 2");
        questions.add("question 3");
        sectionQuestions.put("Sec Test", questions);
        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.setAnswerEditable(true);
        createQuestionGroupParameters.setTitle("TestQuestionGroup");
        createQuestionGroupParameters.setAppliesTo("View Client");
        createQuestionGroupParameters.setAppliesTo("Close Client");
        createQuestionGroupParameters.setExistingQuestions(sectionQuestions);
        questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);
        Map<String, String> answers = new HashMap<String, String>();
        answers.put("Text", "Test - Text");
        answers.put("question 2", "2");
        answers.put("question 3", "11/11/2009");
        ClientViewDetailsPage clientViewDetailsPage = questionGroupTestHelper.attachQuestionGroup(CLIENT, "TestQuestionGroup", asList("Sec Test"), answers);
        CustomerChangeStatusPage customerChangeStatusPage = clientViewDetailsPage.navigateToCustomerChangeStatusPage();
        EditCustomerStatusParameters customerStatusParameters = new EditCustomerStatusParameters();
        customerStatusParameters.setNote("TEST");
        customerStatusParameters.setClientStatus(ClientStatus.CLOSED);
        customerStatusParameters.setClientCloseReason(ClientCloseReason.TRANSFERRED);
        QuestionResponsePage questionResponsePage = customerChangeStatusPage.changeStatusAndNavigateToQuestionResponsePage(customerStatusParameters);
        //Then
        questionResponsePage.verifyQuestionsExists(questions.toArray(new String[questions.size()]));
        //When
        clientViewDetailsPage = questionResponsePage.cancel();
        ViewQuestionResponseDetailPage viewQuestionResponseDetailPage = clientViewDetailsPage.navigateToViewAdditionalInformationPage();
        //Then
        viewQuestionResponseDetailPage.verifyQuestionsDoesnotappear(questions.toArray(new String[questions.size()]));
        clientViewDetailsPage = viewQuestionResponseDetailPage.navigateToClientViewDetailsPage();
        answers = new HashMap<String, String>();
        answers.put("Text", "Test - Text - Edit");
        answers.put("question 2", "22");
        questionGroupInstancesOfClient = clientViewDetailsPage.getQuestionGroupInstances();
        editResponses(clientViewDetailsPage, latestInstanceId(questionGroupInstancesOfClient), answers);
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-660
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAttachingQuestionGroupToGroup() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);
        Map<String, List<String>> sectionQuestions = new HashMap<String, List<String>>();
        List<String> questions = new ArrayList<String>();
        questions.add("Date");
        questions.add("question 3");
        questions.add("question 4");
        sectionQuestions.put("Sec 1", questions);
        questions = new ArrayList<String>();
        questions.add("DateQuestion");
        questions.add("Number");
        questions.add("question 1");
        questions.add("Text");
        sectionQuestions.put("Sec 2", questions);
        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.setAnswerEditable(true);
        createQuestionGroupParameters.setAppliesTo("View Group");
        createQuestionGroupParameters.setTitle("TestQuestionGroup"+StringUtil.getRandomString(6));
        createQuestionGroupParameters.setExistingQuestions(sectionQuestions);
        AttachQuestionGroupParameters attachParams = new AttachQuestionGroupParameters();
        attachParams.setTarget("MyGroup1232993846342");
        attachParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
        attachParams.addTextResponse("Date", "09/02/2011");
        attachParams.addCheckResponse("question 4", "yes");
        attachParams.addTextResponse("DateQuestion", "19/02/2011");
        attachParams.addTextResponse("question 3", "25/02/2011");
        attachParams.addTextResponse("Number", "60");
        attachParams.addTextResponse("question 1", "tekst tekst");
        attachParams.addTextResponse("Text", "ale alo olu");
        AttachQuestionGroupParameters attachErrorParams = new AttachQuestionGroupParameters();
        attachErrorParams.setTarget("MyGroup1232993846342");
        attachErrorParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
        attachErrorParams.addTextResponse("Number", "sdfsdf");
        attachErrorParams.addTextResponse("question 3", "25/02/2011");
        attachErrorParams.addCheckResponse("question 4", "yes");
        attachErrorParams.addError("Please specify a number for Number.");

        //When
        questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);

        questionGroupTestHelper.verifyErrorsWhileAttachingQuestionGroupToGroup(attachErrorParams);
        questionGroupTestHelper.attachQuestionGroupToGroup(attachParams);

        attachParams.addTextResponse("Number", "20");
        attachParams.addTextResponse("question 3", "21/02/2011");
        //Then
        questionGroupTestHelper.editQuestionGroupResponsesInGroup(attachParams);
    }

  //http://mifosforge.jira.com/browse/MIFOSTEST-662
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAttachingQuestionGroupToLoan() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);
        Map<String, List<String>> sectionQuestions = new HashMap<String, List<String>>();
        List<String> questions = new ArrayList<String>();
        questions.add("question 4");
        questions.add("question 3");
        questions.add("Date");
        sectionQuestions.put("Sec 1", questions);
        questions = new ArrayList<String>();
        questions.add("question 1");
        questions.add("Number");
        questions.add("DateQuestion");
        questions.add("Text");
        sectionQuestions.put("Sec 2", questions);
        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.setAppliesTo("View Loan");
        createQuestionGroupParameters.setAnswerEditable(true);
        createQuestionGroupParameters.setTitle("TestQuestionGroup"+StringUtil.getRandomString(6));
        createQuestionGroupParameters.setExistingQuestions(sectionQuestions);
        AttachQuestionGroupParameters attachParams = new AttachQuestionGroupParameters();
        attachParams.setTarget("000100000000004");
        attachParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
        attachParams.addCheckResponse("question 4", "yes");
        attachParams.addTextResponse("Date", "09/02/2011");
        attachParams.addTextResponse("DateQuestion", "19/02/2011");
        attachParams.addTextResponse("question 3", "25/02/2011");
        attachParams.addTextResponse("question 1", "tekst tekst");
        attachParams.addTextResponse("Text", "ale alo olu");
        attachParams.addTextResponse("Number", "60");
        AttachQuestionGroupParameters attachErrorParams = new AttachQuestionGroupParameters();
        attachErrorParams.setTarget("000100000000004");
        attachErrorParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
        attachErrorParams.addError("Please specify a number for Number.");
        attachErrorParams.addTextResponse("Number", "sdfsdf");
        attachErrorParams.addTextResponse("question 3", "25/02/2011");
        attachErrorParams.addCheckResponse("question 4", "yes");

        //When
        questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);

        questionGroupTestHelper.verifyErrorsWhileAttachingQuestionGroupToLoan(attachErrorParams);
        questionGroupTestHelper.attachQuestionGroupToLoan(attachParams);

        attachParams.addTextResponse("Number", "20");
        attachParams.addTextResponse("question 3", "21/02/2011");
        //Then
        questionGroupTestHelper.editQuestionGroupResponsesInLoan(attachParams);
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-680
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAttachingQuestionGroupToViewClient() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_016_dbunit.xml", dataSource, selenium);
        Map<String, List<String>> sectionQuestions = new HashMap<String, List<String>>();
        List<String> questions = new ArrayList<String>();
        questions.add("Date");
        questions.add("question 4");
        questions.add("question 3");
        sectionQuestions.put("Sec 1", questions);
        questions = new ArrayList<String>();
        questions.add("Number");
        questions.add("DateQuestion");
        questions.add("question 1");
        questions.add("Text");
        sectionQuestions.put("Sec 2", questions);
        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.setExistingQuestions(sectionQuestions);
        createQuestionGroupParameters.setAnswerEditable(true);
        createQuestionGroupParameters.setAppliesTo("View Client");
        createQuestionGroupParameters.setTitle("TestQuestionGroup"+StringUtil.getRandomString(6));
        AttachQuestionGroupParameters attachParams = new AttachQuestionGroupParameters();
        attachParams.setTarget("0002-000000003");
        attachParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
        attachParams.addTextResponse("Number", "60");
        attachParams.addTextResponse("question 1", "tekst tekst");
        attachParams.addCheckResponse("question 4", "yes");
        attachParams.addTextResponse("question 3", "25/02/2011");
        attachParams.addTextResponse("Date", "09/02/2011");
        attachParams.addTextResponse("DateQuestion", "19/02/2011");
        attachParams.addTextResponse("Text", "ale alo olu");
        AttachQuestionGroupParameters attachErrorParams = new AttachQuestionGroupParameters();
        attachErrorParams.setTarget("0002-000000003");
        attachErrorParams.addError("Please specify a number for Number.");
        attachErrorParams.setQuestionGroupName(createQuestionGroupParameters.getTitle());
        attachErrorParams.addTextResponse("Number", "sdfsdf");
        attachErrorParams.addCheckResponse("question 4", "yes");
        attachErrorParams.addTextResponse("question 3", "25/02/2011");

        //When
        questionGroupTestHelper.createQuestionGroup(createQuestionGroupParameters);

        questionGroupTestHelper.verifyErrorsWhileAttachingQuestionGroupToClient(attachErrorParams);
        questionGroupTestHelper.attachQuestionGroupToClient(attachParams);

        attachParams.addTextResponse("Number", "20");
        attachParams.addTextResponse("question 3", "21/02/2011");
        //Then
        questionGroupTestHelper.editQuestionGroupResponsesInClient(attachParams);
    }

    private void testValidationAddQuestionGroup() {
        questionGroupTestHelper.validatePageBlankMandatoryField();
        questionGroupTestHelper.validateQuestionGroupTitle(charactersList);
    }

    private void testValidationAddQuestion() {
        CreateQuestionPage createQuestionPage = questionGroupTestHelper.navigateToCreateQuestionPage();
        CreateQuestionParameters createQuestionParameters = new CreateQuestionParameters();
        createQuestionParameters.setText("");
        createQuestionParameters.setType(CreateQuestionParameters.TYPE_FREE_TEXT);
        createQuestionPage.addQuestion(createQuestionParameters);
        createQuestionPage.verifyTextPresent("Please specify the question", "No text <Please specify the question> present on the page");
        createQuestionPage.verifySubmitButtonStatus("true");
        createQuestionPage.cancelQuestion();
        createQuestionPage = questionGroupTestHelper.navigateToCreateQuestionPage();
        createQuestionPage.setNumberQuestion("NumberQuestion",noNumber,noNumber);
        createQuestionPage.verifyNumberQuestion("", "");
        createQuestionPage.setNumberQuestion("NumberQuestion","","");
        for(int i=0;i<charactersList.size();i++) {
            createQuestionParameters.setText(charactersList.get(i));
            createQuestionPage.addQuestion(createQuestionParameters);
            createQuestionPage.verifyQuestionPresent(createQuestionParameters, i+1);
        }
    }

    private void testViewQuestionGroups() {
        ViewAllQuestionGroupsPage viewQuestionGroupsPage = getViewQuestionGroupsPage(new AdminPage(selenium));
        testViewQuestionGroups(viewQuestionGroupsPage);
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle1, SECTION_DEFAULT, asList(qTitle1, qTitle2), asList(qTitle1));
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle1, SECTION_MISC, asList(qTitle4), EMPTY_LIST);
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle2, SECTION_MISC, asList(qTitle1, qTitle3), asList(qTitle1));
        testEditQuestionGroupDetail(viewQuestionGroupsPage.navigateToQuestionGroupDetailPage(qgTitle2));
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle3, SECTION_MISC, asList(qTitle1, qTitle3), asList(qTitle1));
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle3, "New Section", asList(qTitle4), EMPTY_LIST);
        testQuestionGroupDetail(viewQuestionGroupsPage, qgTitle3, "Hello World", asList(qTitle5), EMPTY_LIST);
    }

    private void testEditQuestionGroupDetail(QuestionGroupDetailPage questionGroupDetailPage) {
        EditQuestionGroupPage editQuestionGroupPage = questionGroupDetailPage.navigateToEditPage();
        editQuestionGroupPage.setTitle(qgTitle3);
        CreateQuestionGroupParameters createQuestionGroupParameters = new CreateQuestionGroupParameters();
        createQuestionGroupParameters.addExistingQuestion("New Section", qTitle4);
        for(String section : createQuestionGroupParameters.getExistingQuestions().keySet()){
            editQuestionGroupPage.addExistingQuestion(section, createQuestionGroupParameters.getExistingQuestions().get(section));
        }
        CreateQuestionParameters createQuestionParameters= new CreateQuestionParameters();
        createQuestionParameters.setType("Free Text");
        createQuestionParameters.setText(qTitle5);
        editQuestionGroupPage.setSection("Hello World");
        editQuestionGroupPage.addNewQuestion(createQuestionParameters);
        editQuestionGroupPage.submit();
        questionGroupDetailPage.verifyPage();
        questionGroupDetailPage.navigateToViewQuestionGroupsPage();
    }

    private AdminPage createQuestions(String... qTitles) {
        CreateQuestionPage createQuestionPage = getAdminPage().navigateToCreateQuestionPage();
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        for (String qTitle : qTitles) {
            parameters.setText(qTitle);
            parameters.setType("Free Text");
            createQuestionPage.addQuestion(parameters);
        }
        return createQuestionPage.submitQuestions().verifyPage();
    }

    private void testMissingMandatoryInputs(CreateQuestionGroupPage createQuestionGroupPage) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        createQuestionGroupPage.submit(parameters);
        assertTextFoundOnPage(TITLE_MISSING);
        assertTextFoundOnPage(APPLIES_TO_MISSING);
        assertTextFoundOnPage(SECTION_MISSING);
        createQuestionGroupPage.addEmptySection("Empty Section");
        for(String section : parameters.getExistingQuestions().keySet()){
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        assertTextFoundOnPage(QUESTION_MISSING);
    }

    private void testCancelCreateQuestionGroup(CreateQuestionGroupPage createQuestionGroupPage) {
        createQuestionGroupPage.cancel();
        assertPage(AdminPage.PAGE_ID);
    }

    private void testQuestionGroupDetail(ViewAllQuestionGroupsPage viewAllQuestionGroupsPage, String title,
                                         String sectionName, List<String> questions, List<String> mandatoryQuestions) {
        QuestionGroupDetailPage questionGroupDetailPage = navigateToQuestionGroupDetailPage(viewAllQuestionGroupsPage, title);
        testQuestionGroupDetail(questionGroupDetailPage, title, sectionName, questions, mandatoryQuestions);
        questionGroupDetailPage.navigateToViewQuestionGroupsPage().verifyPage();
    }

    private QuestionGroupDetailPage navigateToQuestionGroupDetailPage(ViewAllQuestionGroupsPage viewAllQuestionGroupsPage, String title) {
        QuestionGroupDetailPage questionGroupDetailPage = viewAllQuestionGroupsPage.navigateToQuestionGroupDetailPage(title);
        questionGroupDetailPage.verifyPage();
        return questionGroupDetailPage;
    }

    private void testQuestionGroupDetail(QuestionGroupDetailPage questionGroupDetailPage, String title, String sectionName,
                                                            List<String> questions, List<String> mandatoryQuestions) {
        assertEquals(title, questionGroupDetailPage.getTitle());
        assertEquals(APPLIES_TO_CREATE_CLIENT, questionGroupDetailPage.getAppliesTo());
        assertTrue(questionGroupDetailPage.getSections().contains(sectionName));
        assertEquals(questions, questionGroupDetailPage.getSectionsQuestions(sectionName));
        assertEquals(mandatoryQuestions, questionGroupDetailPage.getMandatoryQuestions(sectionName));
    }

    private CreateQuestionGroupPage getCreateQuestionGroupPage(AdminPage adminPage) {
        return adminPage.navigateToCreateQuestionGroupPage();
    }

    private void testViewQuestionGroups(ViewAllQuestionGroupsPage viewQuestionGroupsPage) {
        String[] questionGroups = viewQuestionGroupsPage.getAllQuestionGroups();
        assertEquals(3, questionGroups.length);
        assertEquals(qgTitle1, questionGroups[0]);
        assertEquals(qgTitle2, questionGroups[1]);
        assertEquals(qgTitle2, questionGroups[2]);
    }

    private ViewAllQuestionGroupsPage getViewQuestionGroupsPage(AdminPage adminPage) {
        return adminPage.navigateToViewAllQuestionGroups().verifyPage();
    }

    private AdminPage getAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        return homePage.navigateToAdminPage().verifyPage();
    }

    private void testShouldAllowDuplicateTitlesForQuestionGroup() {
        testCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)), qgTitle2, APPLIES_TO_CREATE_CLIENT, false, "", asList(qTitle1, qTitle3), asList(qTitle2), null);
        testCreateQuestionGroup(getCreateQuestionGroupPage(new AdminPage(selenium)), qgTitle2, APPLIES_TO_CREATE_CLIENT, false, "Hello", asList(qTitle2), asList(qTitle1, qTitle3), null);
    }

    private void testCreateQuestionGroup(CreateQuestionGroupPage createQuestionGroupPage, String title, String appliesTo, boolean isAnswerEditable,
                                         String sectionName, List<String> questionsToSelect, List<String> questionsNotToSelect, String questionToAdd) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(title);
        parameters.setAppliesTo(appliesTo);
        parameters.setAnswerEditable(isAnswerEditable);
        for (String question : questionsToSelect) {
            parameters.addExistingQuestion(sectionName, question);
        }
        for(String section : parameters.getExistingQuestions().keySet()){
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(questionsToSelect);
        assertTrue(createQuestionGroupPage.getAvailableQuestions().containsAll(questionsNotToSelect));
        CreateQuestionParameters createQuestionParameters= new CreateQuestionParameters();
        createQuestionParameters.setType("Free Text");
        createQuestionParameters.setText(questionToAdd);
        createQuestionGroupPage.setSection(SECTION_MISC);
        if (questionToAdd != null) {
            createQuestionGroupPage.addNewQuestion(createQuestionParameters);
        }
        createQuestionGroupPage.submit(parameters);
        assertPage(AdminPage.PAGE_ID);
    }

    public Integer latestInstanceId(Map<Integer, QuestionGroup> questionGroups) {
        Set<Integer> keys = questionGroups.keySet();
        return Collections.max(keys);
    }
    
    private void editResponses(ClientViewDetailsPage clientViewDetailsPage, int id, Map<String,String> answers) {
        QuestionGroupResponsePage questionGroupResponsePage = clientViewDetailsPage.navigateToQuestionGroupResponsePage(id);
        QuestionnairePage questionnairePage = questionGroupResponsePage.navigateToEditResponses();
        for(String question: answers.keySet()) {
            questionnairePage.setResponse(question, answers.get(question));
        }
        ClientViewDetailsPage clientViewDetailsPage2 = (ClientViewDetailsPage)questionnairePage.submit();
        Assert.assertEquals(clientViewDetailsPage2.getQuestionGroupInstances().get(id).getName(), "TestQuestionGroup");
    }
}

