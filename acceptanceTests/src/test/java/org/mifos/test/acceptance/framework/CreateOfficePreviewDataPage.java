/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class CreateOfficePreviewDataPage extends MifosPage {

	public CreateOfficePreviewDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CreateOfficePreviewDataPage(Selenium selenium) {
		super(selenium);
	}
	
	public String getOfficeName() {
	    return selenium.getText("preview_new_office.text.officeName");
	}

	public String getShortName() {
	    return selenium.getText("preview_new_office.text.shortName");
	}
	
    public CreateOfficeConfirmationPage submit() {
        selenium.click("preview_new_office.button.submit");
        waitForPageToLoad();
        return new CreateOfficeConfirmationPage(selenium);
    }
}
