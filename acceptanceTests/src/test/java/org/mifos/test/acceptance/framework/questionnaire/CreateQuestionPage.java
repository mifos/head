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

    public void setNumberQuestion(String name, String min, String max) {
        selenium.type("currentQuestion.text", name);
        selenium.select("id=currentQuestion.type", "value=number");
        selenium.type("currentQuestion.numericMin", min);
        selenium.type("currentQuestion.numericMax", max);
    }

    public AdminPage submitQuestions() {
        selenium.click("_eventId_createQuestions");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }

    public AdminPage cancelQuestion() {
        selenium.click("_eventId_cancel");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }

    public AdminPage navigateToAdminPage() {
        selenium.click("header.link.admin");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }

    public String submitButtonClass() {
        return getEval("window.document.getElementById('_eventId_createQuestions').className");
    }

    public String submitButtonStatus() {
        return getEval("window.document.getElementById('_eventId_createQuestions').disabled");
    }

    public CreateQuestionParameters getLastAddedQuestion() {
        CreateQuestionParameters questionParameters = new CreateQuestionParameters();
        String noOfRows = getEval("window.document.getElementById(\"questions.table\").getElementsByTagName(\"tr\").length;");
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
            Assert.assertTrue(isTextPresentInPage(atLeast2Choices), "Missing warning for giving at least 2 choices");
        } else {
            assertLastAddedQuestion(createQuestionParameters);
        }
    }

    public void verifyTextPresent(String expectedText, String errorMessage) {
        Assert.assertTrue(isTextPresentInPage(expectedText), errorMessage);
    }

    public void verifyNotTextPresent(String expectedText, String errorMessage) {
        Assert.assertFalse(isTextPresentInPage(expectedText), errorMessage);
    }

    public void verifySubmitButtonStatus(String status) {
        Assert.assertEquals(submitButtonStatus(), status);
    }

    public void verifyNumberQuestion(String min, String max) {
        Assert.assertEquals(selenium.getText("currentQuestion.numericMin"), min);
        Assert.assertEquals(selenium.getText("currentQuestion.numericMax"), max);
    }

    public void verifyQuestionPresent(CreateQuestionParameters createQuestionParameters, int row) {
        assertEquals(selenium.getTable("questions.table." + row + ".0"), createQuestionParameters.getText());
        assertEquals(selenium.getTable("questions.table." + row + ".1"), createQuestionParameters.getType());
    }
}
