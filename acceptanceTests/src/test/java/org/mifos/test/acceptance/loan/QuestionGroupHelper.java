package org.mifos.test.acceptance.loan;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.questionnaire.CreateQuestionParameters;

import java.util.List;

import static java.util.Arrays.asList;

public class QuestionGroupHelper {
    private final NavigationHelper navigationHelper;

    public QuestionGroupHelper(NavigationHelper navigationHelper) {

        this.navigationHelper = navigationHelper;
    }

    void createQuestionGroup(String questionGroupTitle, String question1, String question2, String appliesTo) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage().verifyPage();
        createQuestionPage.addQuestion(getCreateQuestionParams(question1, CreateClientLoanAccountTest.DATE, null));
        createQuestionPage.addQuestion(getCreateQuestionParams(question2, CreateClientLoanAccountTest.SINGLE_SELECT, asList("Choice1", "Choice2")));
        adminPage = createQuestionPage.submitQuestions();

        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage().verifyPage();
        CreateQuestionGroupParameters parameters = getCreateQuestionGroupParameters(questionGroupTitle, question1, question2, appliesTo);
        createQuestionGroupPage.addSection(parameters);
        createQuestionGroupPage.markEveryOtherQuestionsMandatory(asList(question1));
        createQuestionGroupPage.submit(parameters);
    }

    private CreateQuestionParameters getCreateQuestionParams(String title, String type, List<String> choices) {
        CreateQuestionParameters parameters = new CreateQuestionParameters();
        parameters.setTitle(title);
        parameters.setType(type);
        parameters.setChoicesFromStrings(choices);
        return parameters;
    }

    private CreateQuestionGroupParameters getCreateQuestionGroupParameters(String questionGroupTitle, String question1, String question2, String appliesTo) {
        CreateQuestionGroupParameters parameters = new CreateQuestionGroupParameters();
        parameters.setTitle(questionGroupTitle);
        parameters.setAppliesTo(appliesTo);
        parameters.setAnswerEditable(true);
        parameters.setSectionName("Default Section");
        parameters.setQuestions(asList(question1, question2));
        return parameters;
    }
}