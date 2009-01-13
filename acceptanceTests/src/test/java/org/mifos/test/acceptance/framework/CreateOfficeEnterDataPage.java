/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class CreateOfficeEnterDataPage extends MifosPage {

	public CreateOfficeEnterDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CreateOfficeEnterDataPage(Selenium selenium) {
		super(selenium);
	}
    
    public void setOfficeName(String officeName) {
        selenium.type("CreateNewOffice.input.officeName", officeName);
    }
    
    public void setShortName(String shortName) {
        selenium.type("CreateNewOffice.input.shortName", shortName);
    }
    
    public void setOfficeType(String officeType) {
        selenium.select("officeLevel", officeType);
        waitForPageToLoad();
    }
    
    public void setParentOffice(String parentOffice) {
        selenium.select("parentOfficeId", parentOffice);
    }
    
    public void setAddress1(String address1) {
        selenium.type("CreateNewOffice.input.address1", address1);
    }
    
    public void setAddress3(String address3) {
        selenium.type("CreateNewOffice.input.address3", address3);
    }
    
    public void setState(String state) {
        selenium.type("CreateNewOffice.input.state", state);
    }

    public void setCountry(String country) {
        selenium.type("CreateNewOffice.input.country", country);
    }
    
    public void setPostalCode(String postalCode) {
        selenium.type("CreateNewOffice.input.postalCode", postalCode);
    }
    
    public void setPhoneNumber(String phoneNumber) {
        selenium.type("CreateNewOffice.input.phoneNumber", phoneNumber);
    }
    
    public CreateOfficePreviewDataPage preview() {
        selenium.click("CreateNewOffice.button.preview");
        waitForPageToLoad();
        return new CreateOfficePreviewDataPage(selenium);
    }
}
