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
 
package org.mifos.test.acceptance.framework.user;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class EditUserDataPage extends MifosPage {

	public EditUserDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public EditUserDataPage(Selenium selenium) {
		super(selenium);
	}

    public EditUserPreviewDataPage submitAndGotoEditUserPreviewDataPage(CreateUserParameters parameters) {
        typeTextIfNotEmpty("edit_user.input.firstName", parameters.getFirstName());
        typeTextIfNotEmpty("edit_user.input.lastName", parameters.getLastName());
        typeTextIfNotEmpty("edit_user.input.email", parameters.getEmail());
        typeTextIfNotEmpty("dobDD", parameters.getDateOfBirthDD());
        typeTextIfNotEmpty("dobMM", parameters.getDateOfBirthMM());
        typeTextIfNotEmpty("dobYY", parameters.getDateOfBirthYYYY());        
        selectIfNotEmpty("maritalStatus", parameters.getMaritalStatus());
        selectValueIfNotZero("gender", parameters.getGender());
        selectValueIfNotZero("preferredLocale", parameters.getPreferredLanguage());
        selectValueIfNotZero("level", parameters.getUserLevel());
        typeTextIfNotEmpty("edit_user.input.userPassword", parameters.getPassword());
        typeTextIfNotEmpty("edit_user.input.passwordRepeat", parameters.getPasswordRepeat());
        selenium.click("edit_user.button.preview");
        waitForPageToLoad();
        return new EditUserPreviewDataPage(selenium);
    }
}
