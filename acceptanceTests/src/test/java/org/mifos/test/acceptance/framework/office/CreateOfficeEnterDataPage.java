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

import com.thoughtworks.selenium.Selenium;

public class CreateOfficeEnterDataPage extends MifosPage {

	public CreateOfficeEnterDataPage() {
		super();
	}

    public void verifyPage() {
        verifyPage("CreateNewOffice");
    }

	/**
	 * @param selenium
	 */
	public CreateOfficeEnterDataPage(Selenium selenium) {
		super(selenium);
	}

    public CreateOfficePreviewDataPage submitAndGotoCreateOfficePreviewDataPage(OfficeParameters parameters) {
        typeTextIfNotEmpty("CreateNewOffice.input.officeName", parameters.getOfficeName());
        typeTextIfNotEmpty("CreateNewOffice.input.shortName", parameters.getShortName());
        selectValueIfNotZero("officeLevel", parameters.getOfficeTypeValue());
        waitForPageToLoad(); // loading the possible parent offices
        selectIfNotEmpty("parentOfficeId", parameters.getParentOffice());
        typeTextIfNotEmpty("CreateNewOffice.input.address1", parameters.getAddress1());
        typeTextIfNotEmpty("CreateNewOffice.input.address2", parameters.getAddress2());
        typeTextIfNotEmpty("CreateNewOffice.input.address3", parameters.getAddress3());
        typeTextIfNotEmpty("CreateNewOffice.input.state", parameters.getState());
        typeTextIfNotEmpty("CreateNewOffice.input.country", parameters.getCountry());
        typeTextIfNotEmpty("CreateNewOffice.input.postalCode", parameters.getPostalCode());
        typeTextIfNotEmpty("CreateNewOffice.input.phoneNumber", parameters.getPhoneNumber());
        selenium.click("CreateNewOffice.button.preview");
        waitForPageToLoad();
        return new CreateOfficePreviewDataPage(selenium);
    }
}
