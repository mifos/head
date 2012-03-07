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

public class ViewPenaltiesPage extends MifosPage {
    public ViewPenaltiesPage(final Selenium selenium) {
        super(selenium);
    }
    
    public void verifyPage() {
        verifyPage("viewPenalties");
    }
    
    public void verifyLoanPenaltiesCount(final int count) {
        Assert.assertEquals(selenium.getXpathCount("//*[@id=\"loan.penalties\"]//a"), count);
    }
    
    public void verifySavingPenaltiesCount(final int count) {
        Assert.assertEquals(selenium.getXpathCount("//*[@id=\"saving.penalties\"]//a"), count);
    }
    
    public void verifyInActivePenaltyLabel(final int row) {
        Assert.assertEquals(selenium.getText("//div[@class=\"margin20lefttop\"]/div[" + row + "]/span[2]"), "Inactive");
    }
    
    public ViewPenaltyPage navigateToViewPenaltyPage(final String name) {
        selenium.click("link=" + name);
        waitForPageToLoad();
        return new ViewPenaltyPage(selenium);
    }
    
    public PenaltyFormPage navigateToDefineNewPenaltyPage() {
        selenium.click("define.new.penalty");
        waitForPageToLoad();
        return new PenaltyFormPage(selenium);
    }
}
