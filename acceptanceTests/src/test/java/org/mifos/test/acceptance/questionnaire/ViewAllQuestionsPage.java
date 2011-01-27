package org.mifos.test.acceptance.questionnaire;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.admin.AdminPage;

public class ViewAllQuestionsPage extends MifosPage {
    public ViewAllQuestionsPage(Selenium selenium) {
        super(selenium);
    }

    public ViewAllQuestionsPage verifyPage(){
        verifyPage("view_questions");
        return this;
    }

    public QuestionDetailPage navigateToQuestionDetail(String title) {
        selenium.click("link="+title);
        waitForPageToLoad();
        return new QuestionDetailPage(selenium);
    }

    public AdminPage navigateToAdminPage() {
        selenium.click("link="+"Admin");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }
}
