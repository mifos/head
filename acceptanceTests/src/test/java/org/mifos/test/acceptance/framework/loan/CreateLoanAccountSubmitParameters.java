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

package org.mifos.test.acceptance.framework.loan;

@SuppressWarnings("PMD.TooManyFields")
public class CreateLoanAccountSubmitParameters {
    private String amount;
    private String lsimFrequencyWeeks;
    private String lsimFrequencyMonths;
    private String lsimWeekFrequency;
    private String lsimWeekDay;
    private String lsimDayOfMonth;
    private String lsimMonthTypeDayOfMonth;
    private String lsimMonthTypeNthWeekdayOfMonth;
    private String lsimMonthRank;
    private boolean gracePeriodTypeNone;
    private String interestRate;
    private String numberOfInstallments;
    private String dd;
    private String mm;
    private String yy;
    private String additionalFee1;
    private String additionalFee2;
    private String additionalFee3;
    private String loanPurpose;


    public String getLoanPurpose() {
		return loanPurpose;
	}

	public void setLoanPurpose(String loanPurpose) {
		this.loanPurpose = loanPurpose;
	}

	public String getInterestRate() {
        return this.interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public String getNumberOfInstallments() {
        return this.numberOfInstallments;
    }

    public void setNumberOfInstallments(String numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
    public String getLsimFrequencyWeeks() {
        return this.lsimFrequencyWeeks;
    }

    public void setLsimFrequencyWeeks(String lsimFrequencyWeeks) {
        this.lsimFrequencyWeeks = lsimFrequencyWeeks;
    }

    public String getLsimFrequencyMonths() {
        return this.lsimFrequencyMonths;
    }

    public void setLsimFrequencyMonths(String lsimFrequencyMonths) {
        this.lsimFrequencyMonths = lsimFrequencyMonths;
    }

    public String getLsimWeekFrequency() {
        return this.lsimWeekFrequency;
    }

    public void setLsimWeekFrequency(String lsimWeekFrequency) {
        this.lsimWeekFrequency = lsimWeekFrequency;
    }

    public String getLsimWeekDay() {
        return this.lsimWeekDay;
    }

    public void setLsimWeekDay(String lsimWeekDay) {
        this.lsimWeekDay = lsimWeekDay;
    }

    public String getLsimDayOfMonth() {
        return this.lsimDayOfMonth;
    }

    public void setLsimDayOfMonth(String lsimDayOfMonth) {
        this.lsimDayOfMonth = lsimDayOfMonth;
    }

    public String getLsimMonthTypeDayOfMonth() {
        return this.lsimMonthTypeDayOfMonth;
    }

    public void setLsimMonthTypeDayOfMonth(String lsimMonthTypeDayOfMonth) {
        this.lsimMonthTypeDayOfMonth = lsimMonthTypeDayOfMonth;
    }

    public String getLsimMonthTypeNthWeekdayOfMonth() {
        return this.lsimMonthTypeNthWeekdayOfMonth;
    }

    public void setLsimMonthTypeNthWeekdayOfMonth(String lsimMonthTypeNthWeekdayOfMonth) {
        this.lsimMonthTypeNthWeekdayOfMonth = lsimMonthTypeNthWeekdayOfMonth;
    }

    public String getLsimMonthRank() {
        return this.lsimMonthRank;
    }

    public void setLsimMonthRank(String lsimMonthRank) {
        this.lsimMonthRank = lsimMonthRank;
    }

    public boolean isGracePeriodTypeNone() {
        return gracePeriodTypeNone;
    }

    public void setGracePeriodTypeNone(boolean gracePeriodTypeNone) {
        this.gracePeriodTypeNone = gracePeriodTypeNone;
    }

    public String getDd() {
        return this.dd;
    }

    public void setDd(String dd) {
        this.dd = dd;
    }

    public String getMm() {
        return this.mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
    }

    public String getYy() {
        return this.yy;
    }

    public void setYy(String yy) {
        this.yy = yy;
    }

    public String getAdditionalFee1() {
        return this.additionalFee1;
    }

    public void setAdditionalFee1(String additionalFee1) {
        this.additionalFee1 = additionalFee1;
    }

    public String getAdditionalFee2() {
        return this.additionalFee2;
    }

    public void setAdditionalFee2(String additionalFee2) {
        this.additionalFee2 = additionalFee2;
    }

    public String getAdditionalFee3() {
        return this.additionalFee3;
    }

    public void setAdditionalFee3(String additionalFee3) {
        this.additionalFee3 = additionalFee3;
    }

}
