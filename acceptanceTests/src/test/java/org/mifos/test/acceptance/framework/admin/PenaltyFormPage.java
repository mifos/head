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

import java.lang.reflect.InvocationTargetException;

import org.mifos.test.acceptance.framework.MifosPage;
import org.springframework.util.StringUtils;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class PenaltyFormPage extends MifosPage {

    public PenaltyFormPage(final Selenium selenium) {
        super(selenium);
    }
    
    @SuppressWarnings("PMD.NPathComplexity")
    public PenaltyFormPage fillParameters(final PenaltyFormParameters parameters) {
        selenium.type("name", parameters.getName());

        if (selenium.isElementPresent("categoryTypeId") && selenium.isVisible("categoryTypeId")) {
            selectIfNotEmpty("categoryTypeId", parameters.getApplies());
        }

        selectIfNotEmpty("periodTypeId", parameters.getPeriod());
        selenium.type("duration", parameters.getDuration());
        selenium.type("min", parameters.getMin());
        selenium.type("max", parameters.getMax());

        if (selenium.isElementPresent("amount")) {
            selenium.type("amount", parameters.getAmount());
        }

        if (selenium.isElementPresent("rate")) {
            selenium.type("rate", "");

            if (StringUtils.hasText(parameters.getRate())) {
                selenium.type("rate", parameters.getRate());
            }
        }

        if (selenium.isElementPresent("formulaId") && selenium.isVisible("formulaId")
                && StringUtils.hasText(parameters.getFormula())) {
            selectIfNotEmpty("formulaId", parameters.getFormula());
        }

        selectIfNotEmpty("frequencyId", parameters.getFrequency());
        selectIfNotEmpty("glCodeId", parameters.getGlCode());

        if (selenium.isElementPresent("statusId") && selenium.isVisible("statusId")) {
            selectIfNotEmpty("statusId", parameters.getStatus());
        }

        return this;
    }

    public void verifyErrors(final String[] errors) {
        for (String error : errors) {
            Assert.assertTrue(selenium.isTextPresent(error), "Not found error: " + error);
        }
    }

    public PenaltyFormPage submitPageToDisplayErrors() {
        selenium.click("preview");
        waitForPageToLoad();
        return this;
    }

    public <T extends MifosPage> T submitPageAndGotoPenaltyPreviewPage(final Class<T> type) throws IllegalArgumentException,
            SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        selenium.click("preview");
        waitForPageToLoad();
        return type.getDeclaredConstructor(Selenium.class).newInstance(selenium);
    }

}