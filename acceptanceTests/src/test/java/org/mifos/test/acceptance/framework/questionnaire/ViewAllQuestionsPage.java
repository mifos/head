package org.mifos.test.acceptance.framework.questionnaire;

import com.thoughtworks.selenium.Selenium;
import java.util.Set;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.testng.Assert;

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

    public void verifyQuestions(Set<String> questions) {
        for(String question : questions) {
            Assert.assertTrue(selenium.isTextPresent(question));
        }
    }
}
