package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.MifosPage;
import com.thoughtworks.selenium.Selenium;

public class ClientEditFamilyPage extends MifosPage {
    public ClientEditFamilyPage(Selenium selenium) {
        super(selenium);    
    }
    
    public void verifyPage() {
        this.verifyPage("EditClientFamilyInfo");
    }
        
    public ClientFamilyEditPreviewPage submitAndNavigateToClientEditFamilyPreviewPage(ClientEditFamilyParameters parameters) {
        selectValueIfNotZero("familyRelationship[0]", parameters.getRelationship());
        typeTextIfNotEmpty("familyFirstName[0]", parameters.getFirstName());
        typeTextIfNotEmpty("familyLastName[0]", parameters.getLastName());      
        typeTextIfNotEmpty("familyDateOfBirthDD[0]", parameters.getDateOfBirthDD());
        typeTextIfNotEmpty("familyDateOfBirthMM[0]", parameters.getDateOfBirthMM());
        typeTextIfNotEmpty("familyDateOfBirthYY[0]", parameters.getDateOfBirthYY());     
        selectValueIfNotZero("familyGender[0]", parameters.getGender());
        selectValueIfNotZero("familyLivingStatus[0]", parameters.getLivingStatus());
        
        selenium.click("edit_ClientFamilyInfo.button.preview");
        waitForPageToLoad();
        return new ClientFamilyEditPreviewPage(selenium);
    }
}
