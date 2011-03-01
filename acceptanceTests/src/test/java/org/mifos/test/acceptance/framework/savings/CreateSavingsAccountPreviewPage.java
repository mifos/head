package org.mifos.test.acceptance.framework.savings;

import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;

import com.thoughtworks.selenium.Selenium;

public class CreateSavingsAccountPreviewPage  extends AbstractPage{

    public CreateSavingsAccountPreviewPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("createsavingsaccountpreview");
    }

    public CreateSavingsAccountConfirmationPage submitForApproval() {
        selenium.isVisible("createsavingsaccountpreview.button.submitForApproval");
        selenium.click("createsavingsaccountpreview.button.submitForApproval");
        waitForPageToLoad();
        return new CreateSavingsAccountConfirmationPage(selenium);
    }

    public QuestionResponsePage editAdditionalInformation() {
        selenium.click("editQuestionResponses_button");
        waitForPageToLoad();
        return new QuestionResponsePage(selenium);
    }
}
