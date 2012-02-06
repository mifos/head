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
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.util.StringUtils;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class EditPenaltyPreviewPage extends MifosPage {
    private static final String XPATH_DATA_FORMAT = "//form/div/div[2]/div[%d]/div[%d]";

    public EditPenaltyPreviewPage(final Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("penaltyPreview");
    }

    public void verifyData(final PenaltyFormParameters parameters) {
        Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 3, 1)),
                "Penalty Name: " + parameters.getName());
        Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 3, 2)),
                "Grace Period Type: " + parameters.getPeriod());

        if (StringUtils.hasText(parameters.getDuration())) {
            Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 3, 3)), "Grace Period Duration: "
                    + StringUtil.formatNumber(parameters.getDuration()));
            Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 3, 4)),
                    "Cumulative Penalty Limit (Minimum): " + StringUtil.formatNumber(parameters.getMin()));
            Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 3, 5)),
                    "Cumulative Penalty Limit (Maximum): " + StringUtil.formatNumber(parameters.getMax()));
        } else {
            Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 3, 3)),
                    "Cumulative Penalty Limit (Minimum): " + StringUtil.formatNumber(parameters.getMin()));
            Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 3, 4)),
                    "Cumulative Penalty Limit (Maximum): " + StringUtil.formatNumber(parameters.getMax()));
        }

        if (StringUtils.hasText(parameters.getAmount())) {
            Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 5, 1)),
                    "Amount: " + StringUtil.formatNumber(parameters.getAmount()));
        }

        if (StringUtils.hasText(parameters.getRate())) {
            Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 5, 1)), "Calculate Penalty As: "
                    + StringUtil.formatNumber(parameters.getRate()) + " % of " + parameters.getFormula());
        }

        Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 5, 2)), "Penalty Application Frequency: "
                + parameters.getFrequency());

        Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 7, 1)),
                "GL Code: " + parameters.getGlCode());
        Assert.assertEquals(selenium.getText(String.format(XPATH_DATA_FORMAT, 7, 2)), "Status: " + parameters.getStatus());
    }

    public PenaltyFormPage navigateToEditPenaltyInformationPage() {
        selenium.click("EDIT");
        waitForPageToLoad();
        return new PenaltyFormPage(selenium, "editPenalty");
    }

    public ViewPenaltyPage submit() {
        selenium.click("SUBMIT");
        waitForPageToLoad();
        return new ViewPenaltyPage(selenium);
    }
}
