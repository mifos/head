package org.mifos.test.acceptance.framework.testhelpers;

import java.util.List;
import java.util.Map;

import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.questionnaire.CreateQuestionGroupPage;
import org.mifos.test.acceptance.questionnaire.CreateQuestionGroupParameters;
import org.mifos.test.acceptance.questionnaire.CreateQuestionPage;
import org.mifos.test.acceptance.questionnaire.CreateQuestionParameters;
import org.mifos.test.acceptance.questionnaire.EditQuestionGroupPage;
import org.mifos.test.acceptance.questionnaire.EditQuestionPage;
import org.mifos.test.acceptance.questionnaire.QuestionDetailPage;
import org.mifos.test.acceptance.questionnaire.QuestionGroupDetailPage;
import org.mifos.test.acceptance.questionnaire.ViewAllQuestionGroupsPage;
import org.mifos.test.acceptance.questionnaire.ViewAllQuestionsPage;

import com.thoughtworks.selenium.Selenium;

public class QuestionGroupTestHelper {

    private final NavigationHelper navigationHelper;

    public QuestionGroupTestHelper(Selenium selenium) {
        super();
        this.navigationHelper = new NavigationHelper(selenium);
    }

    public void createQuestionGroup(CreateQuestionGroupParameters createQuestionGroupParameters) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionGroupPage createQuestionGroupPage = adminPage.navigateToCreateQuestionGroupPage();
        for (String section : createQuestionGroupParameters.getExistingQuestions().keySet()) {
            createQuestionGroupPage.addExistingQuestion(section, createQuestionGroupParameters.getExistingQuestions().get(section));
        }
        for (String section : createQuestionGroupParameters.getNewQuestions().keySet()){
            createQuestionGroupPage.setSection(section);
            for(CreateQuestionParameters createQuestionParameters : createQuestionGroupParameters.getNewQuestions().get(section)){
                createQuestionGroupPage.addNewQuestion(createQuestionParameters);
            }
        }
        createQuestionGroupPage.submit(createQuestionGroupParameters);
    }

    public void createQuestions(List<CreateQuestionParameters> questions){
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        CreateQuestionPage createQuestionPage = adminPage.navigateToCreateQuestionPage();
        for (CreateQuestionParameters question : questions) {
            createQuestionPage.addQuestion(question);
        }
        adminPage = createQuestionPage.submitQuestions();
    }

    public void addQuestionsToQuestionGroup(String questionGroup, Map<String, List<String>> existingQuestions){
        EditQuestionGroupPage editQuestionGroupPage = naviagateToEditQuestionGroup(questionGroup);
        for (String section : existingQuestions.keySet()) {
            editQuestionGroupPage.addExistingQuestion(section, existingQuestions.get(section));
        }
        editQuestionGroupPage.submit();
    }

    private EditQuestionGroupPage naviagateToEditQuestionGroup(String questionGroup){
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewAllQuestionGroupsPage allQuestionGroupsPage = adminPage.navigateToViewAllQuestionGroups();
        QuestionGroupDetailPage questionGroupDetailPage = allQuestionGroupsPage.navigateToQuestionGroupDetailPage(questionGroup);
        return questionGroupDetailPage.navigateToEditPage();
    }

    private EditQuestionPage naviagateToEditQuestion(String question){
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewAllQuestionsPage viewAllQuestionsPage = adminPage.navigateToViewAllQuestions();
        QuestionDetailPage questionDetailPage = viewAllQuestionsPage.navigateToQuestionDetail(question);
        return questionDetailPage.navigateToEditQuestionPage();
    }

    public void markQuestionAsInactive(String question) {
        EditQuestionPage editQuestionPage = naviagateToEditQuestion(question);
        editQuestionPage.deactivate();
    }

    public void markQuestionAsActive(String question) {
        EditQuestionPage editQuestionPage = naviagateToEditQuestion(question);
        editQuestionPage.activate();
    }

    public void markQuestionGroupAsActive(String questionGroup) {
        EditQuestionGroupPage editQuestionGroupPage = naviagateToEditQuestionGroup(questionGroup);
        editQuestionGroupPage.activate();
    }

    public void markQuestionGroupAsInactive(String questionGroup) {
        EditQuestionGroupPage editQuestionGroupPage = naviagateToEditQuestionGroup(questionGroup);
        editQuestionGroupPage.deactivate();
    }
}