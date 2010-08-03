package org.mifos.test.acceptance.questionnaire;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.admin.AdminPage;

import java.util.Arrays;

public class CreateQuestionPage extends MifosPage {
    public CreateQuestionPage(Selenium selenium) {
        super(selenium);
    }

    public CreateQuestionPage verifyPage() {
        verifyPage("createQuestion");
        return this;
    }


    public CreateQuestionPage addQuestion(CreateQuestionParameters createQuestionParameters) {
        selenium.type("currentQuestion.title", createQuestionParameters.getTitle());
        selenium.select("id=currentQuestion.type", "value=" + createQuestionParameters.getType());
        fillUpChoices(createQuestionParameters);
        fillUpNumericDetails(createQuestionParameters);
        selenium.click("_eventId_addQuestion");
        waitForPageToLoad();
        return new CreateQuestionPage(selenium);
    }

    private void fillUpNumericDetails(CreateQuestionParameters createQuestionParameters) {
        if (createQuestionParameters.isNumericQuestionType()) {
            selenium.type("currentQuestion.numericMin", createQuestionParameters.getNumericMin().toString());
            selenium.type("currentQuestion.numericMax", createQuestionParameters.getNumericMax().toString());
        }
    }

    private void fillUpChoices(CreateQuestionParameters createQuestionParameters) {
        if (createQuestionParameters.questionHasAnswerChoices()) {
            for (String choice : createQuestionParameters.getChoices()) {
                selenium.type("currentQuestion.currentChoice", choice);
                selenium.keyUp("id=currentQuestion.currentChoice"," ");
                selenium.click("_eventId_addChoice");
                waitForPageToLoad();
            }
        }
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
        questionParameters.setTitle(selenium.getTable("questions.table." + indexOfLastQuestion + ".0"));
        questionParameters.setType(selenium.getTable("questions.table." + indexOfLastQuestion + ".1"));
        String[] choices = selenium.getTable("questions.table." + indexOfLastQuestion + ".2").split(", ");
        questionParameters.setChoices(Arrays.asList(choices));
        return questionParameters;
    }
}
