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

package org.mifos.test.acceptance.framework.user;

import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class EditUserDataPage extends MifosPage {

    private static final String LOCATOR_FORM_STATUS = "name=status";

    public static final String STATUS_INACTIVE = "Inactive";

    public EditUserDataPage() {
        super();
    }

    /**
     * @param selenium
     */
    public EditUserDataPage(Selenium selenium) {
        super(selenium);
    }

    private EditUserPreviewDataPage submit() {
        selenium.click("edit_user.button.preview");
        waitForPageToLoad();
        return new EditUserPreviewDataPage(selenium);
    }

    private void fillForm(CreateUserParameters parameters) {
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
        
        //we can add or remove 'Admin' role
        if (parameters.getRole() != null) {
        	if(parameters.getRole().equals("Admin")) {
        		selenium.select("name=id", "label=Admin");
        		selenium.click("rolelist.button.add");
        	}
        	else if (parameters.getRole().equals("")) {
        		selenium.select("name=personnelRoles", "label=Admin");
        		selenium.click("rolelist.button.remove");
        	}
        }
    }

    public EditUserPreviewDataPage submitAndGotoEditUserPreviewDataPage(CreateUserParameters parameters) {
        fillForm(parameters);
        return submit();
    }

    public EditUserDataPage submitWithInvalidData(CreateUserParameters parameters) {
        fillForm(parameters);
        selenium.click("edit_user.button.preview");
        waitForPageToLoad();
        return new EditUserDataPage(selenium);
    }

    public UserViewDetailsPage changeStatusAndSubmit(String status) {
        selenium.select(LOCATOR_FORM_STATUS, status);
        return submit().submit();
    }

    public void verifyPasswordChangeError() {
        String errorMsg = selenium.getText("edit_user.error.message");
        Assert.assertTrue(errorMsg.contains("Please ensure that password and confirm password entries are made and they are identical."));
    }

}
