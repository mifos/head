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

package org.mifos.test.acceptance.framework.login;

import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class ChangePasswordPage extends MifosPage {

    public ChangePasswordPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("changePassword");
    }

    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class SubmitFormParameters {

        String oldPassword;
        String newPassword;
        String confirmPassword;

        public String getOldPassword() {
            return this.oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return this.newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return this.confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }

    }

    public HomePage submitAndGotoHomePage(SubmitFormParameters parameters) {
        typeTextIfNotEmpty("changePassword.input.oldPassword", parameters.getOldPassword());
        typeTextIfNotEmpty("changePassword.input.newPassword", parameters.getNewPassword());
        typeTextIfNotEmpty("changePassword.input.confirmPassword", parameters.getConfirmPassword());

        selenium.click("changePassword.button.submit");
        waitForPageToLoad();
        return new HomePage(selenium);
    }
}