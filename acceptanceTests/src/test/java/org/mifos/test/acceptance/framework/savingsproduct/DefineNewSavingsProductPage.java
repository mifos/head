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

package org.mifos.test.acceptance.framework.savingsproduct;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class DefineNewSavingsProductPage extends MifosPage {
    public DefineNewSavingsProductPage(Selenium selenium) {
        super(selenium);
        verifyPage("CreateSavingsProduct");
    }

    @SuppressWarnings("PMD.TooManyFields")
    // lots of fields ok for form input case
    public static class SubmitSavingsFormParameters {

        // applicable for
        public static final int CLIENTS = 1;
        public static final int GROUPS = 2;

        // Status
        public static final int ACTIVE = 2;
        public static final int INACTIVE = 5;

        // product category
        public static final int OTHER = 2;

        //type of deposits
        public static final int MANDATORY = 1;
        public static final int VOLUNTARY = 2;

        //Balance used for Interest calculation
        public static final int MINIMUM_BALANCE = 1;
        public static final int AVERAGE_BALANCE = 2;

        //Time period for Interest calculation
        public static final int MONTHS = 2;
        public static final int DAYS = 3;

        private String offeringName;
        private String offeringShortName;
        private String description;
        private int productCategory;
        private String startDateDd;
        private String startDateMm;
        private String startDateYy;
        private int applicableFor;
        private int depositType;
        private String depositAmount;
        private int status;
        private String InterestRate;
        private int balanceInterest;
        private int timePeriodInterestType;
        private String timePeriodInterest;
        private String FrequencyInterest;
        public String getOfferingName() {
            return this.offeringName;
        }
        public void setOfferingName(String offeringName) {
            this.offeringName = offeringName;
        }
        public String getOfferingShortName() {
            return this.offeringShortName;
        }
        public void setOfferingShortName(String offeringShortName) {
            this.offeringShortName = offeringShortName;
        }
        public String getDescription() {
            return this.description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public int getProductCategory() {
            return this.productCategory;
        }
        public void setProductCategory(int productCategory) {
            this.productCategory = productCategory;
        }
        public String getStartDateDd() {
            return this.startDateDd;
        }
        public void setStartDateDd(String startDateDd) {
            this.startDateDd = startDateDd;
        }
        public String getStartDateMm() {
            return this.startDateMm;
        }
        public void setStartDateMm(String startDateMm) {
            this.startDateMm = startDateMm;
        }
        public String getStartDateYy() {
            return this.startDateYy;
        }
        public void setStartDateYy(String startDateYy) {
            this.startDateYy = startDateYy;
        }
        public int getApplicableFor() {
            return this.applicableFor;
        }
        public void setApplicableFor(int applicableFor) {
            this.applicableFor = applicableFor;
        }
        public int getDepositType() {
            return this.depositType;
        }
        public void setDepositType(int depositType) {
            this.depositType = depositType;
        }
        public String getDepositAmount() {
            return this.depositAmount;
        }
        public void setDepositAmount(String mandatoryAmount) {
            this.depositAmount = mandatoryAmount;
        }
        public int getStatus() {
            return this.status;
        }
        public void setStatus(int status) {
            this.status = status;
        }
        public String getInterestRate() {
            return this.InterestRate;
        }
        public void setInterestRate(String interestRate) {
            this.InterestRate = interestRate;
        }
        public int getBalanceInterest() {
            return this.balanceInterest;
        }
        public void setBalanceInterest(int balanceInterest) {
            this.balanceInterest = balanceInterest;
        }
        public int getTimePeriodInterestType() {
            return this.timePeriodInterestType;
        }
        public void setTimePeriodInterestType(int timePeriodInterestType) {
            this.timePeriodInterestType = timePeriodInterestType;
        }
        public String getTimePeriodInterest() {
            return this.timePeriodInterest;
        }
        public void setTimePeriodInterest(String timePeriodInterest) {
            this.timePeriodInterest = timePeriodInterest;
        }
        public String getFrequencyInterest() {
            return this.FrequencyInterest;
        }
        public void setFrequencyInterest(String frequencyInterest) {
            this.FrequencyInterest = frequencyInterest;
        }


    }
    public DefineNewSavingsProductPreviewPage submitAndNavigateToDefineNewSavingsProductPreviewPage(SavingsProductParameters productParameters) {
        selenium.type("CreateSavingsProduct.input.prdOfferingName", productParameters.getProductInstanceName());
        selenium.type("CreateSavingsProduct.input.prdOfferingShortName", productParameters.getShortName());
        selenium.type("startDateDD", productParameters.getStartDateDD());
        selenium.type("startDateMM", productParameters.getStartDateMM());
        selenium.type("startDateYY", productParameters.getStartDateYYYY());

        selectValueIfNotZero("generalDetails.selectedCategory", productParameters.getProductCategory());

        selectValueIfNotZero("generalDetails.selectedApplicableFor", productParameters.getApplicableFor());
        selectValueIfNotZero("selectedDepositType", productParameters.getTypeOfDeposits());
        typeTextIfNotEmpty("amountForDeposit", productParameters.getMandatoryAmount());
        selectValueIfNotZero("selectedGroupSavingsApproach", productParameters.getAmountAppliesTo());
        selenium.type("interestRate", productParameters.getInterestRate());

        selectValueIfNotZero("selectedInterestCalculation", productParameters.getBalanceUsedForInterestCalculation());

        selenium.type("interestCalculationFrequency", productParameters.getNumberOfDaysOrMonthsForInterestCalculation());
        selectValueIfNotZero("selectedFequencyPeriod", productParameters.getDaysOrMonthsForInterestCalculation());

        selenium.type("interestPostingMonthlyFrequency", productParameters.getFrequencyOfInterestPostings());

        selectIfNotEmpty("selectedPrincipalGlCode", productParameters.getGlCodeForDeposit());
        selectIfNotEmpty("selectedInterestGlCode", productParameters.getGlCodeForInterest());

        selenium.click("CreateSavingsProduct.button.preview");
        waitForPageToLoad();

        return new DefineNewSavingsProductPreviewPage(selenium);
    }
}
