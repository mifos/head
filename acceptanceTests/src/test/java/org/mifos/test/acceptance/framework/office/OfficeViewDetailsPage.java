/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.test.acceptance.framework.office;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.admin.AdminPage;

import com.thoughtworks.selenium.Selenium;

public class OfficeViewDetailsPage extends MifosPage {

    public OfficeViewDetailsPage(Selenium selenium) {
        super(selenium);
    }
    
    public void verifyPage() {
        verifyPage("viewOfficeDetails");
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
