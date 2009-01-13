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
	
    public void setFirstName(String firstName) {
        selenium.type("edit_user.input.firstName", firstName);
    }
    
    public void setLastName(String lastName) {
        selenium.type("edit_user.input.lastName", lastName);
    }
    
    public void setEmail(String email) {
        selenium.type("edit_user.input.email", email);
    }
    
    public void setDateOfBirth(String dateOfBirthDD, String dateOfBirthMM, String dateOfBirthYYYY) {
        selenium.type("dobDD", dateOfBirthDD);
        selenium.type("dobMM", dateOfBirthMM);
        selenium.type("dobYY", dateOfBirthYYYY);        
    }
      
    public void setMaritalStatus(String maritalStatus) {
        selenium.select("maritalStatus", "label=" + maritalStatus);
    }
      
    public void setGender(String gender) {
        selenium.select("gender", "label=" + gender);
    }
    
    public void setPreferredLanguage(String preferredLanguage) {
        selenium.select("preferredLocale", "label=" + preferredLanguage);
    }
    
    public void setUserLevel(String userLevel) {
        selenium.select("level", "label=" + userLevel);
    }
    
    public void setUserName(String userName) {
        selenium.type("edit_user.input.userName", userName);
    }
    
    public void setPassword(String password) {
        selenium.type("edit_user.input.password", password);
    }
    
    public void setPasswordRepeat(String passwordRepeat) {
        selenium.type("edit_user.input.passwordRepeat", passwordRepeat);
    }    

    public EditUserPreviewDataPage preview() {
        selenium.click("edit_user.button.preview");
        waitForPageToLoad();
        return new EditUserPreviewDataPage(selenium);
    }
}
