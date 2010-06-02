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

package org.mifos.test.acceptance.framework.holiday;

import junit.framework.Assert;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;


public class CreateHolidayEntryPage extends MifosPage {
    public void verifyPage() {
        this.verifyPage("create_officeHoliday");
        // TODO KRP: uncomment this when acceptance tests are running locally
        this.verifyRepaymentRuleOptions();
    }

    public CreateHolidayEntryPage(Selenium selenium) {
        super(selenium);
    }

    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class CreateHolidaySubmitParameters {
        public static final String NEXT_WORKING_DAY = "Next Working Day";
        public static final String SAME_DAY = "Same Day";
        public static final String NEXT_MEETING_OR_REPAYMENT = "Next Meeting/Repayment";
        public static final String MORATORIUM = "Moratorium";

        private String name;
        private String fromDateDD;
        private String fromDateMM;
        private String fromDateYYYY;
        private String thruDateDD;
        private String thruDateMM;
        private String thruDateYYYY;
        private String repaymentRule;
        private String selectedOfficeIds;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFromDateDD() {
            return this.fromDateDD;
        }

        public void setFromDateDD(String fromDateDD) {
            this.fromDateDD = fromDateDD;
        }

        public String getFromDateMM() {
            return this.fromDateMM;
        }

        public void setFromDateMM(String fromDateMM) {
            this.fromDateMM = fromDateMM;
        }

        public String getFromDateYYYY() {
            return this.fromDateYYYY;
        }

        public void setFromDateYYYY(String fromDateYYYY) {
            this.fromDateYYYY = fromDateYYYY;
        }

        public String getThruDateDD() {
            return this.thruDateDD;
        }

        public void setThruDateDD(String thruDateDD) {
            this.thruDateDD = thruDateDD;
        }

        public String getThruDateMM() {
            return this.thruDateMM;
        }

        public void setThruDateMM(String thruDateMM) {
            this.thruDateMM = thruDateMM;
        }

        public String getThruDateYYYY() {
            return this.thruDateYYYY;
        }

        public void setThruDateYYYY(String thruDateYYYY) {
            this.thruDateYYYY = thruDateYYYY;
        }

        public String getRepaymentRule() {
            return this.repaymentRule;
        }

        public void setRepaymentRule(String repaymentRule) {
            this.repaymentRule = repaymentRule;
        }

        @SuppressWarnings("PMD.OnlyOneReturn")
        public int getRepaymentRuleValue() {
            if (SAME_DAY.equals(repaymentRule)) { return 1; }
            if (NEXT_MEETING_OR_REPAYMENT.equals(repaymentRule)) { return 2; }
            if (NEXT_WORKING_DAY.equals(repaymentRule)) { return 3; }
            if (MORATORIUM.equals(repaymentRule)) { return 4; }

            return -1;
        }

        public void setSelectedOfficeIds(String selectedOfficeIds) {
            this.selectedOfficeIds = selectedOfficeIds;
        }

        public String getSelectedOfficeIds() {
            return selectedOfficeIds;
        }

    }

    public CreateHolidayConfirmationPage submitAndNavigateToHolidayConfirmationPage(CreateHolidaySubmitParameters formParameters) {
        selenium.type("holiday.input.name",formParameters.getName());
        selenium.type("holidayFromDateDD", formParameters.getFromDateDD());
        selenium.type("holidayFromDateMM", formParameters.getFromDateMM());
        selenium.type("holidayFromDateYY", formParameters.getFromDateYYYY());
        selenium.type("selectedOfficeIds", formParameters.getSelectedOfficeIds());
        selenium.getEval("window.CreateHoliday.setState();");
        this.typeTextIfNotEmpty("holidayThruDateDD", formParameters.getThruDateDD());
        this.typeTextIfNotEmpty("holidayThruDateMM", formParameters.getThruDateMM());
        this.typeTextIfNotEmpty("holidayThruDateYY", formParameters.getThruDateYYYY());
        selenium.select("holiday.input.repaymentrule", "value=" + formParameters.getRepaymentRuleValue());

        selenium.fireEvent("holidayFromDateYY", "blur");
        selenium.fireEvent("holidayThruDateYY", "blur");


        selenium.click("holiday.button.preview");
        waitForPageToLoad();


        return new CreateHolidayConfirmationPage(selenium);

    }

    @SuppressWarnings("PMD.StringToString")
    private void verifyRepaymentRuleOptions() {
        String[] options = selenium.getSelectOptions("name=repaymentRuleId");
        Assert.assertEquals("Wrong number of repayment rule options", 5, options.length);
        Assert.assertEquals(CreateHolidaySubmitParameters.SAME_DAY, options[1]);
        Assert.assertEquals(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT, options[2]);
        Assert.assertEquals(CreateHolidaySubmitParameters.NEXT_WORKING_DAY, options[3]);
        Assert.assertEquals(CreateHolidaySubmitParameters.MORATORIUM, options[4]);
    }
}
