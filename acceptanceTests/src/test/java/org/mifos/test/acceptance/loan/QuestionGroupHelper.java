package org.mifos.test.acceptance.loan;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.questionnaire.Choice;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.questionnaire.CreateQuestionParameters;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class QuestionGroupHelper {
    private final NavigationHelper navigationHelper;

    public QuestionGroupHelper(NavigationHelper navigationHelper) {

        this.navigationHelper = navigationHelper;
    }

    public void createQuestionGroup(String questionGroupTitle, String question1, String question2, String appliesTo) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        createQuestionPage.addQuestion(getCreateQuestionParams(question1, CreateClientLoanAccountTest.DATE, null));
        createQuestionPage.addQuestion(getCreateQuestionParams(question2, CreateClientLoanAccountTest.SINGLE_SELECT, asList("Choice1", "Choice2")));
        adminPage = createQuestionPage.submitQuestions();

        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage();
        CreateQuestionGroupParameters parameters = getCreateQuestionGroupParameters(questionGroupTitle, question1, question2, appliesTo);
        for(String section : parameters.getExistingQuestions().keySet()){
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(asList(question1));
        createQuestionGroupPage.submit(parameters);
    }
    
    @SuppressWarnings("unchecked")
	public void createQuestionGroupSmart(String questionGroupTitle, String appliesTo, String question1, String choice1, String choice2, List<String> tag1, List<String> tag2) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        createQuestionPage.addQuestion(getCreateQuestionParams(question1, "Smart Select", asList(choice1, choice2), asList(tag1,tag2)));
        adminPage = createQuestionPage.submitQuestions();

        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage();
        CreateQuestionGroupParameters parameters = getCreateQuestionGroupParameters(questionGroupTitle, question1, appliesTo);
        for(String section : parameters.getExistingQuestions().keySet()){
            createQuestionGroupPage.addExistingQuestion(section, parameters.getExistingQuestions().get(section));
        }
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(asList(question1));
        createQuestionGroupPage.submit(parameters);
    }

    private CreateQuestionParameters getCreateQuestionParams(String title, String type, List<String> choices) {
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        parameters.setText(title);
        parameters.setType(type);
        parameters.setChoicesFromStrings(choices);
        return parameters;
    }
    
    private CreateQuestionParameters getCreateQuestionParams(String title, String type, List<String> choices, List<List<String>> tags) {
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        parameters.setText(title);
        parameters.setType(type);
        
        List<Choice> choiceList = new ArrayList<Choice>();
        for (int i=0; i<choices.size(); i++){
            choiceList.add(createNewChoice(choices.get(i), tags.get(i)));
        }
        
        parameters.setChoices(choiceList);
        return parameters;
    }

    private CreateQuestionGroupParameters getCreateQuestionGroupParameters(String questionGroupTitle, String question1, String question2, String appliesTo) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(questionGroupTitle);
        parameters.setAppliesTo(appliesTo);
        parameters.setAnswerEditable(true);
        parameters.addExistingQuestion("Default Section", question1);
        parameters.addExistingQuestion("Default Section", question2);
        return parameters;
    }
    
    private CreateQuestionGroupParameters getCreateQuestionGroupParameters(String questionGroupTitle, String question1, String appliesTo) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(questionGroupTitle);
        parameters.setAppliesTo(appliesTo);
        parameters.setAnswerEditable(true);
        parameters.addExistingQuestion("Default Section", question1);
        return parameters;
    }
    
    private Choice createNewChoice(String choices, List<String> tags){
        return new Choice(choices, tags);
    }
}
