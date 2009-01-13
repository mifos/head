/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class EditUserPreviewDataPage extends MifosPage {

	public EditUserPreviewDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public EditUserPreviewDataPage(Selenium selenium) {
		super(selenium);
	}

    public UserViewDetailsPage submit() {
        selenium.click("preview_EditUser.button.submit");
        waitForPageToLoad();
        return new UserViewDetailsPage(selenium);
    }
}
