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

import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterPersonalDataPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.util.StringUtil;

import java.util.Arrays;

public class FormParametersHelper {
    public static DefineNewLoanProductPage.SubmitFormParameters getWeeklyLoanProductParameters() {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setOfferingName("productWeekly" + StringUtil.getRandomString(4));
        formParameters.setOfferingShortName("pw" + StringUtil.getRandomString(2));
        formParameters.setDescription("descriptionForWeekly1");
        formParameters.setCategory("Other");
        formParameters.setApplicableFor(DefineNewLoanProductPage.SubmitFormParameters.CLIENTS);
        formParameters.setMinLoanAmount("100");
        formParameters.setMaxLoanAmount("190000");
        formParameters.setDefaultLoanAmount("2500");
        formParameters.setInterestTypes(DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE);
        formParameters.setMaxInterestRate("30");
        formParameters.setMinInterestRate("10");
        formParameters.setDefaultInterestRate("19");
        /* This parameter expects Weeks or Months */
        formParameters.setFreqOfInstallments(DefineNewLoanProductPage.SubmitFormParameters.WEEKS);
        formParameters.setMaxInstallments("52");
        formParameters.setDefInstallments("52");
        formParameters.setGracePeriodType(DefineNewLoanProductPage.SubmitFormParameters.NONE);
        formParameters.setInterestGLCode("31102 - Penalty");
        formParameters.setPrincipalGLCode("1506 - Managed WFLoan");
        return formParameters;
    }

    public static DefineNewLoanProductPage.SubmitFormParameters getWeeklyLoanProductParametersWithInstallmentsSetsByLastLoanAmount() {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setCalculateInstallments(DefineNewLoanProductPage.SubmitFormParameters.BY_LAST_LOAN_AMOUNT);
        String[][] installmentsByLastLoanAmount = {
                {"1000", "5", "10", "5"},
                {"2000", "10", "20", "15"},
                {"3000", "10", "30", "25"},
                {"4000", "20", "50", "30"},
                {"5000", "20", "50", "35"},
                {"6000", "30", "60", "40"}

        };
        formParameters.setInstallmentsByLastLoanAmount(installmentsByLastLoanAmount);
        return formParameters;
    }

    public static DefineNewLoanProductPage.SubmitFormParameters getMonthlyLoanProductParameters() {
        DefineNewLoanProductPage.SubmitFormParameters formParameters = new DefineNewLoanProductPage.SubmitFormParameters();
        formParameters.setOfferingName("productMonthly" + StringUtil.getRandomString(4));
        formParameters.setOfferingShortName("pm" + StringUtil.getRandomString(2));
        formParameters.setDescription("descriptionForMonthly1");
        formParameters.setCategory("Other");
        formParameters.setApplicableFor(DefineNewLoanProductPage.SubmitFormParameters.CLIENTS);
        formParameters.setMinLoanAmount("1007");
        formParameters.setMaxLoanAmount("190000");
        formParameters.setDefaultLoanAmount("60000");
        formParameters.setInterestTypes(DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE);
        formParameters.setMaxInterestRate("30");
        formParameters.setMinInterestRate("10");
        formParameters.setDefaultInterestRate("12");
        /* This parameter expects Weeks or Months */
        formParameters.setFreqOfInstallments(DefineNewLoanProductPage.SubmitFormParameters.MONTHS);
        formParameters.setMaxInstallments("72");
        formParameters.setDefInstallments("60");
        formParameters.setGracePeriodType(DefineNewLoanProductPage.SubmitFormParameters.NONE);
        formParameters.setInterestGLCode("31102 - Penalty");
        formParameters.setPrincipalGLCode("1506 - Managed WFLoan");
        return formParameters;
    }

    public static CreateClientEnterPersonalDataPage.SubmitFormParameters getClientEnterPersonalDataPageFormParameters() {
        CreateClientEnterPersonalDataPage.SubmitFormParameters formParameters = new CreateClientEnterPersonalDataPage.SubmitFormParameters();
        formParameters.setSalutation(CreateClientEnterPersonalDataPage.SubmitFormParameters.MRS);
        formParameters.setFirstName("test");
        formParameters.setLastName("Customer" + StringUtil.getRandomString(8));
        formParameters.setDateOfBirthDD("22");
        formParameters.setDateOfBirthMM("05");
        formParameters.setDateOfBirthYYYY("1987");
        formParameters.setGender(CreateClientEnterPersonalDataPage.SubmitFormParameters.FEMALE);
        formParameters.setPovertyStatus(CreateClientEnterPersonalDataPage.SubmitFormParameters.POOR);
        formParameters.setHandicapped("Yes");
        formParameters.setSpouseNameType(CreateClientEnterPersonalDataPage.SubmitFormParameters.FATHER);
        formParameters.setSpouseFirstName("father");
        formParameters.setSpouseLastName("lastname" + StringUtil.getRandomString(8));
        return formParameters;
    }

    public static FeesCreatePage.SubmitFormParameters getCreatePeriodicFeesParameters() {
        FeesCreatePage.SubmitFormParameters formParameters = new FeesCreatePage.SubmitFormParameters();
        formParameters.setFeeName(StringUtil.getRandomString(5));
        formParameters.setCategoryType("Group");
        formParameters.setDefaultFees(false);
        formParameters.setFeeFrequencyType(formParameters.PERIODIC_FEE_FREQUENCY);
        formParameters.setFeeRecurrenceType(formParameters.MONTHLY_FEE_RECURRENCE);
        formParameters.setMonthRecurAfter(2);
        formParameters.setAmount(6);
        formParameters.setGlCode("6201 - Miscelleneous Income");
        return formParameters;
    }

    public static DefineNewLoanProductPage.SubmitFormParameters getWeeklyLoanProductParametersWithQuestionGroups(String... questionGroupTitle) {
        DefineNewLoanProductPage.SubmitFormParameters weeklyLoanProductParameters = getWeeklyLoanProductParameters();
        weeklyLoanProductParameters.setQuestionGroups(Arrays.asList(questionGroupTitle));
        return weeklyLoanProductParameters;
    }

    public static FeesCreatePage.SubmitFormParameters getOneTimeLoanMultiCurrencyFeesParameters() {
        FeesCreatePage.SubmitFormParameters formParameters = new FeesCreatePage.SubmitFormParameters();
        formParameters.setFeeName(StringUtil.getRandomString(5));
        formParameters.setCategoryType("Loans");
        formParameters.setDefaultFees(false);
        formParameters.setFeeFrequencyType(formParameters.ONETIME_FEE_FREQUENCY);
        formParameters.setCustomerCharge("Upfront");
        formParameters.setAmount(10);
        formParameters.setCurrency(FeesCreatePage.SubmitFormParameters.USD);
        formParameters.setGlCode("31301 - Fees");
        return formParameters;
    }
}
