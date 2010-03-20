/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

@SuppressWarnings("PMD.TooManyFields")
public class SavingsProductParameters {
    private String productInstanceName;
    private String shortName;
    private int productCategory;
    private String startDateDD;
    private String startDateMM;
    private String startDateYYYY;
    private int applicableFor;
    private int typeOfDeposits;
    private String mandatoryAmount;
    private int amountAppliesTo;
    private String interestRate;
    private int balanceUsedForInterestCalculation;
    private int daysOrMonthsForInterestCalculation;
    private String numberOfDaysOrMonthsForInterestCalculation;
    private String frequencyOfInterestPostings;
    private String glCodeForDeposit;
    private String glCodeForInterest;

    // constants:
    // product category
    public static final int OTHER = 2;

    // applicable for
    public static final int CLIENTS = 1;
    public static final int GROUPS = 2;
    public static final int CENTERS = 3;

    // type of deposits
    public static final int MANDATORY = 1;
    public static final int VOLUNTARY = 2;

    // balance used for interest calculation
    public static final int MINIMUM_BALANCE = 1;
    public static final int AVERAGE_BALANCE = 2;

    // days or months for interest calculation
    public static final int MONTHS = 2;
    public static final int DAYS = 3;

    // the amount applies to either the whole group or per individual
    public static final int PER_INDIVIDUAL = 1;
    public static final int WHOLE_GROUP = 2;

    public String getProductInstanceName() {
        return this.productInstanceName;
    }
    public void setProductInstanceName(String productInstanceName) {
        this.productInstanceName = productInstanceName;
    }
    public String getShortName() {
        return this.shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    public int getProductCategory() {
        return this.productCategory;
    }
    public void setProductCategory(int productCategory) {
        this.productCategory = productCategory;
    }
    public String getStartDateDD() {
        return this.startDateDD;
    }
    public void setStartDateDD(String startDateDD) {
        this.startDateDD = startDateDD;
    }
    public String getStartDateMM() {
        return this.startDateMM;
    }
    public void setStartDateMM(String startDateMM) {
        this.startDateMM = startDateMM;
    }
    public String getStartDateYYYY() {
        return this.startDateYYYY;
    }
    public void setStartDateYYYY(String startDateYYYY) {
        this.startDateYYYY = startDateYYYY;
    }
    public int getApplicableFor() {
        return this.applicableFor;
    }
    public void setApplicableFor(int applicableFor) {
        this.applicableFor = applicableFor;
    }
    public int getTypeOfDeposits() {
        return this.typeOfDeposits;
    }
    public void setTypeOfDeposits(int typeOfDeposits) {
        this.typeOfDeposits = typeOfDeposits;
    }
    public String getMandatoryAmount() {
        return this.mandatoryAmount;
    }
    public void setMandatoryAmount(String mandatoryAmount) {
        this.mandatoryAmount = mandatoryAmount;
    }
    public String getInterestRate() {
        return this.interestRate;
    }
    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }
    public int getBalanceUsedForInterestCalculation() {
        return this.balanceUsedForInterestCalculation;
    }
    public void setBalanceUsedForInterestCalculation(int balanceUsedForInterestCalculation) {
        this.balanceUsedForInterestCalculation = balanceUsedForInterestCalculation;
    }
    public int getDaysOrMonthsForInterestCalculation() {
        return this.daysOrMonthsForInterestCalculation;
    }
    public void setDaysOrMonthsForInterestCalculation(int daysOrMonthsForInterestCalculation) {
        this.daysOrMonthsForInterestCalculation = daysOrMonthsForInterestCalculation;
    }
    public String getNumberOfDaysOrMonthsForInterestCalculation() {
        return this.numberOfDaysOrMonthsForInterestCalculation;
    }
    public void setNumberOfDaysOrMonthsForInterestCalculation(String numberOfDaysOrMonthsForInterestCalculation) {
        this.numberOfDaysOrMonthsForInterestCalculation = numberOfDaysOrMonthsForInterestCalculation;
    }
    public String getFrequencyOfInterestPostings() {
        return this.frequencyOfInterestPostings;
    }
    public void setFrequencyOfInterestPostings(String frequencyOfInterestPostings) {
        this.frequencyOfInterestPostings = frequencyOfInterestPostings;
    }
    public String getGlCodeForDeposit() {
        return this.glCodeForDeposit;
    }
    public void setGlCodeForDeposit(String glCodeForDeposit) {
        this.glCodeForDeposit = glCodeForDeposit;
    }
    public String getGlCodeForInterest() {
        return this.glCodeForInterest;
    }
    public void setGlCodeForInterest(String glCodeForInterest) {
        this.glCodeForInterest = glCodeForInterest;
    }
    public void setAmountAppliesTo(int amountAppliesTo) {
        this.amountAppliesTo = amountAppliesTo;
    }
    public int getAmountAppliesTo() {
        return amountAppliesTo;
    }
}
