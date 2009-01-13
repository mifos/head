/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class CreateUserEnterDataPage extends MifosPage {

	public CreateUserEnterDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CreateUserEnterDataPage(Selenium selenium) {
		super(selenium);
	}
	
    public void setFirstName(String firstName) {
        selenium.type("create_user.input.firstName", firstName);
    }
    
    public void setLastName(String lastName) {
        selenium.type("create_user.input.lastName", lastName);
    }
    
    public void setEmail(String email) {
        selenium.type("create_user.input.email", email);
    }
    
    public void setMaritalStatus(String maritalStatus) {
        selenium.select("maritalStatus", "label=" + maritalStatus);
    }    
    
    public void setDateOfBirth(String dateOfBirthDD, String dateOfBirthMM, String dateOfBirthYYYY) {
        selenium.type("dobDD", dateOfBirthDD);
        selenium.type("dobMM", dateOfBirthMM);
        selenium.type("dobYY", dateOfBirthYYYY);        
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
    
    public void setRoles(String roles) {
        selenium.click("MoveRight");
    }
    
    public void setUserName(String userName) {
        selenium.type("create_user.input.userName", userName);
    }
    
    public void setPassword(String password) {
        selenium.type("create_user.input.password", password);
    }
    
    public void setPasswordRepeat(String passwordRepeat) {
        selenium.type("create_user.input.passwordRepeat", passwordRepeat);
    }    

    public CreateUserPreviewDataPage preview() {
        selenium.click("create_user.button.preview");
        waitForPageToLoad();
        return new CreateUserPreviewDataPage(selenium);
    }
}
