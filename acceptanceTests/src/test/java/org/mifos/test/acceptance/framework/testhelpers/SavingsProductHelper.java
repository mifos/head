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

package org.mifos.test.acceptance.framework.testhelpers;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductConfirmationPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductPage;
import org.mifos.test.acceptance.framework.savingsproduct.SavingsProductParameters;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.util.StringUtil;

public class SavingsProductHelper {
    private final NavigationHelper navigationHelper;

    public SavingsProductHelper(Selenium selenium) {
        navigationHelper = new NavigationHelper(selenium);
    }

    public DefineNewSavingsProductConfirmationPage createSavingsProduct(SavingsProductParameters productParameters) {
        DefineNewSavingsProductConfirmationPage confirmationPage = navigationHelper
            .navigateToAdminPage()
            .navigateToDefineSavingsProduct()
            .submitAndNavigateToDefineNewSavingsProductPreviewPage(productParameters)
            .submitAndNavigateToDefineNewSavingsProductConfirmationPage();

        return confirmationPage;
    }
    
    public DefineNewSavingsProductPage getDefineSavingsProductPageWithValidationErrors(SavingsProductParameters productParameters){
        return navigationHelper
            .navigateToAdminPage()
            .navigateToDefineSavingsProduct().submitWithValidationErrors(productParameters);       
    }

    /**
     * This method return a fully useable parameter object for the type of deposit (mandatory/voluntary)
     * and applicable for (clients/groups/centers).
     * @return Parameters like noted above.
     * @param typeOfDeposits
     * @param applicableFor
     */
    public SavingsProductParameters getGenericSavingsProductParameters(int typeOfDeposits, int applicableFor) {
        SavingsProductParameters params = new SavingsProductParameters();

        params.setProductInstanceName("Savings product test" + StringUtil.getRandomString(3));
        params.setShortName(StringUtil.getRandomString(4));
        params.setProductCategory(SavingsProductParameters.OTHER);
        DateTime today = new DateTime();
        params.setStartDateDD(Integer.valueOf(today.getDayOfMonth()).toString());
        params.setStartDateMM(Integer.valueOf(today.getMonthOfYear()).toString());
        params.setStartDateYYYY(Integer.valueOf(today.getYearOfEra()).toString());

        params.setApplicableFor(applicableFor);
        params.setTypeOfDeposits(typeOfDeposits);

        // these two settings are not required in all configurations
        // but they're good to have anyway
        params.setMandatoryAmount("10");
        params.setAmountAppliesTo(SavingsProductParameters.WHOLE_GROUP);

        params.setInterestRate("4");
        params.setBalanceUsedForInterestCalculation(SavingsProductParameters.AVERAGE_BALANCE);
        params.setDaysOrMonthsForInterestCalculation(SavingsProductParameters.MONTHS);
        params.setNumberOfDaysOrMonthsForInterestCalculation("3");
        params.setFrequencyOfInterestPostings("6");

        params.setGlCodeForDeposit("24101");
        params.setGlCodeForInterest("41102");

        return params;
    }

    public SavingsProductParameters getMandatoryClientsMinimumBalanceSavingsProductParameters()
    {
        SavingsProductParameters params = getGenericSavingsProductParameters(SavingsProductParameters.MANDATORY, SavingsProductParameters.CLIENTS);
        params.setDaysOrMonthsForInterestCalculation(params.DAYS);
        params.setInterestRate("1");
        params.setFrequencyOfInterestPostings("1");
        params.setNumberOfDaysOrMonthsForInterestCalculation("1");
        params.setBalanceUsedForInterestCalculation(SavingsProductParameters.MINIMUM_BALANCE);
        params.setMandatoryAmount("100000");
        return params;
    }

    public SavingsProductParameters getVoluntaryClients3MonthCalculactionPostingProductParameters()
    {
        SavingsProductParameters params = getGenericSavingsProductParameters(SavingsProductParameters.VOLUNTARY, SavingsProductParameters.CLIENTS);
        params.setDaysOrMonthsForInterestCalculation(params.MONTHS);
        params.setInterestRate("5");
        params.setFrequencyOfInterestPostings("3");
        params.setNumberOfDaysOrMonthsForInterestCalculation("3");
        params.setBalanceUsedForInterestCalculation(SavingsProductParameters.MINIMUM_BALANCE);
        params.setMandatoryAmount("100000");
        params.setStartDateDD("15");
        params.setStartDateMM("2");
        params.setStartDateYYYY("2011");
        return params;
    }
    
    public SavingsProductParameters getInvalidSavingsProductParameters(int typeOfDeposits, int applicableFor){
        SavingsProductParameters params = this.getGenericSavingsProductParameters(typeOfDeposits, applicableFor);
        params.setNumberOfDaysOrMonthsForInterestCalculation("0");
        params.setFrequencyOfInterestPostings("0");
        params.setAmountAppliesTo(0);
        params.setMandatoryAmount("0.0");
        params.setInterestRate("120");
        return params;
    }
}
