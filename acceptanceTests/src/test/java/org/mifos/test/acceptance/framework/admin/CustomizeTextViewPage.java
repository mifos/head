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

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CustomizeTextViewPage extends MifosPage {

    public CustomizeTextViewPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("customizeTextView");
    }

    public void verifyLabelValue(String label, String value) {
        Assert.assertEquals(selenium.getValue(label), value);
    }

	public AdminPage done() {
        selenium.click("customizeTextView.button.done");
        waitForPageToLoad();
        return new AdminPage(selenium);
	}

	public CustomizeTextAddPage navigateToCustomizeTextAddPage() {
        selenium.click("customizeTextView.button.add");
        waitForPageToLoad();
        return new CustomizeTextAddPage(selenium);
	}

	public AdminPage navigateToCustomizeTextEditPage() {
        selenium.click("customizeTextView.button.edit");
        waitForPageToLoad();
        return new AdminPage(selenium);
	}
	
	public CustomizeTextViewPage removeCustomizedText(String originalText) {
        selenium.select("customizeTextView.select.customizedText", "value="+originalText);
		
        selenium.click("customizeTextView.button.remove");
        waitForPageToLoad();
        return this;
	}
	
	public void verifyCustomTextIsPresent(String originalText, String customText) {
		String customizedTextLabel = originalText + " > " + customText;
		String[] options = selenium.getSelectOptions("customizeTextView.select.customizedText");
		for (String option : options) {
			if (option.equals(customizedTextLabel)) {
				// found what we're looking for so we're done
				return;
			}
		}
		Assert.fail("Could not fine CustomizedText: " + customizedTextLabel);		
	}
	
	public int getCustomizedTextCount() {
		String[] options =  selenium.getSelectOptions("customizeTextView.select.customizedText");
		if (options.length == 1) {
			if (options[0].length() == 0) {
				return 0;
			}
		}
		return options.length;
	}
}