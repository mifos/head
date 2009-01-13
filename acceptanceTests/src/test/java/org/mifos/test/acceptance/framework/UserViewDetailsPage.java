/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class UserViewDetailsPage extends MifosPage {

    public UserViewDetailsPage(Selenium selenium) {
        super(selenium);
    }

    public EditUserDataPage navigateToEditUserDataPage() {
        selenium.click("personneldetails.link.editUser");
        waitForPageToLoad();
        return new EditUserDataPage(selenium);
    }

    public String getFullName() {
        return selenium.getText("personneldetails.text.fullName");
    }
    
    public String getEmail() {
        return selenium.getText("personneldetails.text.email");
    }    
    
    public String getStatus() {
        return selenium.getText("personneldetails.text.status");
    }    
}