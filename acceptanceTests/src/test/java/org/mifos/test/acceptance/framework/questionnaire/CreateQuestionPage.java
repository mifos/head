package org.mifos.test.acceptance.framework.questionnaire;

import java.util.Arrays;
import java.util.List;

import org.mifos.test.acceptance.framework.admin.AdminPage;

import com.thoughtworks.selenium.Selenium;
import org.testng.Assert;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

public class CreateQuestionPage extends CreateQuestionRootPage {

    public CreateQuestionPage(Selenium selenium) {
        super(selenium);
        verifyPage("createQuestion");
    }

    public CreateQuestionPage addQuestion(CreateQuestionParameters createQuestionParameters) {
        enterDetails(createQuestionParameters);
        selenium.click("_eventId_addQuestion");
        waitForPageToLoad();
        return new CreateQuestionPage(selenium);
    }

    public AdminPage submitQuestions() {
        selenium.click("_eventId_createQuestions");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }

    public AdminPage navigateToAdminPage() {
        selenium.click("header.link.admin");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }

    public String submitButtonClass() {
        return selenium.getEval("window.document.getElementById('_eventId_createQuestions').className");
    }

    public String submitButtonStatus() {
        return selenium.getEval("window.document.getElementById('_eventId_createQuestions').disabled");
    }

    public CreateQuestionParameters getLastAddedQuestion() {
        CreateQuestionParameters questionParameters = new CreateQuestionParameters();
        String noOfRows = selenium.getEval("window.document.getElementById(\"questions.table\").getElementsByTagName(\"tr\").length;");
        int indexOfLastQuestion = Integer.parseInt(noOfRows) - 1;
        questionParameters.setText(selenium.getTable("questions.table." + indexOfLastQuestion + ".0"));
        questionParameters.setType(selenium.getTable("questions.table." + indexOfLastQuestion + ".1"));
        String[] choices = selenium.getTable("questions.table." + indexOfLastQuestion + ".2").split(", ");
        questionParameters.setChoicesFromStrings(Arrays.asList(choices));
        return questionParameters;
    }

    public void testSubmitButtonEnabled() {
        assertEquals("false", submitButtonStatus());
        assertEquals("buttn", submitButtonClass());
    }

    public void assertLastAddedQuestion(CreateQuestionParameters createQuestionParameters) {
        CreateQuestionParameters question = getLastAddedQuestion();
        assertEquals(createQuestionParameters.getText(), question.getText());
        assertEquals(createQuestionParameters.getType(), question.getType());
        if (createQuestionParameters.questionHasAnswerChoices() || createQuestionParameters.questionHasSmartAnswerChoices()) {
            Assert.assertEquals(createQuestionParameters.getChoicesAsStrings(), question.getChoicesAsStrings());
        } else {
            Assert.assertEquals(asList("Not Applicable"), question.getChoicesAsStrings());
        }
        testSubmitButtonEnabled();
    }

    public void assertRecentQuestion(CreateQuestionParameters createQuestionParameters, String atLeast2Choices) {
        List<String> choices = createQuestionParameters.getChoicesAsStrings();
        if ((createQuestionParameters.questionHasAnswerChoices() || createQuestionParameters.questionHasSmartAnswerChoices()) && choices.size() < 2) {
            Assert.assertTrue(selenium.isTextPresent(atLeast2Choices), "Missing warning for giving at least 2 choices");
        } else {
            assertLastAddedQuestion(createQuestionParameters);
        }
    }

    public void verifyTextPresent(String expectedText, String errorMessage) {
        Assert.assertTrue(selenium.isTextPresent(expectedText), errorMessage);
    }

    public void verifyNotTextPresent(String expectedText, String errorMessage) {
        Assert.assertFalse(selenium.isTextPresent(expectedText), errorMessage);
    }
}
