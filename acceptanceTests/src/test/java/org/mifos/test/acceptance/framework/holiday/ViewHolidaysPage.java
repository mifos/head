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

package org.mifos.test.acceptance.framework.holiday;

import java.util.Arrays;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage.CreateHolidaySubmitParameters;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;


public class ViewHolidaysPage extends MifosPage {

    public ViewHolidaysPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("view_organizational_holidays");
    }

    public CreateHolidayEntryPage navigateToDefineHolidayPage() {
        selenium.click("holiday.link.defineNewHoliday");
        waitForPageToLoad();
        return new CreateHolidayEntryPage(selenium);
    }


    private String getHolidayName(String year, Integer row){
        return selenium.getTable("organizational_holidays_" + year + "." + row +".2");
    }

    private String getFromDate(String year, Integer row){
        return selenium.getTable("organizational_holidays_" + year + "." + row +".0");
    }
    private String getToDate(String year, Integer row){
        return selenium.getTable("organizational_holidays_" + year + "." + row +".1");
    }

    private String getRepaymentRule(String year, Integer row){
        return selenium.getTable("organizational_holidays_" + year + "." + row +".3");
    }

    private String getAppliesTo(String year, Integer row){
        return selenium.getTable("organizational_holidays_" + year + "." + row +".4");
    }


    public void verifyHolidayName(String holidayName, String year, Integer row){
        Assert.assertEquals(getHolidayName(year, row), holidayName);
    }

    public void verifyFromDate(String fromDate, String year, Integer row){
        Assert.assertEquals(getFromDate(year, row), fromDate);
    }

    public void verifyToDate(String toDate, String year, Integer row){
        Assert.assertEquals(getToDate(year, row), toDate);
    }

    public void verifyRepaymentRule(String repaymentRule, String year, Integer row){
        Assert.assertEquals(getRepaymentRule(year, row), repaymentRule);
    }

    public void verifyAppliesTo(String appliesTo, String year, Integer row){
        Assert.assertTrue(Arrays.asList(getAppliesTo(year, row).split(", ")).contains(appliesTo));
    }


    public void verifyHolidayCreationResault(CreateHolidaySubmitParameters formParameters){
        String year;
        if(formParameters.getThruDateYYYY()==null){
            year=formParameters.getFromDateYYYY();
        }
        else{
            year=formParameters.getThruDateYYYY();
        }
        Integer rowCount = selenium.getXpathCount("//table[@id='organizational_holidays_" + year + "']/tbody/tr").intValue();
        boolean holidayFound = false;
        for(int i = 1; i < rowCount; i++){
            if(formParameters.getName().equals(getHolidayName(year, i))){
                holidayFound = true;
                verifyFromDate(formParameters.getFromDate(), year, i);
                verifyToDate(formParameters.getThruDate(), year, i);
                verifyRepaymentRule(formParameters.getRepaymentRule(), year, i);
                Assert.assertTrue(getAppliesTo(year, i).split(", ").length==formParameters.getSelectedOfficeNames().size());
                for (String officeName : formParameters.getSelectedOfficeNames()) {
                    verifyAppliesTo(officeName, year, i);
                }
            }
        }
        Assert.assertTrue(holidayFound);
    }
}
