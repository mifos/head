package org.mifos.test.acceptance.questionnaire;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.admin.AdminPage;

public class CreateQuestionPage extends MifosPage {
    public CreateQuestionPage(Selenium selenium) {
        super(selenium);
    }

    public CreateQuestionPage verifyPage() {
        verifyPage("createQuestion");
        return this;
    }


    public CreateQuestionPage addQuestion(CreateQuestionParameters createQuestionParameters) {
        selenium.type("title", createQuestionParameters.getTitle());
        selenium.select("id=type","value=Date");
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
}
