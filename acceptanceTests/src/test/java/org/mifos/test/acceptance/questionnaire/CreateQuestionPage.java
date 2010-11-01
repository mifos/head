package org.mifos.test.acceptance.questionnaire;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.admin.AdminPage;

import java.util.Arrays;
import java.util.List;

public class CreateQuestionPage extends MifosPage {
    public CreateQuestionPage(Selenium selenium) {
        super(selenium);
    }

    public CreateQuestionPage verifyPage() {
        verifyPage("createQuestion");
        return this;
    }


    public CreateQuestionPage addQuestion(CreateQuestionParameters createQuestionParameters) {
        enterDetails(createQuestionParameters);
        selenium.click("_eventId_addQuestion");
        waitForPageToLoad();
        return new CreateQuestionPage(selenium);
    }

    protected void enterDetails(CreateQuestionParameters createQuestionParameters) {
        selenium.type("currentQuestion.text", createQuestionParameters.getText());
        selenium.select("id=currentQuestion.type", "value=" + translateQuestionType(createQuestionParameters.getType()));
        fillUpChoices(createQuestionParameters);
        fillUpNumericDetails(createQuestionParameters);
    }

    private String translateQuestionType(String type) {
        String toReturn = type;

        if ("Free Text".equals(type)) {
            toReturn = "freeText";
        }
        else if ("Date".equals(type)) {
            toReturn = "date";
        }
        else if ("Multi Select".equals(type)) {
            toReturn = "multiSelect";
        }
        else if ("Single Select".equals(type)) {
            toReturn = "singleSelect";
        }
        else if ("Smart Select".equals(type)) {
            toReturn = "smartSelect";
        }
        else if ("Number".equals(type)) {
            toReturn = "number";
        }

        return toReturn;
    }

    private void fillUpNumericDetails(CreateQuestionParameters createQuestionParameters) {
        if (createQuestionParameters.isNumericQuestionType()) {
            selenium.type("currentQuestion.numericMin", createQuestionParameters.getNumericMin().toString());
            selenium.type("currentQuestion.numericMax", createQuestionParameters.getNumericMax().toString());
        }
    }

    private void fillUpChoices(CreateQuestionParameters createQuestionParameters) {
        if (createQuestionParameters.questionHasAnswerChoices()) {
            setAnswerChoices(createQuestionParameters);
        } else if (createQuestionParameters.questionHasSmartAnswerChoices()) {
            setSmartAnswerChoices(createQuestionParameters);
        }
    }

    private void setSmartAnswerChoices(CreateQuestionParameters createQuestionParameters) {
        List<Choice> choices = createQuestionParameters.getChoices();
        for (int i = 0, choicesSize = choices.size(); i < choicesSize; i++) {
            Choice choice = choices.get(i);
            selenium.type("currentQuestion.currentSmartChoice", choice.getChoiceText());
            selenium.keyUp("id=currentQuestion.currentSmartChoice", " ");
            selenium.click("_eventId_addSmartChoice");
            waitForPageToLoad();
            for (String tag : choice.getTags()) {
                String tagId = "currentQuestion.currentSmartChoiceTags[" + i + "]";
                selenium.type(tagId, tag);
                selenium.keyUp("id=" + tagId, " ");
                selenium.click("addSmartChoiceTag_" + i);
                waitForPageToLoad();
            }
        }
    }

    private void setAnswerChoices(CreateQuestionParameters createQuestionParameters) {
        for (String choice : createQuestionParameters.getChoicesAsStrings()) {
            selenium.type("currentQuestion.currentChoice", choice);
            selenium.keyUp("id=currentQuestion.currentChoice"," ");
            selenium.click("_eventId_addChoice");
            waitForPageToLoad();
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
        questionParameters.setText(selenium.getTable("questions.table." + indexOfLastQuestion + ".0"));
        questionParameters.setType(selenium.getTable("questions.table." + indexOfLastQuestion + ".1"));
        String[] choices = selenium.getTable("questions.table." + indexOfLastQuestion + ".2").split(", ");
        questionParameters.setChoicesFromStrings(Arrays.asList(choices));
        return questionParameters;
    }
}
