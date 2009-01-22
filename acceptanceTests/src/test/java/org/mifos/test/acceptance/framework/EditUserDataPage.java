/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class EditUserDataPage extends MifosPage {

	public EditUserDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public EditUserDataPage(Selenium selenium) {
		super(selenium);
	}

    public EditUserPreviewDataPage submitAndGotoEditUserPreviewDataPage(CreateUserEnterDataPage.SubmitFormParameters parameters) {
        typeTextIfNotEmpty("edit_user.input.firstName", parameters.getFirstName());
        typeTextIfNotEmpty("edit_user.input.lastName", parameters.getLastName());
        typeTextIfNotEmpty("edit_user.input.email", parameters.getEmail());
        typeTextIfNotEmpty("dobDD", parameters.getDateOfBirthDD());
        typeTextIfNotEmpty("dobMM", parameters.getDateOfBirthMM());
        typeTextIfNotEmpty("dobYY", parameters.getDateOfBirthYYYY());        
        selectIfNotEmpty("maritalStatus", parameters.getMaritalStatus());
        selectIfNotEmpty("gender", parameters.getGender());
        selectIfNotEmpty("preferredLocale", parameters.getPreferredLanguage());
        selectIfNotEmpty("level", parameters.getUserLevel());
        typeTextIfNotEmpty("edit_user.input.userName", parameters.getUserName());
        typeTextIfNotEmpty("edit_user.input.password", parameters.getPassword());
        typeTextIfNotEmpty("edit_user.input.passwordRepeat", parameters.getPasswordRepeat());
        selenium.click("edit_user.button.preview");
        waitForPageToLoad();
        return new EditUserPreviewDataPage(selenium);
    }
}
