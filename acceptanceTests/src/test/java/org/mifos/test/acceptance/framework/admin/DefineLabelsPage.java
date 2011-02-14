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

package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class DefineLabelsPage extends MifosPage {

    public DefineLabelsPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("definelabels");
    }

    public void setLabelValue(String label, String value) {
        selenium.type("definelabels.input." + label, value);
    }

    public String getCitizenshipLabel() {
        return selenium.getText("defineLabels.input.citizenship");
    }

    public String getGovtIdLabel() {
        return selenium.getText("defineLabels.input.govtId");
    }

    public void verifyLabelValue(String label, String value) {
        Assert.assertEquals(selenium.getValue(label), value);
    }

    public void verifyCitizenshipLabel(DefineLabelsParameters labelParameters) {
        Assert.assertEquals(getCitizenshipLabel(), labelParameters.getCitizenship());
    }

    public void verifyGovtIdLabel(DefineLabelsParameters labelParameters) {
        Assert.assertEquals(getGovtIdLabel(), labelParameters.getGovtId());
    }

    public AdminPage submit() {
        selenium.click("definelabels.button.submit");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }

}