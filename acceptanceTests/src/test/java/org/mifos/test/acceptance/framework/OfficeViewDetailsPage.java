/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class OfficeViewDetailsPage extends MifosPage {

    public OfficeViewDetailsPage(Selenium selenium) {
        super(selenium);
    }
    
    public String getOfficeName() {
        return selenium.getText("viewOfficeDetails.text.officeName");
    }

    public String getShortName() {
        return selenium.getText("viewOfficeDetails.text.shortName");
    }    
    
    public String getOfficeType() {
        return selenium.getText("viewOfficeDetails.text.officeLevel");
    }
    
    public AdminPage navigateToAdminPage() {
        selenium.click("viewOfficeDetails.link.admin");
        waitForPageToLoad();
        return new AdminPage(selenium);     
    }    
        
}
