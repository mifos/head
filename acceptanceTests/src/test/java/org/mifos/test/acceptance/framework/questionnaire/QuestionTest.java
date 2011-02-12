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
package org.mifos.test.acceptance.framework.questionnaire;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"client", "acceptance", "ui", "smoke"})
public class QuestionTest extends UiTestCaseBase {
    private AppLauncher appLauncher;
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    private static final String START_DATA_SET = "acceptance_small_003_dbunit.xml";
    private String title;
    private static final String TITLE_MISSING = "Please specify the question.";
    private static final String AT_LEAST_2_CHOICES = "Please specify at least 2 choices.";
    private static final String DUPLICATE_TITLE = "Question already exists.";
    private static final String DATE = "Date";
    private static final String FREE_TEXT = "Free Text";
    private static final String MULTI_SELECT = "Multi Select";
    private static final String SINGLE_SELECT = "Single Select";
    private static final String NUMBER = "Number";
    public static final String INVALID_BOUNDS_ERR = "Please ensure maximum value is greater than minimum value.";
    private CreateQuestionParameters createQuestionParameters;
    private AdminPage adminPage;
    private ViewAllQuestionsPage viewAllQuestionsPage;
    private CreateQuestionPage createQuestionPage;
    private List<String> types;
    private static final List<Choice> EMPTY_CHOICES_LIST = Collections.<Choice>emptyList();
    private static final List<String> EMPTY_TAGS_LIST = Collections.<String>emptyList();


    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);
        title = "Title " + System.currentTimeMillis();
        types = Arrays.asList(
                FREE_TEXT,
                DATE,
                NUMBER,
                SINGLE_SELECT,
                MULTI_SELECT,
                "Smart Select");
        createQuestionParameters = new CreateQuestionParameters();
        adminPage = getAdminPage();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    public void createQuestion() {
        testCreateQuestions();
        testDuplicateTitleForExistingQuestionInDB();
        testViewAllQuestions(types);
        testViewQuestionDetails();
        testEditQuestions();
        testViewAllQuestions(types);
        testViewQuestionDetailsAfterEdit();
    }

    private void testEditQuestions() {
        adminPage.navigateToViewAllQuestions();
        testEditQuestion(FREE_TEXT, title, 0, 0, EMPTY_CHOICES_LIST);
        testEditQuestion(DATE, title, 0, 0, EMPTY_CHOICES_LIST);
        testEditQuestion(NUMBER, title, 1, 2, EMPTY_CHOICES_LIST);
        testEditQuestion(SINGLE_SELECT, title, 0, 0, asList(getChoice("choice5"), getChoice("choice6")));
        testEditQuestion(MULTI_SELECT, title, 0, 0, asList(getChoice("choice6"), getChoice("choice7")));
        testEditQuestion("Smart Select", title, 0, 0, asList(new Choice("Choice3", asList("Tag5", "Tag6"))));
        title = "new" + title;
        viewAllQuestionsPage.navigateToAdminPage();
    }

    private void testEditQuestion(String type, String title, Integer numericMin, Integer numericMax, List<Choice> choices) {
        QuestionDetailPage questionDetailPage = viewAllQuestionsPage.navigateToQuestionDetail(title + type);
        EditQuestionPage editQuestionPage = questionDetailPage.navigateToEditQuestionPage();
        setupQuestionParameters("new" + title + type, type, choices, numericMin, numericMax);
        questionDetailPage = editQuestionPage.update(createQuestionParameters);
        viewAllQuestionsPage = questionDetailPage.navigateToViewAllQuestionsPage();
    }


    private void testGetCreateQuestionPage() {
        createQuestionPage = adminPage.navigateToCreateQuestionPage();
    }

    private void testForAtLeastTwoChoices() {
        testAddQuestion(MULTI_SELECT, title + 1, 0, 0, asList(getChoice("choice2")));
    }

    private void testInvalidNumericBounds() {
        setupQuestionParameters(title + NUMBER, NUMBER, EMPTY_CHOICES_LIST, 100, 10);
        createQuestionPage.addQuestion(createQuestionParameters);
        assertTextFoundOnPage(INVALID_BOUNDS_ERR);
    }

    private void testAddQuestions() {
        testAddQuestion(FREE_TEXT, title, 0, 0, EMPTY_CHOICES_LIST);
        testAddQuestion(DATE, title, 0, 0, EMPTY_CHOICES_LIST);
        testAddQuestion(NUMBER, title, 10, 100, EMPTY_CHOICES_LIST);
        testAddQuestion(SINGLE_SELECT, title, 0, 0, asList(getChoice("choice1"), getChoice("choice2")));
        testAddQuestion(MULTI_SELECT, title, 0, 0, asList(getChoice("choice2"), getChoice("choice3")));
        testAddQuestion("Smart Select", title, 0, 0, asList(new Choice("Choice1", asList("Tag1", "Tag2")), new Choice("Choice2", asList("Tag3", "Tag4"))));
    }

    private Choice getChoice(String choiceText) {
        return new Choice(choiceText, EMPTY_TAGS_LIST);
    }

    private void testAddQuestion(String type, String title, int numericMin, int numericMax, List<Choice> choices) {
        setupQuestionParameters(title + type, type, choices, numericMin, numericMax);
        testAddQuestion();
    }

    private void setupQuestionParameters(String title, String type, List<Choice> choices, int numericMin, int numericMax) {
        createQuestionParameters.setText(title);
        createQuestionParameters.setType(type);
        createQuestionParameters.setChoices(choices);
        createQuestionParameters.setNumericMin(numericMin);
        createQuestionParameters.setNumericMax(numericMax);
    }

    private void testAddQuestion() {
        createQuestionPage.addQuestion(createQuestionParameters);
        createQuestionPage.verifyNotTextPresent(TITLE_MISSING, "Missing title error should not appear");
        createQuestionPage.assertRecentQuestion(createQuestionParameters, AT_LEAST_2_CHOICES);
    }

    @SuppressWarnings("PMD")
    private void testViewQuestionDetails() {
        adminPage.navigateToViewAllQuestions();
        testViewQuestionDetail(DATE, 0, 0, EMPTY_CHOICES_LIST);
        testViewQuestionDetail(FREE_TEXT, 0, 0, EMPTY_CHOICES_LIST);
        testViewQuestionDetail(NUMBER, 10, 100, EMPTY_CHOICES_LIST);
        testViewQuestionDetail(SINGLE_SELECT, 0, 0, asList(getChoice("choice1"), getChoice("choice2")));
        testViewQuestionDetail(MULTI_SELECT, 0, 0, asList(getChoice("choice2"), getChoice("choice3")));
        testViewQuestionDetail("Smart Select", 0, 0, asList(
                new Choice("Choice1", asList("Tag1", "Tag2")),
                new Choice("Choice2", asList("Tag3", "Tag4"))
        ));
        viewAllQuestionsPage.navigateToAdminPage();
    }

    @SuppressWarnings("PMD")
    private void testViewQuestionDetailsAfterEdit() {
        adminPage.navigateToViewAllQuestions();
        testViewQuestionDetail(DATE, 0, 0, EMPTY_CHOICES_LIST);
        testViewQuestionDetail(FREE_TEXT, 0, 0, EMPTY_CHOICES_LIST);
        testViewQuestionDetail(NUMBER, 1, 2, EMPTY_CHOICES_LIST);
        testViewQuestionDetail(SINGLE_SELECT, 0, 0, asList(getChoice("choice1"), getChoice("choice2"), getChoice("choice5"), getChoice("choice6")));
        testViewQuestionDetail(MULTI_SELECT, 0, 0, asList(getChoice("choice2"), getChoice("choice3"), getChoice("choice6"), getChoice("choice7")));
        testViewQuestionDetail("Smart Select", 0, 0, asList(
                new Choice("Choice1", asList("Tag1", "Tag2")),
                new Choice("Choice2", asList("Tag3", "Tag4")),
                new Choice("Choice3", asList("Tag5", "Tag6"))
        ));
        viewAllQuestionsPage.navigateToAdminPage();
    }

    private void testViewQuestionDetail(String type, int numericMin, int numericMax, List<Choice> choices) {
        QuestionDetailPage questionDetailPage = viewAllQuestionsPage.navigateToQuestionDetail(title + type);
        questionDetailPage.verifyPage();
        questionDetailPage.verifyQuestionTitle(type, title);
        questionDetailPage.assertForChoices(type, choices);
        questionDetailPage.assertForNumericDetails(type, numericMin, numericMax);
        viewAllQuestionsPage = questionDetailPage.navigateToViewAllQuestionsPage();
    }

    private void testViewAllQuestions(List<String> types) {
        viewAllQuestionsPage = adminPage.navigateToViewAllQuestions();
        viewAllQuestionsPage.verifyPage();
        for (String type : types) {
            assertTextFoundOnPage(title + type);
        }
        viewAllQuestionsPage.navigateToAdminPage();
    }

    private AdminPage getAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        return homePage.navigateToAdminPage();
    }

    private void testDuplicateTitleForExistingQuestionInDB() {
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        createQuestionParameters.setText(title + DATE);
        createQuestionPage.addQuestion(createQuestionParameters);
        createQuestionPage.verifyTextPresent(DUPLICATE_TITLE, "Duplicate title error should appear");
        adminPage = createQuestionPage.navigateToAdminPage();
    }

    private void testCreateQuestions() {
        testGetCreateQuestionPage();
        testSubmitButtonDisabled();
        testMissingTitle();
        testInvalidNumericBounds();
        testAddQuestions();
        testForAtLeastTwoChoices();
        testDuplicateTitle();
        adminPage = createQuestionPage.submitQuestions();
    }

    private void testDuplicateTitle() {
        createQuestionParameters.setText(title + FREE_TEXT);
        createQuestionPage.addQuestion(createQuestionParameters);
        createQuestionPage.verifyTextPresent(DUPLICATE_TITLE, "Duplicate title error should appear");
    }

    private void testSubmitButtonDisabled() {
        assertEquals("true", createQuestionPage.submitButtonStatus());
        assertEquals("disabledbuttn", createQuestionPage.submitButtonClass());
    }

    private void testMissingTitle() {
        createQuestionParameters.setText("");
        createQuestionParameters.setType(DATE);
        createQuestionPage.addQuestion(createQuestionParameters);
        assertTextFoundOnPage(TITLE_MISSING);
    }
}

