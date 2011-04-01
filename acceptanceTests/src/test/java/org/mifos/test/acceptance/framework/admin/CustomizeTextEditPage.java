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

import static org.testng.Assert.assertEquals;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CustomizeTextEditPage extends MifosPage {

    public CustomizeTextEditPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("customizeTextEdit");
    }

	public CustomizeTextViewPage submit() {
        selenium.click("customizeTextEdit.button.submit");
        waitForPageToLoad();
        return new CustomizeTextViewPage(selenium);
	}

	public CustomizeTextViewPage cancel() {
        selenium.click("customizeTextEdit.button.cancel");
        waitForPageToLoad();
        return new CustomizeTextViewPage(selenium);
	}

	public void setCustomText(String customText) {
        selenium.type("customizeTextEdit.input.customText", customText);
	}	
	
	public String originalTextInputStatus() {
        return selenium.getEval("window.document.getElementById('customizeTextEdit.input.originalText').disabled");
    }
	
	public void verifyOriginalTextInput() {
	    assertEquals("true", originalTextInputStatus());
	}
	
	public CustomizeTextEditPage trySubmit() {
        selenium.click("customizeTextEdit.button.submit");
        waitForPageToLoad();
        return this;
    }
    
    public void verifyTextPresent(String expectedText, String errorMessage) {
        Assert.assertTrue(selenium.isTextPresent(expectedText), errorMessage);
    }
}