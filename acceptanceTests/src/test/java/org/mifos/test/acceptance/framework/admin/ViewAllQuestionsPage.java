package org.mifos.test.acceptance.framework.admin;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.MifosPage;

public class ViewAllQuestionsPage extends MifosPage {
    public ViewAllQuestionsPage(Selenium selenium) {
        super(selenium);
    }

    public ViewAllQuestionsPage verifyPage(){
        verifyPage("view_questions");
        return this;
    }
}
