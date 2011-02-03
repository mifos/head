package org.mifos.test.acceptance.framework.questionnaire;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.selenium.Selenium;

public class CreateQuestionGroupRootPage extends CreateQuestionRootPage{

    public static final String selectQuestionCmd = "window.$('#questionList li label').each(function(){ if(window.$.trim(window.$(this).html()) == \"%s\") { var question = window.$(this).parent().find(\"input\"); if (question) { question.click(); }}});";

    public CreateQuestionGroupRootPage(Selenium selenium) {
        super(selenium);
    }

    public void cancel() {
        selenium.click("id=_eventId_cancel");
        waitForPageToLoad();
    }

    public void addExistingQuestion(String section, List<String> questions) {
        selenium.check("id=addQuestionFlag0");
        if (!selenium.isVisible("id=selectQuestionsDiv")) {
            selenium.fireEvent("name=addQuestionFlag", "change");
        }
        setSection(section);
        selectSectionQuestions(questions);
        selenium.click("id=_eventId_addSection");
        waitForPageToLoad();
    }

    public void addNewQuestion(CreateQuestionParameters createQuestionParameters) {
        selenium.check("id=addQuestionFlag1");
        if (!selenium.isVisible("id=addQuestionDiv")) {
            selenium.fireEvent("name=addQuestionFlag", "change");
        }
        enterDetails(createQuestionParameters);
        selenium.click("id=_eventId_addQuestion");
        waitForPageToLoad();
    }

    public List<String> getAvailableQuestions() {
        int numAvailQuestions = Integer.valueOf(selenium.getEval("window.$('#questionList li').length"));
        List<String> availQuestions = new ArrayList<String>();
        for (int i=0; i<numAvailQuestions; i++){
            availQuestions.add(selenium.getEval(String.format("window.$(\"#questionList li label\").get(%d).innerHTML",i)));
            //availQuestions.add(selenium.getEval("window.document.getElementById('selectedQuestionIds').options[" + i + "].text"));
        }
        return availQuestions;
    }

    private void selectSectionQuestions(List<String> sectionQuestions) {
        if (sectionQuestions != null && !sectionQuestions.isEmpty()) {
            for (String qTitle : sectionQuestions) {
                selenium.getEval(String.format(selectQuestionCmd, qTitle));
                //selenium.addSelection("id=selectedQuestionIds", "label=" + qTitle);
            }
        }
    }

    public void markEveryOtherQuestionsMandatory(List<String> questionsToSelect) {
        for(int i=0;i<questionsToSelect.size();i++){
            if(i%2==0){
                selenium.click("id=sections["+i+"].sectionQuestions["+i+"].mandatory");
            }
        }
    }

    public void setTitle(String title) {
        selenium.type("name=title", title);
    }

    public void setSection(String section) {
        selenium.type("id=sectionName", section);
    }

}
