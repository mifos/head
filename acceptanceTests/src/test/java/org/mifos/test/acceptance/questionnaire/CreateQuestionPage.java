package org.mifos.test.acceptance.questionnaire;

import java.util.Arrays;

import org.mifos.test.acceptance.framework.admin.AdminPage;

import com.thoughtworks.selenium.Selenium;

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
}
