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
package org.mifos.test.acceptance.questionnaire;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.QuestionDetailPage;
import org.mifos.test.acceptance.framework.admin.ViewAllQuestionsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    private static final String START_DATA_SET = "acceptance_small_003_dbunit.xml.zip";
    private String title;
    private static final String TITLE_MISSING = "Please specify the question title.";
    private static final String AT_LEAST_2_CHOICES = "Please specify at least 2 choices.";
    private static final String DUPLICATE_TITLE = "Question title already exists.";
    private CreateQuestionParameters createQuestionParameters;
    private static final String DATE = "Date";
    private static final String FREE_TEXT = "Free Text";
    private static final String MULTI_SELECT = "Multi Select";
    private static final String SINGLE_SELECT = "Single Select";
    private static final String NUMBER = "Number";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);
        title = "Title " + System.currentTimeMillis();
        createQuestionParameters = new CreateQuestionParameters();
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    public void createQuestion() {
        AdminPage adminPage = getAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage().verifyPage();
        testSubmitButtonDisabled(createQuestionPage);
        testMissingTitle(createQuestionPage);
        testAddQuestions(createQuestionPage);
        testDuplicateTitle(createQuestionPage);
        adminPage = testCreateQuestions(createQuestionPage);
        adminPage = testDuplicateTitleForExistingQuestionInDB(adminPage);
        ViewAllQuestionsPage viewAllQuestionsPage = testViewQuestions(adminPage);
        testViewQuestions(viewAllQuestionsPage);
    }

    private void testAddQuestions(CreateQuestionPage createQuestionPage) {
        testAddQuestion(createQuestionPage, FREE_TEXT, title, null, null, null);
        testAddQuestion(createQuestionPage, DATE, title, null, null, null);
        testAddQuestion(createQuestionPage, NUMBER, title, 10, 100, null);
        testAddQuestion(createQuestionPage, SINGLE_SELECT, title, null, null, asList("choice1", "choice2"));
        testAddQuestion(createQuestionPage, MULTI_SELECT, title, null, null, asList("choice2", "choice3"));
        testAddQuestion(createQuestionPage, MULTI_SELECT, title + 1, null, null, asList("choice2"));
    }

    private void testAddQuestion(CreateQuestionPage createQuestionPage, String type, String title, Integer numericMin, Integer numericMax, List<String> choices) {
        setupQuestionParameters(title + type, type, choices, numericMin, numericMax);
        testAddQuestion(createQuestionPage);
    }

    private void setupQuestionParameters(String title, String type, List<String> choices, Integer numericMin, Integer numericMax) {
        createQuestionParameters.setTitle(title);
        createQuestionParameters.setType(type);
        createQuestionParameters.setChoices(choices);
        createQuestionParameters.setNumericMin(numericMin);
        createQuestionParameters.setNumericMax(numericMax);
    }

    private void testAddQuestion(CreateQuestionPage createQuestionPage) {
        createQuestionPage.addQuestion(createQuestionParameters);
        Assert.assertFalse(selenium.isTextPresent(TITLE_MISSING), "Missing title error should not appear");
        List<String> choices = createQuestionParameters.getChoices();
        if (createQuestionParameters.questionHasAnswerChoices() && choices.size() < 2) {
            Assert.assertTrue(selenium.isTextPresent(AT_LEAST_2_CHOICES), "Missing warning for giving at least 2 choices");
        } else {
            assertLastAddedQuestion(createQuestionPage);
        }
    }

    @SuppressWarnings("PMD")
    private void testViewQuestions(ViewAllQuestionsPage viewAllQuestionsPage) {
        viewAllQuestionsPage = testViewQuestionDetail(viewAllQuestionsPage, DATE, null, null, null);
        viewAllQuestionsPage = testViewQuestionDetail(viewAllQuestionsPage, FREE_TEXT, null, null, null);
        viewAllQuestionsPage = testViewQuestionDetail(viewAllQuestionsPage, NUMBER, 10, 100, null);
        viewAllQuestionsPage = testViewQuestionDetail(viewAllQuestionsPage, MULTI_SELECT, null, null, asList("choice2", "choice3"));
        testViewQuestionDetail(viewAllQuestionsPage, SINGLE_SELECT, null, null, asList("choice1", "choice2"));
    }

    private ViewAllQuestionsPage testViewQuestionDetail(ViewAllQuestionsPage viewAllQuestionsPage, String type,
                                                        Integer numericMin, Integer numericMax, List<String> choices) {
        QuestionDetailPage questionDetailPage = viewAllQuestionsPage.navigateToQuestionDetail(title + type);
        questionDetailPage.verifyPage();
        Assert.assertTrue(selenium.isTextPresent("Question: " + title + type), "Title is missing");
        Assert.assertTrue(selenium.isTextPresent("Answer Type: " + type), "Answer type is missing");
        assertForChoices(type, choices);
        assertForNumericDetails(type, numericMin, numericMax);
        return questionDetailPage.navigateToViewAllQuestionsPage();
    }

    private void assertForNumericDetails(String type, Integer numericMin, Integer numericMax) {
        if (isNumericQuestionType(type)) {
            Assert.assertTrue(selenium.isTextPresent("Minimum value: " + numericMin));
            Assert.assertTrue(selenium.isTextPresent("Maximum value: " + numericMax));
        } else {
            Assert.assertFalse(selenium.isTextPresent("Minimum value: "));
            Assert.assertFalse(selenium.isTextPresent("Maximum value: "));
        }
    }

    private void assertForChoices(String type, List<String> choices) {
        if (questionHasAnswerChoices(type)) {
            Assert.assertTrue(selenium.isTextPresent("Answer Choices: " + choices.get(0) + ", " + choices.get(1)));
        } else {
            Assert.assertFalse(selenium.isTextPresent("Answer Choices: "), "Answer type should not be present");
        }
    }

    private ViewAllQuestionsPage testViewQuestions(AdminPage adminPage) {
        ViewAllQuestionsPage viewAllQuestionsPage = adminPage.navigateToViewAllQuestions();
        viewAllQuestionsPage.verifyPage();
        assertTextFoundOnPage(title);
        return viewAllQuestionsPage;
    }

    private AdminPage getAdminPage() {
        HomePage homePage = appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials();
        return homePage.navigateToAdminPage();
    }

    private AdminPage testDuplicateTitleForExistingQuestionInDB(AdminPage adminPage) {
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        createQuestionPage.verifyPage();
        createQuestionParameters.setTitle(title + DATE);
        createQuestionPage.addQuestion(createQuestionParameters);
        Assert.assertTrue(selenium.isTextPresent(DUPLICATE_TITLE), "Duplicate title error should appear");
        return createQuestionPage.navigateToAdminPage();
    }

    private AdminPage testCreateQuestions(CreateQuestionPage createQuestionPage) {
        AdminPage adminPage;
        adminPage = createQuestionPage.submitQuestions();
        adminPage.verifyPage();
        return adminPage;
    }

    private void testDuplicateTitle(CreateQuestionPage createQuestionPage) {
        createQuestionParameters.setTitle(title + FREE_TEXT);
        createQuestionPage.addQuestion(createQuestionParameters);
        Assert.assertTrue(selenium.isTextPresent(DUPLICATE_TITLE), "Duplicate title error should appear");
    }

    private void assertLastAddedQuestion(CreateQuestionPage createQuestionPage) {
        CreateQuestionParameters question = createQuestionPage.getLastAddedQuestion();
        assertEquals(createQuestionParameters.getTitle(), question.getTitle());
        assertEquals(createQuestionParameters.getType(), question.getType());
        if (createQuestionParameters.questionHasAnswerChoices()) {
            Assert.assertEquals(createQuestionParameters.getChoices(), question.getChoices());
        } else {
            Assert.assertEquals(asList("Not Applicable"), question.getChoices());
        }
        testSubmitButtonEnabled(createQuestionPage);
    }

    private boolean questionHasAnswerChoices(String type) {
        return MULTI_SELECT.equals(type) || SINGLE_SELECT.equals(type);
    }

    private boolean isNumericQuestionType(String type) {
        return NUMBER.equals(type);
    }

    private void testSubmitButtonDisabled(CreateQuestionPage createQuestionPage) {
        assertEquals("true", createQuestionPage.submitButtonStatus());
        assertEquals("disabledbuttn", createQuestionPage.submitButtonClass());
    }

    private void testSubmitButtonEnabled(CreateQuestionPage createQuestionPage) {
        assertEquals("false", createQuestionPage.submitButtonStatus());
        assertEquals("buttn", createQuestionPage.submitButtonClass());
    }

    private void testMissingTitle(CreateQuestionPage createQuestionPage) {
        createQuestionParameters.setTitle("");
        createQuestionParameters.setType(DATE);
        createQuestionPage.addQuestion(createQuestionParameters);
        assertTextFoundOnPage(TITLE_MISSING);
    }
}

