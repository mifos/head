package org.mifos.test.acceptance.questionnaire;

import java.util.List;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class CreateQuestionRootPage extends MifosPage {

    public CreateQuestionRootPage(Selenium selenium) {
        super(selenium);
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

}
