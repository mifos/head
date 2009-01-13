/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class CreateUserPreviewDataPage extends MifosPage {

	public CreateUserPreviewDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CreateUserPreviewDataPage(Selenium selenium) {
		super(selenium);
	}

    public CreateUserConfirmationPage submit() {
        selenium.click("createuser_preview.button.submit");
        waitForPageToLoad();
        return new CreateUserConfirmationPage(selenium);
    }
}
