/*
 *  Copyright 2010 lukaszch.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;


public class ViewOfficeHierarchyPage extends MifosPage {
    public ViewOfficeHierarchyPage(Selenium selenium) {
        super(selenium);
    }

    public ViewOfficeHierarchyPage verifyPage() {
        verifyPage("view_office_hierarchy");
        return this;

    }

    public void verifyText(String[] expectedData) {
        for (String expectedText : expectedData) {
            Assert.assertTrue(selenium.isTextPresent(expectedText), "Expected text: " + expectedText);
        }
    }

    public void verifyHeadOfficeCheckboxChecked() {
            Assert.assertTrue(selenium.isChecked("id=headOffice"), "Checkbox 'Head Office' is not checked by default" );
    }

    public void verifyRegionalOfficeCheckboxChecked() {
            Assert.assertTrue(selenium.isChecked("id=regionalOffice"), "Checkbox 'Regional Office' is not checked by default" );
    }

    public void verifyDivisionalOfficeCheckboxChecked() {
            Assert.assertTrue(selenium.isChecked("id=subRegionalOffice"), "Checkbox 'Divisional Office' is not checked by default" );
    }

    public void verifyAreaOfficeCheckboxChecked() {
            Assert.assertTrue(selenium.isChecked("id=areaOffice"), "Checkbox 'Area Office' is not checked by default" );
    }

    public void verifyBranchOfficeCheckboxChecked() {
            Assert.assertTrue(selenium.isChecked("id=branchOffice"), "Checkbox 'Branch Office' is not checked by default" );
    }

    public void verifyHeadOfficeCheckboxDisabled() {
            Assert.assertFalse(selenium.isEditable("id=headOffice"), "Checkbox 'Head Office' is not disabled by default" );
    }

     public void verifyRegionalOfficeCheckboxEnabled() {
            Assert.assertTrue(selenium.isEditable("id=regionalOffice"), "Checkbox 'Regional Office' is not enabled by default" );
    }

    public void verifyDivisionalOfficeCheckboxEnabled() {
            Assert.assertTrue(selenium.isEditable("id=subRegionalOffice"), "Checkbox 'Divisional Office' is not enabled by default" );
    }

    public void verifyAreaOfficeCheckboxEnabled() {
            Assert.assertTrue(selenium.isEditable("id=areaOffice"), "Checkbox 'Area Office' is not enabled by default" );
    }

    public void verifyBranchOfficeCheckboxDisabled() {
            Assert.assertFalse(selenium.isEditable("id=branchOffice"), "Checkbox 'Branch Office' is not disabled by default" );
    }
}


