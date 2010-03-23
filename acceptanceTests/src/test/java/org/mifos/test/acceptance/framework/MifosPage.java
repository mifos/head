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

package org.mifos.test.acceptance.framework;

import org.mifos.test.acceptance.framework.login.LoginPage;

import com.thoughtworks.selenium.Selenium;

/**
 * Responsible for common actions that are executable by a logged-in user.
 *
 */
public class MifosPage extends AbstractPage {

    public MifosPage() {
        super();
    }

    public MifosPage(Selenium selenium) {
        super(selenium);
    }

    public LoginPage logout() {
        selenium.open("loginAction.do?method=logout");
        waitForPageToLoad();
        return new LoginPage(selenium);
    }

    protected void typeTextIfNotEmpty(String locator, String value) {
        if (!isEmpty(value)) {
            selenium.type(locator, value);
        }
    }

    protected void selectIfNotEmpty(String locator, String value) {
        if (!isEmpty(value)) {
            selenium.select(locator, "label=" + value);
        }
    }

    /**
     * This is the equivalent of seleftIfNotEmpty for integers.
     * @see #selectIfNotEmpty(String, String)
     * @param locator
     * @param value
     */
    protected void selectValueIfNotZero(String locator, int value) {
        if (value != 0) {
            selenium.select(locator, "value=" + value);
        }
    }

    protected void checkIfNotEmpty(String locator, String value) {
        if (!isEmpty(value)) {
            selenium.check(locator);
        }
    }

    protected void selectAndWaitIfNotEmpty(String locator, String value) {
        if (!isEmpty(value)) {
            selenium.select(locator, "label=" + value);
            waitForPageToLoad();
        }
    }

    private boolean isEmpty(String value) {
        boolean empty = true;
        if (value!= null && !value.isEmpty()) {
            empty = false;
        }
        return empty;
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    public boolean isErrorMessageDisplayed() {
        // the error message span id is always <page_id>.error.message so
        // using a wildcard we check to see if that span has text or not.
        // wildcards are not supported in id tags so we use a css selector.

        String errorMessage = selenium.getText("css=span[id*=\"error.message\"]");
        if (isEmpty(errorMessage)) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    public boolean isCollectionSheetAccountErrorMessageDisplayed(final String mainStr, final String detailStr) {

        String bodyText = selenium.getBodyText();
        Integer positionOfMainStrInBodyText = bodyText.indexOf(mainStr);

        if (positionOfMainStrInBodyText == -1) {
            return false;
        }
        if ((detailStr != null) && (bodyText.indexOf(detailStr) <= positionOfMainStrInBodyText)) {
            return false;
        }
        return true;
    }
}
