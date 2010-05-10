/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
