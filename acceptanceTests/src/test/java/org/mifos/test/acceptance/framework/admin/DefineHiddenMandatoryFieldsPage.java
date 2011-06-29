/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.test.acceptance.framework.admin;

import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class DefineHiddenMandatoryFieldsPage extends MifosPage {

    public DefineHiddenMandatoryFieldsPage(Selenium selenium) {
        super(selenium);
    }

    public void checkMandatoryEthnicity(){
        selenium.check("mandatorySystemEthnicity");
    }
    
    public void uncheckMandatoryEthnicity(){
        selenium.uncheck("mandatorySystemEthnicity");
    }
    
    public void checkHideEthnicity(){
        selenium.check("hideSystemEthnicity");
    }
    
    public void uncheckHideEthnicity(){
        selenium.uncheck("hideSystemEthnicity");
    }

    public void checkMandatoryLoanAccountPurpose(){
        selenium.check("mandatoryLoanAccountPurpose");
    }
    
    public void uncheckMandatoryLoanAccountPurpose(){
        selenium.uncheck("mandatoryLoanAccountPurpose");
    }
    
    public void checkMandatoryExternalId(){
        selenium.check("mandatorySystemExternalId");
    }
    
    public void uncheckMandatoryExternalId(){
        selenium.uncheck("mandatorySystemExternalId");
    }
    
    public void checkHideExternalId(){
        selenium.check("hideSystemExternalId");
    }
    
    public void uncheckHideExternalId(){
        selenium.uncheck("hideSystemExternalId");
    }
    
    public void checkHideCitizenShip(){
        selenium.check("hideSystemCitizenShip");
    }
    
    public void uncheckHideCitizenShip(){
        selenium.uncheck("hideSystemCitizenShip");
    }
    
    public void checkMandatoryCitizenShip(){
        selenium.check("mandatorySystemCitizenShip");
    }
    
    public void uncheckMandatoryCitizenShip(){
        selenium.uncheck("mandatorySystemCitizenShip");
    }
    
    public void checkHideHandicapped(){
        selenium.check("hideSystemHandicapped");
    }
    
    public void uncheckHideHandicapped(){
        selenium.uncheck("hideSystemHandicapped");
    }
    
    public void checkMandatoryHandicapped(){
        selenium.check("mandatorySystemHandicapped");
    }
    
    public void uncheckMandatoryHandicapped(){
        selenium.uncheck("mandatorySystemHandicapped");
    }
    
    public void checkHideEducationLevel(){
        selenium.check("hideSystemEducationLevel");
    }
    
    public void uncheckHideEducationLevel(){
        selenium.uncheck("hideSystemEducationLevel");
    }
    
    public void checkMandatoryEducationLevel(){
        selenium.check("mandatorySystemEducationLevel");
    }
    
    public void uncheckMandatoryEducationLevel(){
        selenium.uncheck("mandatorySystemEducationLevel");
    }
    
    public void checkHidePhoto(){
        selenium.check("hideSystemPhoto");
    }
    
    public void uncheckHidePhoto(){
        selenium.uncheck("hideSystemPhoto");
    }
    
    public void checkMandatoryPhoto(){
        selenium.check("mandatorySystemPhoto");
    }
    
    public void uncheckMandatoryPhoto(){
        selenium.uncheck("mandatorySystemPhoto");
    }
    
    public void checkHideAssignClientPostions(){
        selenium.check("hideSystemAssignClientPostions");
    }
    
    public void uncheckHideAssignClientPostions(){
        selenium.uncheck("hideSystemAssignClientPostions");
    }
    
    public void checkMandatoryAddress1(){
        selenium.check("mandatorySystemAddress1");
    }
    
    public void uncheckMandatoryAddress1(){
        selenium.uncheck("mandatorySystemAddress1");
    }
    
    public void checkHideAddress2(){
        selenium.check("hideSystemAddress2");
    }
    
    public void uncheckHideAddress2(){
        selenium.uncheck("hideSystemAddress2");
    }
    
    public void checkHideAddress3(){
        selenium.check("hideSystemAddress3");
    }
    
    public void uncheckHideAddress3(){
        selenium.uncheck("hideSystemAddress3");
    }
    
    public void checkHideCity(){
        selenium.check("hideSystemCity");
    }
    
    public void uncheckHideCity(){
        selenium.uncheck("hideSystemCity");
    }
    
    public void checkHideState(){
        selenium.check("hideSystemState");
    }
    
    public void uncheckHideState(){
        selenium.uncheck("hideSystemState");
    }
    
    public void checkHideCountry(){
        selenium.check("hideSystemCountry");
    }
    
    public void uncheckHideCountry(){
        selenium.uncheck("hideSystemCountry");
    }
    
    public void checkHidePostalCode(){
        selenium.check("hideSystemPostalCode");
    }
    
    public void uncheckHidePostalCode(){
        selenium.uncheck("hideSystemPostalCode");
    }
    
    public void checkHideReceiptIdDate(){
        selenium.check("hideSystemReceiptIdDate");
    }
    
    public void uncheckHideReceiptIdDate(){
        selenium.uncheck("hideSystemReceiptIdDate");
    }
    
    public void checkHideCollateralTypeNotes(){
        selenium.check("hideSystemCollateralTypeNotes");
    }
    
    public void uncheckHideCollateralTypeNotes(){
        selenium.uncheck("hideSystemCollateralTypeNotes");
    }
    
    public void checkHideMiddleName(){
        selenium.check("hideClientMiddleName");
    }
    
    public void uncheckHideMiddleName(){
        selenium.uncheck("hideClientMiddleName");
    }
    
    public void checkMandatoryMiddleName(){
        selenium.check("mandatoryClientMiddleName");
    }
    
    public void uncheckMandatoryMiddleName(){
        selenium.uncheck("mandatoryClientMiddleName");
    }
    
    public void checkHideGovtId(){
        selenium.check("hideClientGovtId");
    }
    
    public void uncheckHideGovtId(){
        selenium.uncheck("hideClientGovtId");
    }
    
    public void checkMandatoryGovtId(){
        selenium.check("mandatoryClientGovtId");
    }
    
    public void uncheckMandatoryGovtId(){
        selenium.uncheck("mandatoryClientGovtId");
    }
    
    public void checkHideRelativeSecondLastName(){
        selenium.check("hideClientSpouseFatherSecondLastName");
    }

    public void uncheckHideRelativeSecondLastName(){
        selenium.uncheck("hideClientSpouseFatherSecondLastName");
    }
    
    public void checkMandatoryRelativeSecondLastName(){
        selenium.check("mandatoryClientSpouseFatherSecondLastName");
    }

    public void uncheckMandatoryRelativeSecondLastName(){
        selenium.uncheck("mandatoryClientSpouseFatherSecondLastName");
    }
    
    public void checkHideSecondLastName(){
        selenium.check("hideClientSecondLastName");
    }

    public void uncheckHideSecondLastName(){
        selenium.uncheck("hideClientSecondLastName");
    }
    
    public void checkMandatorySecondLastName(){
        selenium.check("mandatoryClientSecondLastName");
    }

    public void uncheckMandatorySecondLastName(){
        selenium.uncheck("mandatoryClientSecondLastName");
    }
    
    public void checkMandatoryMaritalStatus(){
        selenium.check("mandatoryMaritalStatus");
    }

    public void uncheckMandatoryMaritalStatus(){
        selenium.uncheck("mandatoryMaritalStatus");
    }
    
    public void checkHidePovertyStatus(){
        selenium.click("hideClientPovertyStatus");
    }

    public void uncheckHidePovertyStatus(){
        selenium.click("hideClientPovertyStatus");
    }
    
    public void checkMandatoryPovertyStatus(){
        selenium.click("mandatoryClientPovertyStatus");
    }

    public void uncheckMandatoryPovertyStatus(){
        selenium.click("mandatoryClientPovertyStatus");
    }
    
    public void checkMandatoryFamilyDetails(){
        selenium.check("mandatoryClienFamilyDetails");
    }

    public void uncheckMandatoryFamilyDetails(){
        selenium.uncheck("mandatoryClientFamilyDetails");
    }
    
    public void checkHideRelativeMiddleName (){
        selenium.check("hideClientSpouseFatherMiddleName");
    }

    public void uncheckHideRelativeMiddleName (){
        selenium.uncheck("hideClientSpouseFatherMiddleName");
    }
    
    public void checkHidePhone(){
        selenium.check("hideClientPhone");
    }

    public void uncheckHidePhone(){
        selenium.uncheck("hideClientPhone");
    }
    
    public void checkMandatoryPhone(){
        selenium.check("mandatoryClientPhone");
    }

    public void uncheckMandatoryPhone(){
        selenium.uncheck("mandatoryClientPhone");
    }
    
    public void checkHideTrained(){
        selenium.check("hideClientTrained");
    }

    public void uncheckHideTrained(){
        selenium.uncheck("hideClientTrained");
    }
    
    public void checkMandatoryTrained(){
        selenium.check("mandatoryClientTrained");
    }

    public void uncheckMandatoryTrained(){
        selenium.uncheck("mandatoryClientTrained");
    }
    
    public void checkMandatoryTrainedOn(){
        selenium.check("mandatoryClientTrainedOn");
    }

    public void uncheckMandatoryTrainedOn(){
        selenium.uncheck("mandatoryClientTrainedOn");
    }
    
    public void checkHideBusinessWorkActivities(){
        selenium.check("hideClientBusinessWorkActivities");
    }

    public void uncheckHideBusinessWorkActivities(){
        selenium.uncheck("hideClientBusinessWorkActivities");
    }
    
    public void checkMandatoryNumberOfChildren(){
        selenium.check("mandatoryNumberOfChildren");
    }

    public void uncheckMandatoryNumberOfChildren(){
        selenium.uncheck("mandatoryNumberOfChildren");
    }
    
    public void checkHideGroupTrained(){
        selenium.check("hideGroupTrained");
    }

    public void uncheckHideGroupTrained(){
        selenium.uncheck("hideGroupTrained");
    }
    
    public void checkMandatoryLoanSourceOfFund(){
        selenium.check("mandatoryLoanSourceOfFund");
    }

    public void uncheckMandatoryLoanSourceOfFund(){
        selenium.uncheck("mandatoryLoanSourceOfFund");
    } 
    
    public void verifyPage(){
        verifyPage("defineMandatoryHiddenFields");
    }
    
    public void verifyAccessDenied(){
        Assert.assertEquals("Access Denied", selenium.getText("accessDeniedHeading") );
        Assert.assertEquals("You are not allowed to access this page.", selenium.getText("accessDeniedMessage"));
    }

    public AdminPage submit(){
        selenium.click("name=SUBMIT");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }
}
