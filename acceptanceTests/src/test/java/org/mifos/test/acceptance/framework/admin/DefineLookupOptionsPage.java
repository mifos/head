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
import junit.framework.Assert;
import com.thoughtworks.selenium.Selenium;

public class DefineLookupOptionsPage extends MifosPage {

    private final static String ELEMENT_SELECT_SALUTATION = "salutationList";
    private final static String ELEMENT_SELECT_USER_TITLE = "userTitleList";
    private final static String ELEMENT_SELECT_MARITAL_STATUS = "maritalStatusList";
    private final static String ELEMENT_SELECT_ETHNICITY = "ethnicityList";
    private final static String ELEMENT_SELECT_EDUCATION_LEVEL = "educationLevelList";
    private final static String ELEMENT_SELECT_CITIZENSHIP = "citizenshipList";
    private final static String ELEMENT_SELECT_BUSINESS_ACTIVITY = "businessActivityList";
    private final static String ELEMENT_SELECT_PURPOSES_OF_LOAN = "purposesOfLoanList";
    private final static String ELEMENT_SELECT_HANDICAPPED = "handicappedList";
    private final static String ELEMENT_SELECT_COLLATERAL_TYPE = "collateralTypeList";
    private final static String ELEMENT_SELECT_OFFICE_TITLE = "officerTitleList";
    private final static String ELEMENT_SELECT_PAYMENT_TYPE = "paymentTypeList";

    private final String[] lokatorsByType = new String[12];

    public DefineLookupOptionsPage(Selenium selenium) {
        super(selenium);
        verifyPage("definelookupoptions");
        lokatorsByType[DefineLookupOptionParameters.TYPE_SALUTATION] = "btnAddSalutation";
        lokatorsByType[DefineLookupOptionParameters.TYPE_USER_TITLE] = "btnAddUserTitle";
        lokatorsByType[DefineLookupOptionParameters.TYPE_MARITAL_STATUS] = "btnAddMaritalStatus";
        lokatorsByType[DefineLookupOptionParameters.TYPE_ETHNICITY] = "btnAddEthnicity";
        lokatorsByType[DefineLookupOptionParameters.TYPE_EDUCATION_LEVEL] = "btnAddEducationLevel";
        lokatorsByType[DefineLookupOptionParameters.TYPE_CITIZENSHIP] = "btnAddCitizenship";
        lokatorsByType[DefineLookupOptionParameters.TYPE_BUSINESS_ACTIVITY] = "btnAddBusinessActivity";
        lokatorsByType[DefineLookupOptionParameters.TYPE_PURPOSES_OF_LOAN] = "btnAddPurposeOfLoan";
        lokatorsByType[DefineLookupOptionParameters.TYPE_HANDICAPPED] = "btnAddHandicapped";
        lokatorsByType[DefineLookupOptionParameters.TYPE_COLLATERAL_TYPE] = "btnAddCollateralType";
        lokatorsByType[DefineLookupOptionParameters.TYPE_OFFICE_TITLE] = "btnAddOfficerTitle";
        lokatorsByType[DefineLookupOptionParameters.TYPE_PAYMENT_TYPE] = "btnAddPaymentType";
    }

    public void verifyLookupOptions() {
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_SALUTATION));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_USER_TITLE));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_MARITAL_STATUS));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_ETHNICITY));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_EDUCATION_LEVEL));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_CITIZENSHIP));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_BUSINESS_ACTIVITY));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_PURPOSES_OF_LOAN));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_HANDICAPPED));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_COLLATERAL_TYPE));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_OFFICE_TITLE));
        Assert.assertTrue(selenium.isElementPresent(ELEMENT_SELECT_PAYMENT_TYPE));
    }

    public DefineLookupOptionPage navigateToDefineLookupOptionPage(DefineLookupOptionParameters lookupOptionParams) {
        String addLokator = lokatorsByType[lookupOptionParams.getType()];
        selenium.click(addLokator);
        waitForPageToLoad();
        return new DefineLookupOptionPage(selenium);
    }
}
